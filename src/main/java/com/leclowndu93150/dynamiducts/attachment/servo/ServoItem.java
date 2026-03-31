package com.leclowndu93150.dynamiducts.attachment.servo;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.ItemGrid;
import com.leclowndu93150.dynamiducts.core.network.Route;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class ServoItem extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "servo_item");

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
        if (!(itemUnit.getGrid() instanceof ItemGrid grid)) return;

        IItemHandler source = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (source == null) return;

        int maxSend = filter.getMaxStockOrDefault(tier.stackSize());
        List<Route> routes = grid.getSortedRoutes(itemUnit, filter.getRouteType());

        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack peek = source.extractItem(slot, maxSend, true);
            if (peek.isEmpty()) continue;
            if (!filter.matchesItem(peek)) continue;

            Route route = findRouteForItem(peek, routes, grid);
            if (route == null) continue;

            ItemStack extracted = source.extractItem(slot, maxSend, false);
            if (extracted.isEmpty()) continue;

            if (tier.multiStack() && extracted.getCount() < maxSend) {
                for (int s = slot + 1; s < source.getSlots() && extracted.getCount() < maxSend; s++) {
                    ItemStack other = source.extractItem(s, maxSend - extracted.getCount(), true);
                    if (other.isEmpty() || !ItemStack.isSameItemSameComponents(extracted, other)) continue;
                    ItemStack extra = source.extractItem(s, maxSend - extracted.getCount(), false);
                    if (!extra.isEmpty()) {
                        extracted.grow(extra.getCount());
                    }
                }
            }

            itemUnit.insertItemWithRoute(extracted, side, route, tier.speedBoost());
            return;
        }
    }

    private Route findRouteForItem(ItemStack stack, List<Route> routes, ItemGrid grid) {
        for (Route route : routes) {
            for (ItemDuctUnit node : grid.getNodeSet()) {
                if (!node.getPos().equals(route.destination)) continue;

                IItemHandler target = node.getTileCache(route.insertionSide);
                if (target == null) continue;

                ItemStack simulated = stack.copy();
                for (int i = 0; i < target.getSlots() && !simulated.isEmpty(); i++) {
                    simulated = target.insertItem(i, simulated, true);
                }
                if (simulated.getCount() < stack.getCount()) return route;
            }
        }
        return null;
    }
}
