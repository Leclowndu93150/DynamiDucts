package com.leclowndu93150.modular_networks.attachment.servo;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ServoItem extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "servo_item");

    public ServoItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public ResourceLocation getId() {
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
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit itemUnit)) return;
        if (itemUnit.getGrid() == null) return;

        IItemHandler source = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (source == null) return;

        int maxStack = filter.getMaxStockOrDefault(tier.stackSize());

        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack extracted = source.extractItem(slot, maxStack, true);
            if (extracted.isEmpty()) continue;

            if (!filter.isEmpty() && !filter.matchesItem(extracted)) continue;

            ItemStack toSend = source.extractItem(slot, maxStack, false);
            if (!toSend.isEmpty()) {
                if (!itemUnit.insertItem(toSend)) {
                    source.insertItem(slot, toSend, false);
                }
                return;
            }
        }
    }
}
