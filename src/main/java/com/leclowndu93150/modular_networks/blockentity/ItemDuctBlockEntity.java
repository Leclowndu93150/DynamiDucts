package com.leclowndu93150.modular_networks.blockentity;

import com.leclowndu93150.modular_networks.MNConfig;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.energy.EnergyDuctUnit;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import com.leclowndu93150.modular_networks.init.MNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemDuctBlockEntity extends DuctBlockEntity {

    private final Tier tier;
    private final boolean opaque;

    public ItemDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier tier, boolean opaque) {
        super(type, pos, state);
        this.tier = tier;
        this.opaque = opaque;
        initDuctUnits();
    }

    @Override
    protected void initDuctUnits() {
        int speed = tier.isFast() ? MNConfig.itemFastSpeed : MNConfig.itemBaseSpeed;
        addDuctUnit(new ItemDuctUnit(this, speed, !opaque));

        if (tier.hasEnergy()) {
            int[] rates = MNConfig.energyTransferRates();
            int[] caps = MNConfig.energyCapacities();
            addDuctUnit(new EnergyDuctUnit(this, rates[3], caps[3]));
        }
    }

    public IItemHandler getItemCapability(Direction side) {
        var unit = getDuctUnit(DuctToken.ITEM);
        if (unit instanceof ItemDuctUnit itemUnit) {
            return itemUnit.createCapability(side);
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
        BASIC(false, false),
        FAST(false, true),
        ENERGY(true, false),
        ENERGY_FAST(true, true);

        private final boolean hasEnergy;
        private final boolean fast;

        Tier(boolean hasEnergy, boolean fast) {
            this.hasEnergy = hasEnergy;
            this.fast = fast;
        }

        public boolean hasEnergy() {
            return hasEnergy;
        }

        public boolean isFast() {
            return fast;
        }

        public ItemDuctBlockEntity createBlockEntity(BlockPos pos, BlockState state, boolean opaque) {
            BlockEntityType<?> type = switch (this) {
                case BASIC -> opaque ? MNBlockEntities.ITEM_DUCT_BASIC_OPAQUE.get() : MNBlockEntities.ITEM_DUCT_BASIC.get();
                case FAST -> opaque ? MNBlockEntities.ITEM_DUCT_FAST_OPAQUE.get() : MNBlockEntities.ITEM_DUCT_FAST.get();
                case ENERGY -> opaque ? MNBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get() : MNBlockEntities.ITEM_DUCT_ENERGY.get();
                case ENERGY_FAST -> opaque ? MNBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get() : MNBlockEntities.ITEM_DUCT_ENERGY_FAST.get();
            };
            return new ItemDuctBlockEntity(type, pos, state, this, opaque);
        }
    }
}
