package com.leclowndu93150.dynamiducts.attachment.retriever;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.network.Route;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.ItemGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class RetrieverItem extends ConnectionBase {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "retriever_item");

    private Route lastRoute;

    public RetrieverItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
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
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit localUnit)) return;
        if (!(localUnit.getGrid() instanceof ItemGrid grid)) return;

        ResourceHandler<ItemResource> localInv = level.getCapability(
                Capabilities.Item.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (localInv == null) return;

        int maxStack = tier.multiStack() ? tier.stackSize() : Math.min(tier.stackSize(), 64);
        int maxStock = filter.getMaxStock() <= 0 ? Integer.MAX_VALUE : filter.getMaxStock();
        List<Route> routes = grid.getSortedRoutes(localUnit, filter.getRouteType(), lastRoute);

        for (Route route : routes) {
            if (route.destination.equals(parent.getBlockPos()) && route.insertionSide == side) continue;
            ItemDuctUnit remoteNode = findNode(route.destination, grid);
            if (remoteNode == null) continue;

            ResourceHandler<ItemResource> remoteInv = level.getCapability(
                    Capabilities.Item.BLOCK,
                    remoteNode.getPos().relative(route.insertionSide),
                    route.insertionSide.getOpposite()
            );
            if (remoteInv == null) continue;

            for (int slot = 0; slot < remoteInv.size(); slot++) {
                ItemResource resource = remoteInv.getResource(slot);
                if (resource.isEmpty()) continue;
                int available = simulateExtract(remoteInv, slot, resource, maxStack);
                if (available <= 0) continue;
                ItemStack peek = resource.toStack(available);
                if (!filter.matchesItem(peek)) continue;
                int retainSpace = getRetainSpace(localInv, peek, maxStock);
                if (retainSpace <= 0) continue;
                int requestAmount = Math.min(maxStack, retainSpace);
                if (requestAmount <= 0) continue;

                available = simulateExtract(remoteInv, slot, resource, requestAmount);
                if (available <= 0) continue;
                peek = resource.toStack(available);
                if (!canInsertInto(localInv, peek, maxStock)) continue;

                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = remoteInv.extract(slot, resource, requestAmount, transaction);
                    if (extracted <= 0) continue;

                    if (tier.multiStack() && extracted < requestAmount) {
                        for (int s = slot + 1; s < remoteInv.size() && extracted < requestAmount; s++) {
                            if (!resource.equals(remoteInv.getResource(s))) continue;
                            extracted += remoteInv.extract(s, resource, requestAmount - extracted, transaction);
                        }
                    }

                    Route returnRoute = grid.getRouteCache().getRouteBetween(remoteNode, localUnit, side, grid.getNodeSet());
                    if (returnRoute == null) continue;
                    transaction.commit();

                    remoteNode.insertItemWithRoute(resource.toStack(extracted), route.insertionSide, returnRoute, tier.speedBoost());
                }
                lastRoute = route;
                return;
            }
        }
    }

    private static int simulateExtract(ResourceHandler<ItemResource> handler, int slot, ItemResource resource, int amount) {
        try (Transaction simulation = Transaction.openRoot()) {
            return handler.extract(slot, resource, amount, simulation);
        }
    }

    private boolean canInsertInto(ResourceHandler<ItemResource> handler, ItemStack stack, int maxStock) {
        int retainSpace = getRetainSpace(handler, stack, maxStock);
        if (retainSpace <= 0) {
            return false;
        }

        try (Transaction simulation = Transaction.openRoot()) {
            return handler.insert(ItemResource.of(stack), Math.min(stack.getCount(), retainSpace), simulation) > 0;
        }
    }

    private int getRetainSpace(ResourceHandler<ItemResource> handler, ItemStack stack, int maxStock) {
        if (maxStock == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        int stored = 0;
        for (int i = 0; i < handler.size(); i++) {
            ItemResource existing = handler.getResource(i);
            if (!existing.isEmpty() && existing.matches(stack)) {
                stored += handler.getAmountAsInt(i);
                if (stored >= maxStock) {
                    return 0;
                }
            }
        }
        stored += countInTransit(stack);
        return Math.max(0, maxStock - stored);
    }

    private int countInTransit(ItemStack stack) {
        var unit = parent.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit localUnit)) return 0;
        if (!(localUnit.getGrid() instanceof ItemGrid grid)) return 0;

        int count = 0;
        for (ItemDuctUnit ductUnit : grid.getNodeSnapshot()) {
            for (var item : ductUnit.getMyItems()) {
                if (item.route.destination.equals(parent.getBlockPos())
                        && item.route.insertionSide == side
                        && ItemStack.isSameItemSameComponents(item.stack, stack)) {
                    count += item.stack.getCount();
                }
            }
            for (var item : ductUnit.getItemsToAdd()) {
                if (item.route.destination.equals(parent.getBlockPos())
                        && item.route.insertionSide == side
                        && ItemStack.isSameItemSameComponents(item.stack, stack)) {
                    count += item.stack.getCount();
                }
            }
        }
        for (ItemDuctUnit ductUnit : grid.getIdleSnapshot()) {
            for (var item : ductUnit.getMyItems()) {
                if (item.route.destination.equals(parent.getBlockPos())
                        && item.route.insertionSide == side
                        && ItemStack.isSameItemSameComponents(item.stack, stack)) {
                    count += item.stack.getCount();
                }
            }
            for (var item : ductUnit.getItemsToAdd()) {
                if (item.route.destination.equals(parent.getBlockPos())
                        && item.route.insertionSide == side
                        && ItemStack.isSameItemSameComponents(item.stack, stack)) {
                    count += item.stack.getCount();
                }
            }
        }
        return count;
    }

    private ItemDuctUnit findNode(BlockPos pos, ItemGrid grid) {
        for (ItemDuctUnit node : grid.getNodeSnapshot()) {
            if (node.getPos().equals(pos)) return node;
        }
        return null;
    }
}
