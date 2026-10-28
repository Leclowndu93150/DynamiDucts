package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

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
                    IEnergyStorage source = node.getTileCache(dir);
                    if (source == null || !source.canExtract()) continue;

                    int available = source.extractEnergy(Integer.MAX_VALUE, true);
                    if (available <= 0) continue;

                    int accepted = distribute(node, dir, available, true, snapshot);
                    if (accepted <= 0) continue;

                    int extracted = source.extractEnergy(accepted, false);
                    if (extracted > 0) {
                        distribute(node, dir, extracted, false, snapshot);
                    }
                }
            }
        } finally {
            distributing = false;
            endTick();
        }
    }

    private int distribute(EnergyDuctUnit sourceNode, Direction sourceSide, int available, boolean simulate, List<EnergyDuctUnit> snapshot) {
        int totalSent = 0;
        for (EnergyDuctUnit targetNode : snapshot) {
            if (targetNode.getGrid() != this) continue;
            for (Direction dir : Direction.values()) {
                if (targetNode == sourceNode && dir == sourceSide) continue;
                IEnergyStorage target = targetNode.getTileCache(dir);
                if (target == null || !target.canReceive()) continue;

                totalSent += target.receiveEnergy(available - totalSent, simulate);
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
    public int receiveEnergy(EnergyDuctUnit unit, Direction side, int maxReceive, boolean simulate) {
        if (distributing || nodeSet.isEmpty()) return 0;

        distributing = true;
        try {
            return distribute(unit, side, maxReceive, simulate, getNodeSnapshot());
        } finally {
            distributing = false;
        }
    }
}
