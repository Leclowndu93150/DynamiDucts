package com.leclowndu93150.modular_networks.block;

import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.attachment.cover.Cover;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.network.ConnectionType;
import com.leclowndu93150.modular_networks.menu.AttachmentMenu;
import com.leclowndu93150.modular_networks.menu.RelayMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public abstract class DuctBlock extends Block implements EntityBlock {

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

    private static final float DUCT_RADIUS = 3.0F / 16.0F;
    private static final VoxelShape CENTER = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape[] ARM_SHAPES = new VoxelShape[6];
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64];

    static {
        ARM_SHAPES[Direction.DOWN.ordinal()] = Block.box(5, 0, 5, 11, 5, 11);
        ARM_SHAPES[Direction.UP.ordinal()] = Block.box(5, 11, 5, 11, 16, 11);
        ARM_SHAPES[Direction.NORTH.ordinal()] = Block.box(5, 5, 0, 11, 11, 5);
        ARM_SHAPES[Direction.SOUTH.ordinal()] = Block.box(5, 5, 11, 11, 11, 16);
        ARM_SHAPES[Direction.WEST.ordinal()] = Block.box(0, 5, 5, 5, 11, 11);
        ARM_SHAPES[Direction.EAST.ordinal()] = Block.box(11, 5, 5, 16, 11, 11);

        for (int i = 0; i < 64; i++) {
            VoxelShape shape = CENTER;
            for (int j = 0; j < 6; j++) {
                if ((i & (1 << j)) != 0) {
                    shape = Shapes.or(shape, ARM_SHAPES[j]);
                }
            }
            SHAPE_CACHE[i] = shape;
        }
    }

    protected DuctBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
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
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getMultipartShape(state, level, pos);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getMultipartShape(state, level, pos);
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
        VoxelShape shape = SHAPE_CACHE[getShapeIndex(state)];
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
        return updateVisualConnections(level, pos, defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
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
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
