package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.TransportDuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.menu.TransportMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransportDuctBlock extends DuctBlock {

    public static final MapCodec<TransportDuctBlock> CODEC = simpleCodec(props -> new TransportDuctBlock(props, TransportDuctBlockEntity.Tier.BASIC));
    private static final DuctToken[] TOKENS = {DuctToken.TRANSPORT};
    private final TransportDuctBlockEntity.Tier tier;

    public TransportDuctBlock(Properties properties, TransportDuctBlockEntity.Tier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected MapCodec<? extends DuctBlock> codec() {
        return CODEC;
    }

    @Override
    public DuctToken[] getDuctTokens() {
        return TOKENS;
    }

    public TransportDuctBlockEntity.Tier getTier() {
        return tier;
    }

    @Override
    protected VoxelShape[] getShapeCache() {
        return SHAPE_TRANSPORT;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return tier.createBlockEntity(pos, state);
    }

    @Override
    protected boolean canRenderConnection(net.minecraft.world.level.LevelAccessor level, BlockPos pos, net.minecraft.core.Direction direction) {
        if (level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            if (ductBE.getConnectionType(direction) == com.leclowndu93150.dynamiducts.core.network.ConnectionType.FORCED) {
                return true;
            }
        }
        return super.canRenderConnection(level, pos, direction);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
            if (ductBE.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit unit && unit.isEndpoint()) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new TransportMenu(id, inv, ductBE, unit),
                            Component.translatable("gui.dynamiducts.transport.title")
                    ), buf -> TransportMenu.writeScreenData(buf, unit, pos));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
