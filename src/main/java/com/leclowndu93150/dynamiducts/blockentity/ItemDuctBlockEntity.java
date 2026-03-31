package com.leclowndu93150.dynamiducts.blockentity;

import com.leclowndu93150.dynamiducts.MNConfig;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.energy.EnergyDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.init.DDBlockEntities;
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
        addDuctUnit(new ItemDuctUnit(this, speed, !opaque, tier.pathWeight()));

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
        BASIC(false, false, 1),
        DENSE(false, false, 1000),
        VACUUM(false, false, 0),
        FAST(false, true, 1),
        ENERGY(true, false, 1),
        ENERGY_FAST(true, true, 1);

        private final boolean hasEnergy;
        private final boolean fast;
        private final int pathWeight;

        Tier(boolean hasEnergy, boolean fast, int pathWeight) {
            this.hasEnergy = hasEnergy;
            this.fast = fast;
            this.pathWeight = pathWeight;
        }

        public boolean hasEnergy() {
            return hasEnergy;
        }

        public boolean isFast() {
            return fast;
        }

        public int pathWeight() {
            return pathWeight;
        }

        public ItemDuctBlockEntity createBlockEntity(BlockPos pos, BlockState state, boolean opaque) {
            BlockEntityType<?> type = switch (this) {
                case BASIC -> opaque ? DDBlockEntities.ITEM_DUCT_BASIC_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_BASIC.get();
                case DENSE -> opaque ? DDBlockEntities.ITEM_DUCT_DENSE_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_DENSE.get();
                case VACUUM -> opaque ? DDBlockEntities.ITEM_DUCT_VACUUM_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_VACUUM.get();
                case FAST -> opaque ? DDBlockEntities.ITEM_DUCT_FAST_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_FAST.get();
                case ENERGY -> opaque ? DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_ENERGY.get();
                case ENERGY_FAST -> opaque ? DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get() : DDBlockEntities.ITEM_DUCT_ENERGY_FAST.get();
            };
            return new ItemDuctBlockEntity(type, pos, state, this, opaque);
        }
    }
}
