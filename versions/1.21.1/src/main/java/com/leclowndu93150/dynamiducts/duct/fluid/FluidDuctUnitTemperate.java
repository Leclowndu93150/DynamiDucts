package com.leclowndu93150.dynamiducts.duct.fluid;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidDuctUnitTemperate extends FluidDuctUnit {

    private static final int ROOM_TEMPERATURE = 300;
    private static final int FREEZING_TEMPERATURE = 250;
    private static final int MELTING_TEMPERATURE = 800;
    private static final float TEMPERATURE_LERP = 0.0005F;
    private static final int BREAK_CHANCE = 50;

    private float internalTemperature = ROOM_TEMPERATURE;

    public FluidDuctUnitTemperate(DuctBlockEntity parent, int capacityPerDuct, int throughputPerDuct, boolean transparent) {
        super(parent, capacityPerDuct, throughputPerDuct, transparent);
    }

    public void tickTemperature() {
        Level level = getParent().getLevel();
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (grid == null) return;

        FluidStack fluid = grid.getTank().getFluid();
        if (fluid.isEmpty()) {
            internalTemperature += (ROOM_TEMPERATURE - internalTemperature) * TEMPERATURE_LERP;
            return;
        }

        int fluidTemp = fluid.getFluid().getFluidType().getTemperature(fluid);
        internalTemperature += (fluidTemp - internalTemperature) * TEMPERATURE_LERP;

        if (internalTemperature < FREEZING_TEMPERATURE || internalTemperature > MELTING_TEMPERATURE) {
            if (serverLevel.random.nextInt(BREAK_CHANCE) == 0) {
                shatter(serverLevel, fluid);
            }
        }
    }

    public boolean isOverheating() {
        return internalTemperature > MELTING_TEMPERATURE;
    }

    public boolean isFreezing() {
        return internalTemperature < FREEZING_TEMPERATURE;
    }

    private void shatter(ServerLevel level, FluidStack fluid) {
        BlockPos pos = getPos();

        Block block = getParent().getBlockState().getBlock();
        for (ItemStack drop : Block.getDrops(getParent().getBlockState(), level, pos, getParent())) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            level.addFreshEntity(entity);
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, Level.ExplosionInteraction.NONE);

        if (level.random.nextInt(6) == 0) {
            Block fluidBlock = fluid.getFluid().defaultFluidState().createLegacyBlock().getBlock();
            if (fluidBlock instanceof LiquidBlock && level.getBlockState(pos).isAir()) {
                if (fluid.getFluid() == Fluids.WATER && level.dimensionType().ultraWarm()) {
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
                } else {
                    level.setBlock(pos, fluid.getFluid().defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
                    level.scheduleTick(pos, fluid.getFluid(), 10 + level.random.nextInt(30));
                }
            }
        }
    }

    public void spawnSmokeParticles(ServerLevel level) {
        if (!isOverheating()) return;
        BlockPos pos = getPos();
        level.sendParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5 + level.random.nextGaussian() * 0.2,
                pos.getY() + 0.5 + level.random.nextGaussian() * 0.2,
                pos.getZ() + 0.5 + level.random.nextGaussian() * 0.2,
                1, 0, 0.05, 0, 0.01);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("Temperature", internalTemperature);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("Temperature")) {
            internalTemperature = tag.getFloat("Temperature");
        }
    }
}
