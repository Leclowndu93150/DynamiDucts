package com.leclowndu93150.dynamiducts.attachment.retriever;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class RetrieverFluid extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "retriever_fluid");

    public RetrieverFluid(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isRetriever() {
        return true;
    }

    @Override
    public void tick() {
        if (!isActive()) return;
        performAction();
    }

    @Override
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.FLUID);
        if (!(unit instanceof FluidDuctUnit fluidUnit)) return;
        var grid = fluidUnit.getGrid();
        if (grid == null) return;

        int maxInput = tier.fluidDrainAmount();
        if (maxInput <= 0) return;

        for (FluidDuctUnit node : grid.getNodeSnapshot()) {
            for (Direction dir : Direction.values()) {
                IFluidHandler source = node.getTileCache(dir);
                if (source == null) continue;

                FluidStack drained = source.drain(maxInput, IFluidHandler.FluidAction.SIMULATE);
                if (drained.isEmpty()) continue;
                if (!filter.matchesFluid(drained)) continue;

                int filled = grid.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    return;
                }
            }
        }
    }
}
