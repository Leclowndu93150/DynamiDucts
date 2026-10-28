package com.leclowndu93150.dynamiducts.attachment.servo;

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

public class ServoFluid extends ConnectionBase {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "servo_fluid");

    public ServoFluid(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean isServo() {
        return true;
    }

    @Override
    public boolean canSend() {
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

        ResourceHandler<FluidResource> source = level.getCapability(
                Capabilities.Fluid.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (source == null) return;

        int maxInput = tier.fluidDrainAmount();
        if (maxInput <= 0) return;

        grid.extractFrom(source, maxInput, filter::matchesFluid);
    }
}
