package com.leclowndu93150.modular_networks.block;

import com.leclowndu93150.modular_networks.blockentity.TransportDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return tier.createBlockEntity(pos, state);
    }
}
