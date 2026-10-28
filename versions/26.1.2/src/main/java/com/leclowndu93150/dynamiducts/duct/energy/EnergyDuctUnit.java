package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergyDuctUnit extends DuctUnit<EnergyDuctUnit, EnergyGrid, EnergyHandler> {

    private final int transferLimit;
    private final int capacityPerDuct;
    private int energyForGrid;
    private byte receivedMask;
    private long receivedGameTime = -1;
    private final ReceivedJournal receivedJournal = new ReceivedJournal();

    public EnergyDuctUnit(DuctBlockEntity parent, int transferLimit, int capacityPerDuct) {
        super(parent);
        this.transferLimit = transferLimit;
        this.capacityPerDuct = capacityPerDuct;
    }

    @Override
    protected EnergyHandler[] createTileCacheArray() {
        return new EnergyHandler[6];
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
    protected BlockCapability<EnergyHandler, Direction> getTileCapability() {
        return Capabilities.Energy.BLOCK;
    }

    @Override
    public boolean tickPass(int pass) {
        if (grid == null) return false;
        if (pass != 0) return true;

        int sendable = grid.getSendableEnergy();
        int sent = 0;
        try (Transaction transaction = Transaction.openRoot()) {
            for (Direction dir : Direction.values()) {
                if (sent >= sendable) break;
                if (receivedThisTick(dir)) continue;
                EnergyHandler target = tileCache[dir.ordinal()];
                if (target == null) continue;

                sent += target.insert(sendable - sent, transaction);
            }
            transaction.commit();
        }
        if (sent > 0) {
            grid.useEnergy(sent);
        }
        return true;
    }

    private void markReceived(Direction side, TransactionContext transaction) {
        receivedJournal.updateSnapshots(transaction);
        long gameTime = parent.getLevel().getGameTime();
        if (gameTime != receivedGameTime) {
            receivedGameTime = gameTime;
            receivedMask = 0;
        }
        receivedMask |= (byte) (1 << side.ordinal());
    }

    private boolean receivedThisTick(Direction side) {
        return receivedGameTime == parent.getLevel().getGameTime() && (receivedMask & (1 << side.ordinal())) != 0;
    }

    public EnergyHandler createCapability(Direction side) {
        return new EnergyHandler() {
            @Override
            public long getAmountAsLong() {
                return grid != null ? grid.getStorage().getAmountAsLong() : 0;
            }

            @Override
            public long getCapacityAsLong() {
                return grid != null ? grid.getStorage().getCapacityAsLong() : 0;
            }

            @Override
            public int insert(int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonNegative(amount);
                if (grid == null) return 0;
                int received = grid.receiveEnergy(EnergyDuctUnit.this, side, amount, transaction);
                if (received > 0 && side != null) {
                    markReceived(side, transaction);
                }
                return received;
            }

            @Override
            public int extract(int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonNegative(amount);
                return 0;
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
    public void saveAdditional(ValueOutput output) {
        if (grid != null && isNode()) {
            output.putInt("EnergyShare", grid.getNodeShare(this));
        } else {
            output.putInt("EnergyShare", energyForGrid);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        energyForGrid = input.getIntOr("EnergyShare", 0);
    }

    private record ReceivedState(long gameTime, byte mask) {
    }

    private class ReceivedJournal extends SnapshotJournal<ReceivedState> {
        @Override
        protected ReceivedState createSnapshot() {
            return new ReceivedState(receivedGameTime, receivedMask);
        }

        @Override
        protected void revertToSnapshot(ReceivedState snapshot) {
            receivedGameTime = snapshot.gameTime();
            receivedMask = snapshot.mask();
        }
    }
}
