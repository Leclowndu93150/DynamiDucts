package com.leclowndu93150.modular_networks.duct.item;

import com.leclowndu93150.modular_networks.core.network.Route;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class TravelingItem {

    public final ItemStack stack;
    public final Route route;
    public BlockPos currentPos;
    public int pathIndex;
    public int ticksInDuct;
    public int speed;
    public boolean reachedDestination;

    public TravelingItem(ItemStack stack, Route route, BlockPos startPos, int speed) {
        this.stack = stack;
        this.route = route;
        this.currentPos = startPos;
        this.pathIndex = 0;
        this.ticksInDuct = 0;
        this.speed = speed;
        this.reachedDestination = false;
    }

    public boolean tick() {
        ticksInDuct++;
        if (ticksInDuct >= speed) {
            ticksInDuct = 0;
            if (pathIndex < route.path.size()) {
                Direction dir = route.path.get(pathIndex);
                currentPos = currentPos.relative(dir);
                pathIndex++;
            }
            if (pathIndex >= route.path.size()) {
                reachedDestination = true;
            }
        }
        return reachedDestination;
    }

    public float getProgress() {
        return (float) ticksInDuct / speed;
    }

    public Direction getCurrentDirection() {
        if (pathIndex < route.path.size()) {
            return route.path.get(pathIndex);
        }
        if (!route.path.isEmpty()) {
            return route.path.get(route.path.size() - 1);
        }
        return Direction.DOWN;
    }
}
