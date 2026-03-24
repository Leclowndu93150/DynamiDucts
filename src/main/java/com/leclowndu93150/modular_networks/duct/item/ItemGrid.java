package com.leclowndu93150.modular_networks.duct.item;

import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import com.leclowndu93150.modular_networks.core.network.Route;
import com.leclowndu93150.modular_networks.core.network.RouteCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemGrid extends NetworkGrid<ItemDuctUnit> {

    private final RouteCache routeCache = new RouteCache();
    private final List<TravelingItem> travelingItems = new ArrayList<>();
    private final StackMap stackMap = new StackMap();

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
        tickTravelingItems();
    }

    private void tickTravelingItems() {
        Iterator<TravelingItem> it = travelingItems.iterator();
        while (it.hasNext()) {
            TravelingItem item = it.next();
            if (item.tick()) {
                deliverItem(item);
                it.remove();
            }
        }
    }

    private void deliverItem(TravelingItem tItem) {
        stackMap.remove(tItem.route.destination, tItem.stack.getCount());

        BlockPos destPos = tItem.route.destination;
        for (ItemDuctUnit node : nodeSet) {
            if (!node.getPos().equals(destPos)) continue;

            IItemHandler target = node.getTileCache(tItem.route.insertionSide);
            if (target != null) {
                ItemStack remainder = insertIntoHandler(target, tItem.stack);
                if (!remainder.isEmpty()) {
                    dropItem(remainder, destPos);
                }
                return;
            }
        }
        dropItem(tItem.stack, destPos);
    }

    private ItemStack insertIntoHandler(IItemHandler handler, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = handler.insertItem(i, remaining, false);
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

    public boolean insertNewItem(ItemStack stack, ItemDuctUnit origin, int speed) {
        List<Route> routes = routeCache.getRoutes(origin, nodeSet);
        if (routes.isEmpty()) return false;

        for (Route route : routes) {
            for (ItemDuctUnit node : nodeSet) {
                if (!node.getPos().equals(route.destination)) continue;

                IItemHandler target = node.getTileCache(route.insertionSide);
                if (target == null) continue;

                ItemStack simulated = insertIntoHandler(target, stack.copy());
                int canInsert = stack.getCount() - simulated.getCount();
                if (canInsert <= 0) continue;

                ItemStack toSend = stack.split(canInsert);
                TravelingItem tItem = new TravelingItem(toSend, route, origin.getPos(), speed);
                travelingItems.add(tItem);
                stackMap.add(route.destination, toSend.getCount());
                return true;
            }
        }
        return false;
    }

    public RouteCache getRouteCache() {
        return routeCache;
    }

    public List<TravelingItem> getTravelingItems() {
        return travelingItems;
    }

    public StackMap getStackMap() {
        return stackMap;
    }
}
