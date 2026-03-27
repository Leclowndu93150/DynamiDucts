package com.leclowndu93150.modular_networks.duct.fluid;

import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
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
    private FluidStack lastSyncedFluid = FluidStack.EMPTY;
    private int lastSyncedRenderLevel = -1;

    public FluidGrid(ServerLevel level, int capacityPerDuct, int throughputPerDuct) {
        super(level);
        this.capacityPerDuct = capacityPerDuct;
        this.throughputPerDuct = throughputPerDuct;
        this.tank = new FluidGridTank(capacityPerDuct, throughputPerDuct);
    }

    @Override
    public void addBlock(FluidDuctUnit unit) {
        super.addBlock(unit);
        collectFluidFrom(unit);
    }

    private void collectFluidFrom(FluidDuctUnit unit) {
        FluidStack fluid = unit.getFluidForGrid();
        if (!fluid.isEmpty()) {
            tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            unit.setFluidForGrid(FluidStack.EMPTY);
        }
    }

    @Override
    public void removeBlock(FluidDuctUnit unit) {
        if (!tank.isEmpty() && !unit.isBeingDestroyed()) {
            FluidStack share = getNodeShare(unit);
            if (!share.isEmpty()) {
                unit.setFluidForGrid(share);
                tank.drain(share.getAmount(), IFluidHandler.FluidAction.EXECUTE);
            }
        }
        super.removeBlock(unit);
    }

    @Override
    public void onMergeFrom(NetworkGrid<?> source) {
        if (source instanceof FluidGrid fluidSource && !fluidSource.tank.isEmpty()) {
            tank.fill(fluidSource.tank.getFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void balanceGrid() {
        int totalDucts = Math.max(1, nodeSet.size() + idleSet.size());
        tank.setCapacity(totalDucts * capacityPerDuct);
        tank.setThroughput(totalDucts * throughputPerDuct);
    }

    @Override
    public void onMajorGridChange() {
        super.onMajorGridChange();
        lastSyncedFluid = FluidStack.EMPTY;
        lastSyncedRenderLevel = -1;
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        if (nodeSet.isEmpty() || tank.isEmpty()) {
            syncVisualIfChanged();
            return;
        }

        int available = getEffectiveThroughput();
        if (available <= 0) {
            syncVisualIfChanged();
            return;
        }

        for (FluidDuctUnit node : nodeSet) {
            for (Direction dir : Direction.values()) {
                IFluidHandler target = node.getTileCache(dir);
                if (target == null) continue;
                if (hasServoOnSide(node, dir)) continue;

                FluidStack toSend = tank.drain(available, IFluidHandler.FluidAction.SIMULATE);
                if (toSend.isEmpty()) break;

                int filled = target.fill(toSend, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    available -= filled;
                    if (available <= 0) {
                        syncVisualIfChanged();
                        return;
                    }
                }
            }
        }
        syncVisualIfChanged();
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
            syncVisualIfChanged();
        }
        return filled;
    }

    public FluidStack getNodeShare(FluidDuctUnit unit) {
        if (tank.isEmpty()) return FluidStack.EMPTY;
        int totalDucts = nodeSet.size() + idleSet.size();
        if (totalDucts <= 0) return FluidStack.EMPTY;
        int share = tank.getFluid().getAmount() / totalDucts;
        if (share <= 0) return FluidStack.EMPTY;
        return tank.getFluid().copyWithAmount(share);
    }

    private void syncVisualIfChanged() {
        FluidStack current = tank.getFluid();
        int renderLevel = getRenderLevel();
        if (isSameVisual(lastSyncedFluid, current) && lastSyncedRenderLevel == renderLevel) return;

        lastSyncedFluid = current.copy();
        lastSyncedRenderLevel = renderLevel;

        FluidStack renderFluid = current.isEmpty() || renderLevel <= 0
                ? FluidStack.EMPTY
                : current.copyWithAmount(renderLevel);

        for (FluidDuctUnit unit : nodeSet) syncVisualToClient(unit, renderFluid);
        for (FluidDuctUnit unit : idleSet) syncVisualToClient(unit, renderFluid);
    }

    private void syncVisualToClient(FluidDuctUnit unit, FluidStack fluid) {
        unit.setRenderFluid(fluid);
        unit.getParent().setChanged();
        level.sendBlockUpdated(unit.getPos(), unit.getParent().getBlockState(), unit.getParent().getBlockState(), Block.UPDATE_CLIENTS);
    }

    private int getRenderLevel() {
        if (tank.isEmpty() || tank.getTankCapacity(0) <= 0) {
            return 0;
        }

        long fullPercent = 10000L * tank.getFluid().getAmount() / tank.getTankCapacity(0);
        if (fullPercent <= 700) {
            return 1;
        }
        if (fullPercent <= 2500) {
            return 2;
        }
        if (fullPercent <= 4500) {
            return 3;
        }
        if (fullPercent <= 6500) {
            return 4;
        }
        if (fullPercent <= 8500) {
            return 5;
        }
        return 6;
    }

    private static boolean hasServoOnSide(FluidDuctUnit unit, Direction side) {
        Attachment att = unit.getParent().getAttachment(side);
        return att instanceof ConnectionBase conn && conn.isServo();
    }

    private static boolean isSameVisual(FluidStack a, FluidStack b) {
        if (a.isEmpty() || b.isEmpty()) return a.isEmpty() == b.isEmpty();
        return FluidStack.isSameFluidSameComponents(a, b);
    }
}
