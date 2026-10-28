package com.leclowndu93150.dynamiducts.duct.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidGridTank extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {

    private final Runnable onCommit;
    private FluidStack fluid = FluidStack.EMPTY;
    private int capacity;
    private int throughput;

    public FluidGridTank(int capacity, int throughput, Runnable onCommit) {
        this.capacity = capacity;
        this.throughput = throughput;
        this.onCommit = onCommit;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (!fluid.isEmpty() && fluid.getAmount() > capacity) {
            fluid.setAmount(capacity);
        }
    }

    public void setThroughput(int throughput) {
        this.throughput = throughput;
    }

    public int getThroughput() {
        return throughput;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return FluidResource.of(fluid);
    }

    @Override
    public long getAmountAsLong(int index) {
        return fluid.getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return capacity;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return true;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int toFill;
        if (fluid.isEmpty()) {
            toFill = Math.min(capacity, amount);
            if (toFill > 0) {
                updateSnapshots(transaction);
                fluid = resource.toStack(toFill);
            }
        } else if (resource.matches(fluid)) {
            toFill = Math.min(capacity - fluid.getAmount(), amount);
            if (toFill > 0) {
                updateSnapshots(transaction);
                fluid.grow(toFill);
            }
        } else {
            return 0;
        }
        return Math.max(0, toFill);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!resource.matches(fluid)) {
            return 0;
        }

        int drained = Math.min(fluid.getAmount(), amount);
        if (drained > 0) {
            updateSnapshots(transaction);
            shrink(drained);
        }
        return drained;
    }

    public int fill(FluidStack resource) {
        if (resource.isEmpty()) return 0;

        int toFill;
        if (fluid.isEmpty()) {
            toFill = Math.min(capacity, resource.getAmount());
            fluid = resource.copyWithAmount(toFill);
        } else if (FluidStack.isSameFluidSameComponents(fluid, resource)) {
            toFill = Math.min(capacity - fluid.getAmount(), resource.getAmount());
            fluid.grow(toFill);
        } else {
            return 0;
        }
        return toFill;
    }

    public FluidStack drain(int maxDrain) {
        if (fluid.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        int drained = Math.min(fluid.getAmount(), maxDrain);
        FluidStack result = fluid.copyWithAmount(drained);
        shrink(drained);
        return result;
    }

    private void shrink(int amount) {
        fluid.shrink(amount);
        if (fluid.getAmount() <= 0) {
            fluid = FluidStack.EMPTY;
        }
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    @Override
    protected FluidStack createSnapshot() {
        return fluid.copy();
    }

    @Override
    protected void revertToSnapshot(FluidStack snapshot) {
        fluid = snapshot;
    }

    @Override
    protected void onRootCommit(FluidStack originalState) {
        onCommit.run();
    }
}
