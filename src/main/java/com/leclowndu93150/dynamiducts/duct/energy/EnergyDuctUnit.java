package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyDuctUnit extends DuctUnit<EnergyDuctUnit, EnergyGrid, IEnergyStorage> {

    private final int transferLimit;
    private final int capacityPerDuct;
    private int energyForGrid;

    public EnergyDuctUnit(DuctBlockEntity parent, int transferLimit, int capacityPerDuct) {
        super(parent);
        this.transferLimit = transferLimit;
        this.capacityPerDuct = capacityPerDuct;
    }

    @Override
    protected IEnergyStorage[] createTileCacheArray() {
        return new IEnergyStorage[6];
    }

    @Override
    protected EnergyDuctUnit[] createDuctCacheArray() {
        return new EnergyDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.ENERGY;
    }

    @Override
    public EnergyGrid createGrid(ServerLevel level) {
        return new EnergyGrid(level, transferLimit, capacityPerDuct);
    }

    @Override
    public int getPathWeight() {
        return 1;
    }

    @Override
    public IEnergyStorage cacheTile(Direction side) {
        if (parent.getLevel() == null) return null;
        return parent.getLevel().getCapability(
                Capabilities.EnergyStorage.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
    }

    @Override
    public boolean tickPass(int pass) {
        if (grid == null) return false;
        if (pass != 0) return true;

        int sendable = grid.getSendableEnergy();
        if (sendable <= 0) return true;

        for (Direction dir : Direction.values()) {
            IEnergyStorage target = tileCache[dir.ordinal()];
            if (target == null || !target.canReceive()) continue;

            int sent = target.receiveEnergy(sendable, false);
            if (sent > 0) {
                grid.useEnergy(sent);
                sendable = grid.getSendableEnergy();
                if (sendable <= 0) break;
            }
        }
        return true;
    }

    public IEnergyStorage createCapability(Direction side) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (grid == null) return 0;
                return grid.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return grid != null ? grid.getStorage().getEnergyStored() : 0;
            }

            @Override
            public int getMaxEnergyStored() {
                return grid != null ? grid.getStorage().getMaxEnergyStored() : 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return grid != null;
            }
        };
    }

    @Override
    public boolean canConnectTo(EnergyDuctUnit other) {
        return other.transferLimit == this.transferLimit || this instanceof SuperConductorDuctUnit || other instanceof SuperConductorDuctUnit;
    }

    public int getTransferLimit() {
        return transferLimit;
    }

    public int getEnergyForGrid() {
        return energyForGrid;
    }

    public void setEnergyForGrid(int energy) {
        this.energyForGrid = energy;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (grid != null && isNode()) {
            tag.putInt("EnergyShare", grid.getNodeShare(this));
        } else {
            tag.putInt("EnergyShare", energyForGrid);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        energyForGrid = tag.getInt("EnergyShare");
    }
}
