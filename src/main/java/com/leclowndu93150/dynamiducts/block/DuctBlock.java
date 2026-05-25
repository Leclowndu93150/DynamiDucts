package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.attachment.cover.Cover;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.menu.AttachmentMenu;
import com.leclowndu93150.dynamiducts.menu.RelayMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public abstract class DuctBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH,
            Direction.EAST, EAST,
            Direction.SOUTH, SOUTH,
            Direction.WEST, WEST,
            Direction.UP, UP,
            Direction.DOWN, DOWN
    );

    // Normal ducts: W=3/16, center 5-11px, arms 5-11px
    protected static final VoxelShape[] SHAPE_SMALL = buildShapeCache(5, 11, 5, 11);
    // Bronze frame ducts (cryo, superconductor): frame radius 0.375 = 6/16, center 5-11px, arms 2-14px
    protected static final VoxelShape[] SHAPE_LARGE = buildShapeCache(2, 14, 5, 11);
    // Transport ducts (viaducts): radius ~0.5, center 1-15px, arms 1-15px
    protected static final VoxelShape[] SHAPE_TRANSPORT = buildShapeCache(1, 15, 1, 15);

    private static VoxelShape[] buildShapeCache(double armMin, double armMax, double centerMin, double centerMax) {
        VoxelShape center = Block.box(centerMin, centerMin, centerMin, centerMax, centerMax, centerMax);
        VoxelShape[] arms = new VoxelShape[6];
        arms[Direction.DOWN.ordinal()] = Block.box(armMin, 0, armMin, armMax, centerMin, armMax);
        arms[Direction.UP.ordinal()] = Block.box(armMin, centerMax, armMin, armMax, 16, armMax);
        arms[Direction.NORTH.ordinal()] = Block.box(armMin, armMin, 0, armMax, armMax, centerMin);
        arms[Direction.SOUTH.ordinal()] = Block.box(armMin, armMin, centerMax, armMax, armMax, 16);
        arms[Direction.WEST.ordinal()] = Block.box(0, armMin, armMin, centerMin, armMax, armMax);
        arms[Direction.EAST.ordinal()] = Block.box(centerMax, armMin, armMin, 16, armMax, armMax);

        VoxelShape[] cache = new VoxelShape[64];
        for (int i = 0; i < 64; i++) {
            VoxelShape shape = center;
            for (int j = 0; j < 6; j++) {
                if ((i & (1 << j)) != 0) {
                    shape = Shapes.or(shape, arms[j]);
                }
            }
            cache[i] = shape;
        }
        return cache;
    }

    public VoxelShape[] getShapeCache() {
        return SHAPE_SMALL;
    }

    protected DuctBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected abstract MapCodec<? extends DuctBlock> codec();

    public abstract DuctToken[] getDuctTokens();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getMultipartShape(state, level, pos);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getMultipartShape(state, level, pos);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            Attachment att = ductBE.getAttachment(direction.getOpposite());
            if (att instanceof Relay relay && relay.getType() == Relay.TYPE_REDSTONE_OUTPUT) {
                return relay.getOutputStrength();
            }
        }
        return 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    private int getShapeIndex(BlockState state) {
        int index = 0;
        for (Direction dir : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(dir))) {
                index |= 1 << dir.ordinal();
            }
        }
        return index;
    }

    private VoxelShape getMultipartShape(BlockState state, BlockGetter level, BlockPos pos) {
        int index = getShapeIndex(state);
        if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            for (Direction dir : Direction.values()) {
                Attachment att = ductBE.getAttachment(dir);
                if (att instanceof Cover) {
                    index |= (1 << dir.ordinal());
                } else if (att != null) {
                    index |= (1 << dir.ordinal());
                }
            }
        }
        VoxelShape shape = getShapeCache()[index];
        if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            for (Direction dir : Direction.values()) {
                if (ductBE.getAttachment(dir) instanceof Cover) {
                    shape = Shapes.or(shape, DuctHitHelper.coverShape(dir));
                }
            }
        }
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);
        return updateVisualConnections(level, pos, defaultBlockState())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canRenderConnection(level, pos, direction));
    }

    protected boolean canConnectTo(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (neighborState.getBlock() instanceof DuctBlock otherDuct) {
            return hasCompatibleToken(otherDuct);
        }

        BlockEntity be = level.getBlockEntity(neighborPos);
        if (be instanceof DuctBlockEntity) {
            return false;
        }

        return canConnectToExternal(level, pos, direction, neighborPos);
    }

    protected boolean canRenderConnection(LevelAccessor level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE && !ductBE.getConnectionType(direction).allowsTransfer()) {
            return false;
        }
        return canConnectTo(level, pos, direction);
    }

    public BlockState updateVisualConnections(LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), canRenderConnection(level, pos, dir));
        }
        return state;
    }

    protected boolean hasCompatibleToken(DuctBlock other) {
        for (DuctToken myToken : getDuctTokens()) {
            for (DuctToken otherToken : other.getDuctTokens()) {
                if (myToken == otherToken) return true;
            }
        }
        return false;
    }

    public boolean isCraftingOnly() {
        return false;
    }

    protected boolean canConnectToExternal(LevelAccessor level, BlockPos pos, Direction direction, BlockPos neighborPos) {
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            BlockState updatedState = updateVisualConnections(level, pos, state);
            if (updatedState != state) {
                level.setBlock(pos, updatedState, Block.UPDATE_CLIENTS);
            }

            if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
                ductBE.onNeighborChanged();
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            ductBE.onPlaced();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
                ductBE.onBroken();
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            var hit = DuctHitHelper.resolve(state, ductBE, pos, hitResult);
            if (hit.part() != DuctHitHelper.HitPart.COLLAR) {
                return InteractionResult.PASS;
            }
            Direction side = hit.side();

            Attachment[] attachments = ductBE.getAttachments();
            if (attachments != null && attachments[side.ordinal()] != null) {
                Attachment att = attachments[side.ordinal()];
                if (att instanceof ConnectionBase conn && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new AttachmentMenu(id, inv, ductBE, side, conn),
                            Component.empty()
                    ), buf -> {
                        buf.writeBlockPos(pos);
                        buf.writeByte(side.ordinal());
                        buf.writeVarInt(conn.getTier().index());
                        buf.writeBoolean(conn.isServo());
                        buf.writeBoolean(conn.isFilter());
                        buf.writeBoolean(conn.isRetriever());
                        buf.writeVarInt(conn.getRedstoneMode().ordinal());
                    });
                    return InteractionResult.SUCCESS;
                }
                if (att instanceof Relay relay && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new RelayMenu(id, inv, relay),
                            Component.empty()
                    ), buf -> {
                        buf.writeBlockPos(pos);
                        buf.writeByte(side.ordinal());
                    });
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof DuctBlockEntity ductBE) {
                if (lvl.isClientSide) {
                    DuctBlockEntity.clientTick(lvl, pos, st, ductBE);
                } else {
                    DuctBlockEntity.serverTick(lvl, pos, st, ductBE);
                }
            }
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }
}
