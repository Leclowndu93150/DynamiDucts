package com.leclowndu93150.dynamiducts.duct.fluid;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidDuctUnit extends DuctUnit<FluidDuctUnit, FluidGrid, ResourceHandler<FluidResource>> {

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
    @SuppressWarnings("unchecked")
    protected ResourceHandler<FluidResource>[] createTileCacheArray() {
        return new ResourceHandler[6];
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
    protected BlockCapability<ResourceHandler<FluidResource>, Direction> getTileCapability() {
        return Capabilities.Fluid.BLOCK;
    }

    public ResourceHandler<FluidResource> createCapability(Direction side) {
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return grid != null ? grid.getTank().size() : 0;
            }

            @Override
            public FluidResource getResource(int index) {
                return grid != null ? grid.getTank().getResource(index) : FluidResource.EMPTY;
            }

            @Override
            public long getAmountAsLong(int index) {
                return grid != null ? grid.getTank().getAmountAsLong(index) : 0;
            }

            @Override
            public long getCapacityAsLong(int index, FluidResource resource) {
                return grid != null ? grid.getTank().getCapacityAsLong(index, resource) : 0;
            }

            @Override
            public boolean isValid(int index, FluidResource resource) {
                return true;
            }

            @Override
            public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
                if (grid == null) return 0;
                return grid.fill(resource, amount, transaction);
            }

            @Override
            public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
                return 0;
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
    public void saveAdditional(ValueOutput output) {
        FluidStack toSave = FluidStack.EMPTY;
        if (grid != null && !grid.getTank().isEmpty()) {
            toSave = grid.getNodeShare(this);
        } else if (!fluidForGrid.isEmpty()) {
            toSave = fluidForGrid;
        }
        if (!toSave.isEmpty()) {
            output.store("Fluid", FluidStack.OPTIONAL_CODEC, toSave);
        }
        if (!renderFluid.isEmpty()) {
            output.store("RenderFluid", FluidStack.OPTIONAL_CODEC, renderFluid);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        fluidForGrid = input.read("Fluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        renderFluid = input.read("RenderFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
    }
}
