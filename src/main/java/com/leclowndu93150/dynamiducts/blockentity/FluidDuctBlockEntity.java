package com.leclowndu93150.dynamiducts.blockentity;

import com.leclowndu93150.dynamiducts.MNConfig;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.energy.EnergyDuctUnit;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnit;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnitTemperate;
import com.leclowndu93150.dynamiducts.init.DDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidDuctBlockEntity extends DuctBlockEntity {

    private final Tier tier;
    private final boolean opaque;

    public FluidDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier tier, boolean opaque) {
        super(type, pos, state);
        this.tier = tier;
        this.opaque = opaque;
        initDuctUnits();
    }

    @Override
    protected void initDuctUnits() {
        int capacity = MNConfig.fluidBaseCapacity;
        int throughput = MNConfig.fluidBaseThroughput;

        switch (tier) {
            case SUPER -> {
                capacity *= 4;
                throughput = Integer.MAX_VALUE;
            }
            case HARDENED -> {
                capacity *= 2;
                throughput *= 2;
            }
            case ENERGY -> {
                capacity *= 2;
                throughput *= 2;
            }
        }

        if (tier == Tier.BASIC) {
            addDuctUnit(new FluidDuctUnitTemperate(this, capacity, throughput, !opaque));
        } else {
            addDuctUnit(new FluidDuctUnit(this, capacity, throughput, !opaque));
        }

        if (tier.hasEnergy()) {
            int[] rates = MNConfig.energyTransferRates();
            int[] caps = MNConfig.energyCapacities();
            addDuctUnit(new EnergyDuctUnit(this, rates[3], caps[3]));
        }
    }

    public IFluidHandler getFluidCapability(Direction side) {
        var unit = getDuctUnit(DuctToken.FLUID);
        if (unit instanceof FluidDuctUnit fluidUnit) {
            return fluidUnit.createCapability(side);
        }
        return null;
    }

    public IEnergyStorage getEnergyCapability(Direction side) {
        var unit = getDuctUnit(DuctToken.ENERGY);
        if (unit instanceof EnergyDuctUnit energyUnit) {
            return energyUnit.createCapability(side);
        }
        return null;
    }

    public enum Tier {
        BASIC(false),
        HARDENED(false),
        ENERGY(true),
        SUPER(false);

        private final boolean hasEnergy;

        Tier(boolean hasEnergy) {
            this.hasEnergy = hasEnergy;
        }

        public boolean hasEnergy() {
            return hasEnergy;
        }

        public FluidDuctBlockEntity createBlockEntity(BlockPos pos, BlockState state, boolean opaque) {
            BlockEntityType<?> type = switch (this) {
                case BASIC -> opaque ? DDBlockEntities.FLUID_DUCT_BASIC_OPAQUE.get() : DDBlockEntities.FLUID_DUCT_BASIC.get();
                case HARDENED -> opaque ? DDBlockEntities.FLUID_DUCT_HARDENED_OPAQUE.get() : DDBlockEntities.FLUID_DUCT_HARDENED.get();
                case ENERGY -> opaque ? DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get() : DDBlockEntities.FLUID_DUCT_ENERGY.get();
                case SUPER -> opaque ? DDBlockEntities.FLUID_DUCT_SUPER_OPAQUE.get() : DDBlockEntities.FLUID_DUCT_SUPER.get();
            };
            return new FluidDuctBlockEntity(type, pos, state, this, opaque);
        }
    }
}
