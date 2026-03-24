package com.leclowndu93150.modular_networks.blockentity;

import com.leclowndu93150.modular_networks.MNConfig;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.energy.EnergyDuctUnit;
import com.leclowndu93150.modular_networks.duct.energy.SuperConductorDuctUnit;
import com.leclowndu93150.modular_networks.init.MNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyDuctBlockEntity extends DuctBlockEntity {

    private final Tier tier;

    public EnergyDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier tier) {
        super(type, pos, state);
        this.tier = tier;
        initDuctUnits();
    }

    @Override
    protected void initDuctUnits() {
        if (tier == Tier.SUPERCONDUCTOR) {
            addDuctUnit(new SuperConductorDuctUnit(this));
        } else if (tier.isCraftingItem()) {
            return;
        } else {
            int[] rates = MNConfig.energyTransferRates();
            int[] caps = MNConfig.energyCapacities();
            addDuctUnit(new EnergyDuctUnit(this, rates[tier.index], caps[tier.index]));
        }
    }

    public IEnergyStorage getEnergyCapability(Direction side) {
        var unit = getDuctUnit(DuctToken.ENERGY);
        if (unit instanceof EnergyDuctUnit energyUnit) {
            return energyUnit.createCapability(side);
        }
        return null;
    }

    public enum Tier {
        BASIC(0, false),
        HARDENED(1, false),
        REINFORCED(2, false),
        SIGNALUM(3, false),
        RESONANT(4, false),
        SUPERCONDUCTOR(5, false),
        REINFORCED_EMPTY(2, true),
        SIGNALUM_EMPTY(3, true),
        RESONANT_EMPTY(4, true),
        SUPERCONDUCTOR_EMPTY(5, true);

        final int index;
        private final boolean craftingItem;

        Tier(int index, boolean craftingItem) {
            this.index = index;
            this.craftingItem = craftingItem;
        }

        public boolean isCraftingItem() {
            return craftingItem;
        }

        public EnergyDuctBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            BlockEntityType<?> type = switch (this) {
                case BASIC -> MNBlockEntities.ENERGY_DUCT_BASIC.get();
                case HARDENED -> MNBlockEntities.ENERGY_DUCT_HARDENED.get();
                case REINFORCED -> MNBlockEntities.ENERGY_DUCT_REINFORCED.get();
                case SIGNALUM -> MNBlockEntities.ENERGY_DUCT_SIGNALUM.get();
                case RESONANT -> MNBlockEntities.ENERGY_DUCT_RESONANT.get();
                case SUPERCONDUCTOR -> MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR.get();
                case REINFORCED_EMPTY -> MNBlockEntities.ENERGY_DUCT_REINFORCED_EMPTY.get();
                case SIGNALUM_EMPTY -> MNBlockEntities.ENERGY_DUCT_SIGNALUM_EMPTY.get();
                case RESONANT_EMPTY -> MNBlockEntities.ENERGY_DUCT_RESONANT_EMPTY.get();
                case SUPERCONDUCTOR_EMPTY -> MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY.get();
            };
            return new EnergyDuctBlockEntity(type, pos, state, this);
        }
    }
}
