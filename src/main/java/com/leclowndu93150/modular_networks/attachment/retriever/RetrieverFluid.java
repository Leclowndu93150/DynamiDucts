package com.leclowndu93150.modular_networks.attachment.retriever;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.fluid.FluidDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class RetrieverFluid extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "retriever_fluid");

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
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.FLUID);
        if (!(unit instanceof FluidDuctUnit fluidUnit)) return;
        if (fluidUnit.getGrid() == null) return;

        for (FluidDuctUnit node : fluidUnit.getGrid().getNodeSet()) {
            for (Direction dir : Direction.values()) {
                IFluidHandler source = node.getTileCache(dir);
                if (source == null) continue;

                FluidStack drained = source.drain(tier.fluidDrainAmount(), IFluidHandler.FluidAction.SIMULATE);
                if (drained.isEmpty()) continue;

                int filled = fluidUnit.getGrid().fill(drained, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    return;
                }
            }
        }
    }
}
