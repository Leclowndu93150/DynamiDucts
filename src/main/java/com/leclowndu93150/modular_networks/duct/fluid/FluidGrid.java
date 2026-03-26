package com.leclowndu93150.modular_networks.duct.fluid;

import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidGrid extends NetworkGrid<FluidDuctUnit> {

    protected final FluidGridTank tank;
    protected final int capacityPerDuct;
    protected final int throughputPerDuct;
    private FluidStack syncedVisualFluid = FluidStack.EMPTY;

    public FluidGrid(ServerLevel level, int capacityPerDuct, int throughputPerDuct) {
        super(level);
        this.capacityPerDuct = capacityPerDuct;
        this.throughputPerDuct = throughputPerDuct;
        this.tank = new FluidGridTank(capacityPerDuct, throughputPerDuct);
    }

    @Override
    public void balanceGrid() {
        int totalDucts = Math.max(1, nodeSet.size() + idleSet.size());
        tank.setCapacity(totalDucts * capacityPerDuct);
        tank.setThroughput(totalDucts * throughputPerDuct);
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        if (nodeSet.isEmpty() || tank.isEmpty()) {
            syncVisualFluidIfNeeded();
            return;
        }

        int available = getEffectiveThroughput();
        if (available <= 0) {
            syncVisualFluidIfNeeded();
            return;
        }

        for (FluidDuctUnit node : nodeSet) {
            for (Direction dir : Direction.values()) {
                IFluidHandler target = node.getTileCache(dir);
                if (target == null) continue;

                FluidStack toSend = tank.drain(available, IFluidHandler.FluidAction.SIMULATE);
                if (toSend.isEmpty()) break;

                int filled = target.fill(toSend, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    available -= filled;
                    if (available <= 0) {
                        syncVisualFluidIfNeeded();
                        return;
                    }
                }
            }
        }
        syncVisualFluidIfNeeded();
    }

    private int getEffectiveThroughput() {
        if (tank.isEmpty()) return 0;
        int capacity = tank.getTankCapacity(0);
        int amount = tank.getFluid().getAmount();
        int throughput = tank.getThroughput();

        if (amount >= capacity * 3 / 4) return throughput;
        if (amount <= capacity / 4) return throughput / 2;
        return throughput * 3 / 4;
    }

    public FluidGridTank getTank() {
        return tank;
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        int filled = tank.fill(resource, action);
        if (filled > 0 && action.execute()) {
            syncVisualFluidIfNeeded();
        }
        return filled;
    }

    private void syncVisualFluidIfNeeded() {
        FluidStack current = tank.getFluid();
        if (sameVisualFluid(syncedVisualFluid, current)) {
            return;
        }

        syncedVisualFluid = current.copy();
        for (FluidDuctUnit node : nodeSet) {
            syncVisualFluid(node, current);
        }
        for (FluidDuctUnit node : idleSet) {
            syncVisualFluid(node, current);
        }
    }

    private void syncVisualFluid(FluidDuctUnit node, FluidStack fluid) {
        node.setVisualFluid(fluid);
        node.getParent().setChanged();
        level.sendBlockUpdated(node.getPos(), node.getParent().getBlockState(), node.getParent().getBlockState(), Block.UPDATE_CLIENTS);
    }

    private static boolean sameVisualFluid(FluidStack first, FluidStack second) {
        if (first.isEmpty() || second.isEmpty()) {
            return first.isEmpty() == second.isEmpty();
        }
        return FluidStack.isSameFluidSameComponents(first, second);
    }
}
