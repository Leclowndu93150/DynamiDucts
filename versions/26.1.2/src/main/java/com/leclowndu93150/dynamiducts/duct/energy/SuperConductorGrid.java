package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;

public class SuperConductorGrid extends EnergyGrid {

    private boolean distributing;

    public SuperConductorGrid(ServerLevel level) {
        super(level, Integer.MAX_VALUE, 0);
    }

    @Override
    public void tickGrid() {
        if (nodeSet.isEmpty()) return;

        beginTick();
        distributing = true;
        try {
            List<EnergyDuctUnit> snapshot = getNodeSnapshot();
            for (EnergyDuctUnit node : snapshot) {
                if (node.getGrid() != this) continue;
                for (Direction dir : Direction.values()) {
                    EnergyHandler source = node.getTileCache(dir);
                    if (source == null) continue;

                    try (Transaction transaction = Transaction.openRoot()) {
                        int available;
                        try (Transaction simulation = Transaction.open(transaction)) {
                            available = source.extract(Integer.MAX_VALUE, simulation);
                        }
                        if (available <= 0) continue;

                        int accepted;
                        try (Transaction simulation = Transaction.open(transaction)) {
                            accepted = distribute(node, dir, available, simulation, snapshot);
                        }
                        if (accepted <= 0) continue;

                        int extracted = source.extract(accepted, transaction);
                        if (extracted > 0) {
                            distribute(node, dir, extracted, transaction, snapshot);
                        }
                        transaction.commit();
                    }
                }
            }
        } finally {
            distributing = false;
            endTick();
        }
    }

    private int distribute(EnergyDuctUnit sourceNode, Direction sourceSide, int available, TransactionContext transaction, List<EnergyDuctUnit> snapshot) {
        int totalSent = 0;
        for (EnergyDuctUnit targetNode : snapshot) {
            if (targetNode.getGrid() != this) continue;
            for (Direction dir : Direction.values()) {
                if (targetNode == sourceNode && dir == sourceSide) continue;
                EnergyHandler target = targetNode.getTileCache(dir);
                if (target == null) continue;

                totalSent += target.insert(available - totalSent, transaction);
                if (totalSent >= available) return totalSent;
            }
        }
        return totalSent;
    }

    @Override
    public boolean canAddBlock(EnergyDuctUnit block) {
        return block instanceof SuperConductorDuctUnit;
    }

    @Override
    public boolean canGridsMerge(NetworkGrid<?> other) {
        return other instanceof SuperConductorGrid;
    }

    @Override
    public int receiveEnergy(EnergyDuctUnit unit, Direction side, int maxReceive, TransactionContext transaction) {
        if (distributing || nodeSet.isEmpty()) return 0;

        distributing = true;
        try {
            return distribute(unit, side, maxReceive, transaction, getNodeSnapshot());
        } finally {
            distributing = false;
        }
    }
}
