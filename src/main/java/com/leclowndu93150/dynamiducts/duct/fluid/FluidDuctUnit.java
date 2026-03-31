package com.leclowndu93150.dynamiducts.duct.fluid;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidDuctUnit extends DuctUnit<FluidDuctUnit, FluidGrid, IFluidHandler> {

    private final int capacityPerDuct;
    private final int throughputPerDuct;
    private final boolean transparent;
    private FluidStack fluidForGrid = FluidStack.EMPTY;
    private FluidStack renderFluid = FluidStack.EMPTY;

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

    private boolean beingDestroyed;

    public boolean isTransparent() {
        return transparent;
    }

    public void markBeingDestroyed() {
        beingDestroyed = true;
    }

    public boolean isBeingDestroyed() {
        return beingDestroyed;
    }

    public FluidStack getFluidForGrid() {
        return fluidForGrid;
    }

    public void setFluidForGrid(FluidStack fluid) {
        this.fluidForGrid = fluid != null ? fluid : FluidStack.EMPTY;
    }

    public FluidStack getVisualFluid() {
        return renderFluid;
    }

    public int getVisualFluidLevel() {
        return renderFluid.isEmpty() ? 0 : Mth.clamp(renderFluid.getAmount(), 1, 6);
    }

    public void setRenderFluid(FluidStack fluid) {
        renderFluid = fluid == null || fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        FluidStack toSave = FluidStack.EMPTY;
        if (grid != null && !grid.getTank().isEmpty()) {
            toSave = grid.getNodeShare(this);
        } else if (!fluidForGrid.isEmpty()) {
            toSave = fluidForGrid;
        }
        if (!toSave.isEmpty()) {
            tag.put("Fluid", toSave.saveOptional(provider));
        }
        if (!renderFluid.isEmpty()) {
            tag.put("RenderFluid", renderFluid.saveOptional(provider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        fluidForGrid = tag.contains("Fluid")
                ? FluidStack.parseOptional(provider, tag.getCompound("Fluid"))
                : FluidStack.EMPTY;
        renderFluid = tag.contains("RenderFluid")
                ? FluidStack.parseOptional(provider, tag.getCompound("RenderFluid"))
                : FluidStack.EMPTY;
    }
}
