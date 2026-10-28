package com.leclowndu93150.dynamiducts.duct.fluid;

import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        this.tank = new FluidGridTank(capacityPerDuct, throughputPerDuct, this::syncVisualIfChanged);
    }

    @Override
    public void addBlock(FluidDuctUnit unit) {
        super.addBlock(unit);
        collectFluidFrom(unit);
    }

    private void collectFluidFrom(FluidDuctUnit unit) {
        FluidStack fluid = unit.getFluidForGrid();
        if (!fluid.isEmpty()) {
            tank.setCapacity(Math.max(tank.getCapacity(), tank.getFluid().getAmount() + fluid.getAmount()));
            tank.fill(fluid);
            unit.setFluidForGrid(FluidStack.EMPTY);
        }
    }

    @Override
    public void removeBlock(FluidDuctUnit unit) {
        if (!isTicking() && !tank.isEmpty() && !unit.isBeingDestroyed()) {
            FluidStack share = getNodeShare(unit);
            if (!share.isEmpty()) {
                unit.setFluidForGrid(share);
                tank.drain(share.getAmount());
            }
        }
        super.removeBlock(unit);
    }

    @Override
    public void onMergeFrom(NetworkGrid<?> source) {
        if (source instanceof FluidGrid fluidSource && !fluidSource.tank.isEmpty()) {
            tank.setCapacity(tank.getCapacity() + fluidSource.tank.getCapacity());
            tank.fill(fluidSource.tank.getFluid().copy());
        }
    }

    @Override
    public void balanceGrid() {
        int totalDucts = Math.max(1, nodeSet.size() + idleSet.size());
        tank.setCapacity(totalDucts * capacityPerDuct);
        tank.setThroughput(throughputPerDuct);
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
        beginTick();
        try {
            tickTemperature();
            if (nodeSet.isEmpty() || tank.isEmpty()) {
                syncVisualIfChanged();
                return;
            }

            int available = getEffectiveThroughput();
            if (available <= 0) {
                syncVisualIfChanged();
                return;
            }

            for (FluidDuctUnit node : getNodeSnapshot()) {
                if (node.getGrid() != this) continue;
                for (Direction dir : Direction.values()) {
                    ResourceHandler<FluidResource> target = node.getTileCache(dir);
                    if (target == null) continue;
                    if (hasServoOnSide(node, dir)) continue;

                    FluidStack toSend = peek(available);
                    if (toSend.isEmpty()) break;
                    if (!acceptsOutput(node, dir, toSend)) continue;

                    int filled = fillTarget(target, toSend);
                    if (filled > 0) {
                        tank.drain(filled);
                        available -= filled;
                        if (available <= 0) {
                            syncVisualIfChanged();
                            return;
                        }
                    }
                }
            }
            syncVisualIfChanged();
        } finally {
            endTick();
        }
    }

    private void tickTemperature() {
        List<FluidDuctUnitTemperate> tempUnits = new ArrayList<>();
        for (FluidDuctUnit unit : getNodeSnapshot()) {
            if (unit instanceof FluidDuctUnitTemperate t) tempUnits.add(t);
        }
        for (FluidDuctUnit unit : getIdleSnapshot()) {
            if (unit instanceof FluidDuctUnitTemperate t) tempUnits.add(t);
        }
        for (FluidDuctUnitTemperate temperate : tempUnits) {
            temperate.tickTemperature();
            if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextInt(10) == 0) {
                temperate.spawnSmokeParticles(serverLevel);
            }
        }
    }

    private int getEffectiveThroughput() {
        if (tank.isEmpty()) return 0;
        int capacity = tank.getCapacity();
        int amount = tank.getFluid().getAmount();
        int throughput = tank.getThroughput();

        if (amount >= capacity * 3 / 4) return throughput;
        if (amount <= capacity / 4) return throughput / 2;
        return throughput * 3 / 4;
    }

    public FluidGridTank getTank() {
        return tank;
    }

    public int fill(FluidResource resource, int amount, TransactionContext transaction) {
        return tank.insert(0, resource, amount, transaction);
    }

    public int extractFrom(ResourceHandler<FluidResource> source, int maxDrain, Predicate<FluidStack> filter) {
        for (int i = 0; i < source.size(); i++) {
            FluidStack contained = FluidUtil.getStack(source, i);
            if (contained.isEmpty() || !filter.test(contained)) continue;
            int moved = transferFrom(source, FluidResource.of(contained), maxDrain);
            if (moved > 0) return moved;
        }
        return 0;
    }

    public int pushTo(ResourceHandler<FluidResource> target, int maxAmount) {
        FluidStack toSend = peek(maxAmount);
        if (toSend.isEmpty()) return 0;
        int filled = fillTarget(target, toSend);
        if (filled > 0) {
            tank.drain(filled);
            syncVisualIfChanged();
        }
        return filled;
    }

    private FluidStack peek(int maxAmount) {
        if (tank.isEmpty() || maxAmount <= 0) return FluidStack.EMPTY;
        return tank.getFluid().copyWithAmount(Math.min(tank.getFluid().getAmount(), maxAmount));
    }

    private static int fillTarget(ResourceHandler<FluidResource> target, FluidStack toSend) {
        try (Transaction transaction = Transaction.openRoot()) {
            int filled = target.insert(FluidResource.of(toSend), toSend.getAmount(), transaction);
            transaction.commit();
            return filled;
        }
    }

    private int transferFrom(ResourceHandler<FluidResource> source, FluidResource resource, int maxDrain) {
        try (Transaction transaction = Transaction.openRoot()) {
            int simulated;
            try (Transaction simulation = Transaction.open(transaction)) {
                simulated = source.extract(resource, maxDrain, simulation);
            }
            if (simulated <= 0) return 0;
            int accepted;
            try (Transaction simulation = Transaction.open(transaction)) {
                accepted = tank.insert(0, resource, simulated, simulation);
            }
            if (accepted <= 0) return 0;
            int drained = source.extract(resource, accepted, transaction);
            if (drained <= 0) return 0;
            int filled = tank.insert(0, resource, drained, transaction);
            transaction.commit();
            return filled;
        }
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

        for (FluidDuctUnit unit : getNodeSnapshot()) syncVisualToClient(unit, renderFluid);
        for (FluidDuctUnit unit : getIdleSnapshot()) syncVisualToClient(unit, renderFluid);
    }

    private void syncVisualToClient(FluidDuctUnit unit, FluidStack fluid) {
        unit.setRenderFluid(fluid);
        unit.getParent().setChanged();
        level.sendBlockUpdated(unit.getPos(), unit.getParent().getBlockState(), unit.getParent().getBlockState(), Block.UPDATE_CLIENTS);
    }

    private int getRenderLevel() {
        if (tank.isEmpty() || tank.getCapacity() <= 0) {
            return 0;
        }

        long fullPercent = 10000L * tank.getFluid().getAmount() / tank.getCapacity();
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

    private static boolean acceptsOutput(FluidDuctUnit unit, Direction side, FluidStack fluid) {
        Attachment att = unit.getParent().getAttachment(side);
        if (att instanceof ConnectionBase conn && conn.isFilter()) {
            return conn.getFilter().matchesFluid(fluid);
        }
        return true;
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
