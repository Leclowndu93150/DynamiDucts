package com.leclowndu93150.dynamiducts.attachment.retriever;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class RetrieverFluid extends ConnectionBase {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "retriever_fluid");

    public RetrieverFluid(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public Identifier getId() {
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

        ResourceHandler<FluidResource> target = level.getCapability(
                Capabilities.Fluid.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (target == null) return;

        int maxInput = tier.fluidDrainAmount();
        if (maxInput <= 0) return;

        for (FluidDuctUnit node : grid.getNodeSnapshot()) {
            for (Direction dir : Direction.values()) {
                if (hasRetrieverOnSide(node, dir)) continue;
                ResourceHandler<FluidResource> source = node.getTileCache(dir);
                if (source == null) continue;

                if (grid.extractFrom(source, maxInput, filter::matchesFluid) > 0) {
                    grid.pushTo(target, maxInput);
                    return;
                }
            }
        }
    }

    private static boolean hasRetrieverOnSide(FluidDuctUnit node, Direction side) {
        return node.getParent().getAttachment(side) instanceof ConnectionBase conn && conn.isRetriever();
    }
}
