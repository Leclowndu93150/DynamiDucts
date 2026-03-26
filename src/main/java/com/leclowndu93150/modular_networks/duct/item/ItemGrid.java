package com.leclowndu93150.modular_networks.duct.item;

import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import com.leclowndu93150.modular_networks.core.network.Route;
import com.leclowndu93150.modular_networks.core.network.RouteCache;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.network.payload.ItemTravelSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ItemGrid extends NetworkGrid<ItemDuctUnit> {

    private static final int MAX_SYNCED_ITEMS_PER_DUCT = 16;

    private final RouteCache routeCache = new RouteCache();

    public ItemGrid(ServerLevel level) {
        super(level);
    }

    @Override
    public void onMajorGridChange() {
        super.onMajorGridChange();
        routeCache.markDirty();
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        tickAllDuctItems();
    }

    private void tickAllDuctItems() {
        Set<BlockPos> dirtyPositions = new HashSet<>();

        for (ItemDuctUnit unit : getAllUnits()) {
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
                            dirtyPositions.add(unit.getPos());
                            bounceItem(item, unit);
                            it.remove();
                        } else if (nextDuct != null) {
                            dirtyPositions.add(unit.getPos());
                            item.advance();
                            it.remove();
                            nextDuct.transferItem(item);
                            dirtyPositions.add(nextDuct.getPos());
                        } else if (item.isAtEnd()) {
                            dirtyPositions.add(unit.getPos());
                            item.advance();
                            deliverItem(item, unit);
                            it.remove();
                        } else {
                            dirtyPositions.add(unit.getPos());
                            bounceItem(item, unit);
                            it.remove();
                        }
                    }
                    case ARRIVED -> {
                        dirtyPositions.add(unit.getPos());
                        deliverItem(item, unit);
                        it.remove();
                    }
                }
            }
        }

        for (ItemDuctUnit unit : getAllUnits()) {
            unit.flushItemsToAdd();
        }

        dirtyPositions.forEach(this::syncTravelersAt);
    }

    private List<ItemDuctUnit> getAllUnits() {
        List<ItemDuctUnit> all = new ArrayList<>(nodeSet.size() + idleSet.size());
        all.addAll(nodeSet);
        all.addAll(idleSet);
        return all;
    }

    private void deliverItem(TravelingItem tItem, ItemDuctUnit currentUnit) {
        BlockPos destPos = tItem.route.destination;

        for (ItemDuctUnit node : nodeSet) {
            if (!node.getPos().equals(destPos)) continue;

            IItemHandler target = node.getTileCache(tItem.route.insertionSide);
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

            for (ItemDuctUnit node : nodeSet) {
                if (!node.getPos().equals(route.destination)) continue;

                IItemHandler target = node.getTileCache(route.insertionSide);
                if (target == null) continue;

                ItemStack simulated = insertIntoHandler(target, item.stack.copy(), true);
                int canInsert = item.stack.getCount() - simulated.getCount();
                if (canInsert <= 0) continue;

                item.reroute(route);
                currentUnit.addTravelingItem(item);
                return;
            }
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
            for (ItemDuctUnit node : nodeSet) {
                if (!node.getPos().equals(route.destination)) continue;

                IItemHandler target = node.getTileCache(route.insertionSide);
                if (target == null) continue;

                ItemStack simulated = insertIntoHandler(target, stack.copy(), true);
                int canInsert = stack.getCount() - simulated.getCount();
                if (canInsert <= 0) continue;

                ItemStack toSend = stack.split(canInsert);
                TravelingItem tItem = new TravelingItem(toSend, route, origin.getPos(), entrySide, speed);
                origin.addTravelingItem(tItem);
                syncTravelersAt(origin.getPos());
                return true;
            }
        }
        return false;
    }

    private void syncTravelersAt(BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE)) {
            return;
        }
        if (!(ductBE.getDuctUnit(DuctToken.ITEM) instanceof ItemDuctUnit itemUnit)) {
            return;
        }

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
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), new ItemTravelSyncPayload(pos, snapshots));
    }

    private boolean passesFilter(ItemDuctUnit unit, Direction side, ItemStack stack) {
        Attachment att = unit.getParent().getAttachment(side);
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
