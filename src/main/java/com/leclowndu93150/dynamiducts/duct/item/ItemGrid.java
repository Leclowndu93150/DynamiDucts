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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

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
            IItemHandler target = destNode.getTileCache(tItem.route.insertionSide);
            if (target != null) {
                ItemStack remainder = insertIntoHandler(target, tItem.stack, false);
                if (!remainder.isEmpty()) {
                    tItem.stack.setCount(remainder.getCount());
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

            IItemHandler target = node.getTileCache(route.insertionSide);
            if (target == null) continue;

            ItemStack simulated = insertIntoHandler(target, item.stack.copy(), true);
            int canInsert = item.stack.getCount() - simulated.getCount();
            if (canInsert <= 0) continue;

            item.reroute(route);
            currentUnit.addTravelingItem(item);
            return;
        }

        item.reverse();
        currentUnit.addTravelingItem(item);
    }

    private ItemStack insertIntoHandler(IItemHandler handler, ItemStack stack, boolean simulate) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = handler.insertItem(i, remaining, simulate);
        }
        return remaining;
    }

    private void dropItem(ItemStack stack, BlockPos pos) {
        if (!stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            level.addFreshEntity(entity);
        }
    }

    public boolean insertNewItem(ItemStack stack, ItemDuctUnit origin, Direction entrySide, int speed) {
        List<Route> routes = routeCache.getRoutes(origin, nodeSet);
        if (routes.isEmpty()) return false;

        for (Route route : routes) {
            ItemDuctUnit node = nodesByPos.get(route.destination);
            if (node == null) continue;
            if (!acceptsDestinationItem(node, route.insertionSide, stack)) continue;

            IItemHandler target = node.getTileCache(route.insertionSide);
            if (target == null) continue;

            ItemStack simulated = insertIntoHandler(target, stack.copy(), true);
            int canInsert = stack.getCount() - simulated.getCount();
            if (canInsert <= 0) continue;

            ItemStack toSend = stack.split(canInsert);
            TravelingItem tItem = new TravelingItem(toSend, route, origin.getPos(), entrySide, speed);
            origin.addTravelingItem(tItem);
            syncTravelersAt(origin);
            return true;
        }
        return false;
    }

    private void syncTravelersAt(ItemDuctUnit itemUnit) {
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
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), new ItemTravelSyncPayload(pos, snapshots));
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

    public List<Route> getSortedRoutes(ItemDuctUnit origin, int routeType) {
        return routeCache.getSortedRoutes(origin, nodeSet, routeType);
    }

    public RouteCache getRouteCache() {
        return routeCache;
    }
}
