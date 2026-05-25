package com.leclowndu93150.dynamiducts.duct.energy;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class SuperConductorGrid extends EnergyGrid {

    public SuperConductorGrid(ServerLevel level) {
        super(level, Integer.MAX_VALUE, 0);
    }

    @Override
    public void tickGrid() {
        if (nodeSet.isEmpty()) return;

        beginTick();
        try {
            List<EnergyDuctUnit> snapshot = getNodeSnapshot();
            for (EnergyDuctUnit node : snapshot) {
                if (node.getGrid() != this) continue;
                for (Direction dir : Direction.values()) {
                    IEnergyStorage source = node.getTileCache(dir);
                    if (source == null || !source.canExtract()) continue;

                    int available = source.extractEnergy(Integer.MAX_VALUE, true);
                    if (available <= 0) continue;

                    int distributed = distributeToOthers(node, available, snapshot);
                    if (distributed > 0) {
                        source.extractEnergy(distributed, false);
                    }
                }
            }
        } finally {
            endTick();
        }
    }

    private int distributeToOthers(EnergyDuctUnit sourceNode, int available, List<EnergyDuctUnit> snapshot) {
        int totalSent = 0;
        for (EnergyDuctUnit targetNode : snapshot) {
            if (targetNode == sourceNode) continue;
            if (targetNode.getGrid() != this) continue;
            for (Direction dir : Direction.values()) {
                IEnergyStorage target = targetNode.getTileCache(dir);
                if (target == null || !target.canReceive()) continue;

                int sent = target.receiveEnergy(available - totalSent, false);
                totalSent += sent;
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
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }
}
