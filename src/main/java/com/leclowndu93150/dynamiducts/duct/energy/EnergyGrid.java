package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;

public class EnergyGrid extends NetworkGrid<EnergyDuctUnit> {

    protected final GridEnergyStorage storage;
    protected final int transferLimit;
    protected final int capacityPerDuct;
    private int currentEnergyShare;
    private int extraEnergy;

    public EnergyGrid(ServerLevel level, int transferLimit, int capacityPerDuct) {
        super(level);
        this.transferLimit = transferLimit;
        this.capacityPerDuct = capacityPerDuct;
        this.storage = new GridEnergyStorage(capacityPerDuct, transferLimit);
    }

    @Override
    public void addNode(EnergyDuctUnit unit) {
        super.addNode(unit);
        int stored = unit.getEnergyForGrid();
        if (stored > 0) {
            storage.modifyEnergyStored(stored);
            unit.setEnergyForGrid(0);
        }
    }

    @Override
    public void removeBlock(EnergyDuctUnit unit) {
        if (unit.isNode() && !nodeSet.isEmpty()) {
            unit.setEnergyForGrid(getNodeShare(unit));
        }
        super.removeBlock(unit);
    }

    @Override
    public void balanceGrid() {
        storage.setCapacity(Math.max(1, nodeSet.size()) * capacityPerDuct);
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        if (nodeSet.isEmpty() || storage.getEnergyStored() <= 0) return;

        currentEnergyShare = storage.getEnergyStored() / nodeSet.size();
        extraEnergy = storage.getEnergyStored() % nodeSet.size();

        beginTick();
        try {
            for (EnergyDuctUnit node : getNodeSnapshot()) {
                if (node.getGrid() != this) continue;
                if (!node.tickPass(0) || node.getGrid() == null) break;
            }
        } finally {
            endTick();
        }
    }

    @Override
    public boolean canAddBlock(EnergyDuctUnit block) {
        return block.getTransferLimit() == transferLimit;
    }

    @Override
    public boolean canGridsMerge(NetworkGrid<?> other) {
        return super.canGridsMerge(other) && ((EnergyGrid) other).transferLimit == this.transferLimit;
    }

    public int getSendableEnergy() {
        return Math.min(transferLimit, currentEnergyShare == 0 ? extraEnergy : currentEnergyShare);
    }

    public void useEnergy(int amount) {
        storage.extractEnergy(amount, false);
        if (amount > currentEnergyShare) {
            extraEnergy -= (amount - currentEnergyShare);
            extraEnergy = Math.max(0, extraEnergy);
        }
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    public boolean isPowered() {
        return storage.getEnergyStored() > 0;
    }

    public int getNodeShare(EnergyDuctUnit unit) {
        if (nodeSet.size() <= 1) return storage.getEnergyStored();
        if (isFirstBlock(unit)) {
            return storage.getEnergyStored() / nodeSet.size() + storage.getEnergyStored() % nodeSet.size();
        }
        return storage.getEnergyStored() / nodeSet.size();
    }

    public GridEnergyStorage getStorage() {
        return storage;
    }

    public int getTransferLimit() {
        return transferLimit;
    }
}
