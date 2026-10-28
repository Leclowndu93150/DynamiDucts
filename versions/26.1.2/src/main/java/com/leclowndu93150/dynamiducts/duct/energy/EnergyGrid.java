package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

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
            storage.setCapacity(Math.max(storage.getCapacityAsInt(), storage.getAmountAsInt() + stored));
            storage.modifyEnergyStored(stored);
            unit.setEnergyForGrid(0);
        }
    }

    @Override
    public void removeBlock(EnergyDuctUnit unit) {
        if (!isTicking() && nodeSet.contains(unit)) {
            int share = getNodeShare(unit);
            unit.setEnergyForGrid(share);
            storage.modifyEnergyStored(-share);
        }
        super.removeBlock(unit);
    }

    @Override
    public void onMergeFrom(NetworkGrid<?> source) {
        if (source instanceof EnergyGrid energySource && energySource.storage.getAmountAsInt() > 0) {
            storage.setCapacity(storage.getCapacityAsInt() + energySource.storage.getCapacityAsInt());
            storage.modifyEnergyStored(energySource.storage.getAmountAsInt());
        }
    }

    @Override
    public void balanceGrid() {
        storage.setCapacity(Math.max(1, nodeSet.size()) * capacityPerDuct);
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        if (nodeSet.isEmpty() || storage.getAmountAsInt() <= 0) return;

        currentEnergyShare = storage.getAmountAsInt() / nodeSet.size();
        extraEnergy = storage.getAmountAsInt() % nodeSet.size();

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
        try (Transaction transaction = Transaction.openRoot()) {
            storage.extract(amount, transaction);
            transaction.commit();
        }
        if (amount > currentEnergyShare) {
            extraEnergy -= (amount - currentEnergyShare);
            extraEnergy = Math.max(0, extraEnergy);
        }
    }

    public int receiveEnergy(EnergyDuctUnit unit, Direction side, int maxReceive, TransactionContext transaction) {
        return storage.insert(maxReceive, transaction);
    }

    public boolean isPowered() {
        return storage.getAmountAsInt() > 0;
    }

    public int getNodeShare(EnergyDuctUnit unit) {
        if (nodeSet.size() <= 1) return storage.getAmountAsInt();
        if (isFirstBlock(unit)) {
            return storage.getAmountAsInt() / nodeSet.size() + storage.getAmountAsInt() % nodeSet.size();
        }
        return storage.getAmountAsInt() / nodeSet.size();
    }

    public GridEnergyStorage getStorage() {
        return storage;
    }

    public int getTransferLimit() {
        return transferLimit;
    }
}
