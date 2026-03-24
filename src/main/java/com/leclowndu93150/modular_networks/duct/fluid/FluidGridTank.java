package com.leclowndu93150.modular_networks.duct.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidGridTank implements IFluidHandler {

    private FluidStack fluid = FluidStack.EMPTY;
    private int capacity;
    private int throughput;

    public FluidGridTank(int capacity, int throughput) {
        this.capacity = capacity;
        this.throughput = throughput;
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

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        int toFill;
        if (fluid.isEmpty()) {
            toFill = Math.min(capacity, resource.getAmount());
            if (action.execute()) {
                fluid = resource.copyWithAmount(toFill);
            }
        } else if (FluidStack.isSameFluidSameComponents(fluid, resource)) {
            toFill = Math.min(capacity - fluid.getAmount(), resource.getAmount());
            if (action.execute()) {
                fluid.grow(toFill);
            }
        } else {
            return 0;
        }
        return toFill;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluid.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        int drained = Math.min(fluid.getAmount(), maxDrain);
        FluidStack result = fluid.copyWithAmount(drained);
        if (action.execute()) {
            fluid.shrink(drained);
            if (fluid.getAmount() <= 0) {
                fluid = FluidStack.EMPTY;
            }
        }
        return result;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }
}
