package com.leclowndu93150.dynamiducts.duct.item;

import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import com.leclowndu93150.dynamiducts.core.network.Route;
import com.leclowndu93150.dynamiducts.core.network.RouteCache;
import com.leclowndu93150.dynamiducts.network.payload.ItemTravelSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.*;

public class ItemGrid extends NetworkGrid<ItemDuctUnit> {

    private static final int MAX_SYNCED_ITEMS_PER_DUCT = 16;

    private final RouteCache routeCache = new RouteCache();
    private final Map<BlockPos, ItemDuctUnit> nodesByPos = new HashMap<>();
    private final Set<ItemDuctUnit> dirtyUnits = new HashSet<>();

    public ItemGrid(ServerLevel level) {
        super(level);
    }

    @Override
    public void onMajorGridChange() {
        super.onMajorGridChange();
        routeCache.markDirty();
        rebuildNodesByPos();
    }

    @Override
    public void addNode(ItemDuctUnit unit) {
        super.addNode(unit);
        nodesByPos.put(unit.getPos(), unit);
    }

    @Override
    public void removeBlock(ItemDuctUnit unit) {
        nodesByPos.remove(unit.getPos());
        super.removeBlock(unit);
    }

    @Override
    public void removeNode(ItemDuctUnit unit) {
        nodesByPos.remove(unit.getPos());
        super.removeNode(unit);
    }

    private void rebuildNodesByPos() {
        nodesByPos.clear();
        for (ItemDuctUnit node : getNodeSnapshot()) {
            nodesByPos.put(node.getPos(), node);
        }
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        beginTick();
        try {
            tickAllDuctItems();
        } finally {
            endTick();
        }
    }

    private void tickAllDuctItems() {
        dirtyUnits.clear();

        List<ItemDuctUnit> nodeSnap = getNodeSnapshot();
        List<ItemDuctUnit> idleSnap = getIdleSnapshot();

        for (ItemDuctUnit unit : nodeSnap) tickUnit(unit);
        for (ItemDuctUnit unit : idleSnap) tickUnit(unit);

        for (ItemDuctUnit unit : nodeSnap) unit.flushItemsToAdd();
        for (ItemDuctUnit unit : idleSnap) unit.flushItemsToAdd();

        for (ItemDuctUnit unit : dirtyUnits) {
            syncTravelersAt(unit);
        }
        dirtyUnits.clear();
    }

    private void tickUnit(ItemDuctUnit unit) {
        unit.flushItemsToAdd();

        Iterator<TravelingItem> it = unit.getMyItems().iterator();
        while (it.hasNext()) {
            TravelingItem item = it.next();
            TravelingItem.TickResult result = item.tick();

            switch (result) {
                case TRAVELING -> {}
                case ADVANCE -> {
                    Direction dir = item.getCurrentDirection();
                    ItemDuctUnit nextDuct = unit.getDuctNeighbor(dir);

                    if (nextDuct != null && !passesFilter(unit, dir, item.stack)) {
                        dirtyUnits.add(unit);
                        bounceItem(item, unit);
                        it.remove();
                    } else if (nextDuct != null) {
                        dirtyUnits.add(unit);
                        item.advance();
                        it.remove();
                        nextDuct.transferItem(item);
                        dirtyUnits.add(nextDuct);
                    } else if (item.isAtEnd()) {
                        dirtyUnits.add(unit);
                        item.advance();
                        deliverItem(item, unit);
                        it.remove();
                    } else {
                        dirtyUnits.add(unit);
                        bounceItem(item, unit);
                        it.remove();
                    }
                }
                case ARRIVED -> {
                    dirtyUnits.add(unit);
                    deliverItem(item, unit);
                    it.remove();
                }
            }
        }
    }

    private void deliverItem(TravelingItem tItem, ItemDuctUnit currentUnit) {
        ItemDuctUnit destNode = nodesByPos.get(tItem.route.destination);

        if (destNode != null) {
            if (!acceptsDestinationItem(destNode, tItem.route.insertionSide, tItem.stack)) {
                bounceItem(tItem, currentUnit);
                return;
            }
            ResourceHandler<ItemResource> target = destNode.getTileCache(tItem.route.insertionSide);
            if (target != null) {
                int inserted;
                try (Transaction transaction = Transaction.openRoot()) {
                    inserted = target.insert(ItemResource.of(tItem.stack), tItem.stack.getCount(), transaction);
                    transaction.commit();
                }
                if (inserted < tItem.stack.getCount()) {
                    tItem.stack.shrink(inserted);
                    bounceItem(tItem, currentUnit);
                }
                return;
            }
        }
        bounceItem(tItem, currentUnit);
    }

    private void bounceItem(TravelingItem item, ItemDuctUnit currentUnit) {
        if (item.bounceCount >= TravelingItem.MAX_BOUNCES) {
            dropItem(item.stack, currentUnit.getPos());
            return;
        }

        List<Route> routes = routeCache.getRoutes(currentUnit, nodeSet);
        for (Route route : routes) {
            if (route.destination.equals(item.route.destination)) continue;

            ItemDuctUnit node = nodesByPos.get(route.destination);
            if (node == null) continue;
            if (!acceptsDestinationItem(node, route.insertionSide, item.stack)) continue;

            ResourceHandler<ItemResource> target = node.getTileCache(route.insertionSide);
            if (target == null) continue;

            int canInsert;
            try (Transaction transaction = Transaction.openRoot()) {
                canInsert = target.insert(ItemResource.of(item.stack), item.stack.getCount(), transaction);
            }
            if (canInsert <= 0) continue;

            item.reroute(route);
            currentUnit.addTravelingItem(item);
            return;
        }

        item.reverse();
        currentUnit.addTravelingItem(item);
    }

    private void dropItem(ItemStack stack, BlockPos pos) {
        if (!stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            level.addFreshEntity(entity);
        }
    }

    public boolean insertNewItem(ItemStack stack, ItemDuctUnit origin, Direction entrySide, int speed, TransactionContext transaction) {
        List<Route> routes = routeCache.getRoutes(origin, nodeSet);
        if (routes.isEmpty()) return false;

        for (Route route : routes) {
            ItemDuctUnit node = nodesByPos.get(route.destination);
            if (node == null) continue;
            if (!acceptsDestinationItem(node, route.insertionSide, stack)) continue;

            ResourceHandler<ItemResource> target = node.getTileCache(route.insertionSide);
            if (target == null) continue;

            int canInsert;
            try (Transaction simulation = Transaction.open(transaction)) {
                canInsert = target.insert(ItemResource.of(stack), stack.getCount(), simulation);
            }
            if (canInsert <= 0) continue;

            ItemStack toSend = stack.split(canInsert);
            TravelingItem tItem = new TravelingItem(toSend, route, origin.getPos(), entrySide, speed);
            origin.addTravelingItem(tItem, transaction);
            return true;
        }
        return false;
    }

    public void syncTravelersAt(ItemDuctUnit itemUnit) {
        List<TravelingItemSnapshot> snapshots = new ArrayList<>();
        for (TravelingItem item : itemUnit.getMyItems()) {
            snapshots.add(TravelingItemSnapshot.fromTravelingItem(item));
            if (snapshots.size() >= MAX_SYNCED_ITEMS_PER_DUCT) break;
        }
        for (TravelingItem item : itemUnit.getItemsToAdd()) {
            if (snapshots.size() >= MAX_SYNCED_ITEMS_PER_DUCT) break;
            snapshots.add(TravelingItemSnapshot.fromTravelingItem(item));
        }

        itemUnit.setServerTravelingItems(snapshots);
        BlockPos pos = itemUnit.getPos();
        level.blockEntityChanged(pos);
        PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos.containing(pos), new ItemTravelSyncPayload(pos, snapshots));
    }

    private boolean passesFilter(ItemDuctUnit unit, Direction side, ItemStack stack) {
        Attachment att = unit.getParent().getAttachment(side);
        if (att instanceof ConnectionBase conn && conn.isFilter()) {
            return conn.getFilter().matchesItem(stack);
        }
        return true;
    }

    public boolean acceptsDestinationItem(ItemDuctUnit node, Direction insertionSide, ItemStack stack) {
        Attachment att = node.getParent().getAttachment(insertionSide);
        if (att instanceof ConnectionBase conn && conn.isFilter()) {
            return conn.getFilter().matchesItem(stack);
        }
        return true;
    }

    public List<Route> getSortedRoutes(ItemDuctUnit origin, int routeType, Route lastRoundRobinRoute) {
        return routeCache.getSortedRoutes(origin, nodeSet, routeType, lastRoundRobinRoute);
    }

    public RouteCache getRouteCache() {
        return routeCache;
    }
}
