package com.leclowndu93150.dynamiducts.block;

import com.leclowndu93150.dynamiducts.blockentity.EnergyDuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

public class EnergyDuctBlock extends DuctBlock {

    public static final MapCodec<EnergyDuctBlock> CODEC = simpleCodec(props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.BASIC));

    private static final DuctToken[] TOKENS = {DuctToken.ENERGY};
    private final EnergyDuctBlockEntity.Tier tier;

    public EnergyDuctBlock(Properties properties, EnergyDuctBlockEntity.Tier tier) {
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

    public EnergyDuctBlockEntity.Tier getTier() {
        return tier;
    }

    @Override
    protected VoxelShape[] getShapeCache() {
        return (tier == EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR || tier == EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR_EMPTY)
                ? SHAPE_LARGE : SHAPE_SMALL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return tier.createBlockEntity(pos, state);
    }

    @Override
    protected boolean canConnectToExternal(LevelAccessor level, BlockPos pos, Direction direction, BlockPos neighborPos) {
        if (level instanceof Level realLevel) {
            return realLevel.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite()) != null;
        }
        return false;
    }
}
