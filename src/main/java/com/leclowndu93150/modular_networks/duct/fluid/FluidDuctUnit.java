package com.leclowndu93150.modular_networks.duct.fluid;

import com.leclowndu93150.modular_networks.MNConfig;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidDuctUnit extends DuctUnit<FluidDuctUnit, FluidGrid, IFluidHandler> {

    private final int capacityPerDuct;
    private final int throughputPerDuct;
    private final boolean transparent;

    public FluidDuctUnit(DuctBlockEntity parent, int capacityPerDuct, int throughputPerDuct, boolean transparent) {
        super(parent);
        this.capacityPerDuct = capacityPerDuct;
        this.throughputPerDuct = throughputPerDuct;
        this.transparent = transparent;
    }

    @Override
    protected IFluidHandler[] createTileCacheArray() {
        return new IFluidHandler[6];
    }

    @Override
    protected FluidDuctUnit[] createDuctCacheArray() {
        return new FluidDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.FLUID;
    }

    @Override
    public FluidGrid createGrid(ServerLevel level) {
        return new FluidGrid(level, capacityPerDuct, throughputPerDuct);
    }

    @Override
    public int getPathWeight() {
        return 1;
    }

    @Override
    public IFluidHandler cacheTile(Direction side) {
        if (parent.getLevel() == null) return null;
        return parent.getLevel().getCapability(
                Capabilities.FluidHandler.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
    }

    public IFluidHandler createCapability(Direction side) {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return grid != null ? grid.getTank().getTanks() : 0;
            }

            @Override
            public FluidStack getFluidInTank(int tank) {
                return grid != null ? grid.getTank().getFluidInTank(tank) : FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                return grid != null ? grid.getTank().getTankCapacity(tank) : 0;
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return true;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (grid == null) return 0;
                return grid.fill(resource, action);
            }

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                return FluidStack.EMPTY;
            }

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return FluidStack.EMPTY;
            }
        };
    }

    public boolean isTransparent() {
        return transparent;
    }
}
