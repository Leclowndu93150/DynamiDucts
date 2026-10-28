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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class ServoItem extends ConnectionBase {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "servo_item");

    private Route lastRoute;

    public ServoItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
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
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit itemUnit)) return;
        if (!(itemUnit.getGrid() instanceof ItemGrid grid)) return;

        ResourceHandler<ItemResource> source = level.getCapability(
                Capabilities.Item.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (source == null) return;

        int maxSend = filter.getMaxStockOrDefault(tier.stackSize());
        List<Route> routes = grid.getSortedRoutes(itemUnit, filter.getRouteType(), lastRoute);

        for (int slot = 0; slot < source.size(); slot++) {
            ItemResource resource = source.getResource(slot);
            if (resource.isEmpty()) continue;
            int available;
            try (Transaction simulation = Transaction.openRoot()) {
                available = source.extract(slot, resource, maxSend, simulation);
            }
            if (available <= 0) continue;
            ItemStack peek = resource.toStack(available);
            if (!filter.matchesItem(peek)) continue;

            Route route = findRouteForItem(peek, routes, grid);
            if (route == null) continue;

            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = source.extract(slot, resource, maxSend, transaction);
                if (extracted <= 0) continue;

                if (tier.multiStack() && extracted < maxSend) {
                    for (int s = slot + 1; s < source.size() && extracted < maxSend; s++) {
                        if (!resource.equals(source.getResource(s))) continue;
                        extracted += source.extract(s, resource, maxSend - extracted, transaction);
                    }
                }
                transaction.commit();

                itemUnit.insertItemWithRoute(resource.toStack(extracted), side, route, tier.speedBoost());
            }
            lastRoute = route;
            return;
        }
    }

    private Route findRouteForItem(ItemStack stack, List<Route> routes, ItemGrid grid) {
        for (Route route : routes) {
            if (route.destination.equals(parent.getBlockPos()) && route.insertionSide == side) continue;
            for (ItemDuctUnit node : grid.getNodeSnapshot()) {
                if (!node.getPos().equals(route.destination)) continue;
                if (!grid.acceptsDestinationItem(node, route.insertionSide, stack)) continue;

                ResourceHandler<ItemResource> target = node.getTileCache(route.insertionSide);
                if (target == null) continue;

                try (Transaction simulation = Transaction.openRoot()) {
                    if (target.insert(ItemResource.of(stack), stack.getCount(), simulation) > 0) return route;
                }
            }
        }
        return null;
    }
}
