package com.leclowndu93150.modular_networks.block;

import com.leclowndu93150.modular_networks.blockentity.FluidDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public class FluidDuctBlock extends DuctBlock {

    public static final MapCodec<FluidDuctBlock> CODEC = simpleCodec(props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.BASIC, false));

    private final FluidDuctBlockEntity.Tier tier;
    private final boolean opaque;
    private final DuctToken[] tokens;

    public FluidDuctBlock(Properties properties, FluidDuctBlockEntity.Tier tier, boolean opaque) {
        super(properties);
        this.tier = tier;
        this.opaque = opaque;
        this.tokens = tier.hasEnergy() ? new DuctToken[]{DuctToken.FLUID, DuctToken.ENERGY} : new DuctToken[]{DuctToken.FLUID};
    }

    @Override
    protected MapCodec<? extends DuctBlock> codec() {
        return CODEC;
    }

    @Override
    public DuctToken[] getDuctTokens() {
        return tokens;
    }

    public FluidDuctBlockEntity.Tier getTier() {
        return tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return tier.createBlockEntity(pos, state, opaque);
    }

    @Override
    protected boolean canConnectToExternal(LevelAccessor level, BlockPos pos, Direction direction, BlockPos neighborPos) {
        if (level instanceof Level realLevel) {
            if (realLevel.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, direction.getOpposite()) != null) return true;
            return tier.hasEnergy() && realLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite()) != null;
        }
        return false;
    }
}
