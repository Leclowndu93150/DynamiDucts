package com.leclowndu93150.dynamiducts.duct.item;

import com.leclowndu93150.dynamiducts.core.network.Route;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TravelingItem {

    public static final int MAX_BOUNCES = 3;

    public final ItemStack stack;
    public Route route;
    public final Direction entrySide;
    public BlockPos currentPos;
    public int pathIndex;
    public int ticksInDuct;
    public int speed;
    public int bounceCount;
    private Direction oldDirection;

    public TravelingItem(ItemStack stack, Route route, BlockPos startPos, Direction entrySide, int speed) {
        this.stack = stack;
        this.route = route;
        this.entrySide = entrySide;
        this.currentPos = startPos;
        this.pathIndex = 0;
        this.ticksInDuct = 0;
        this.speed = speed;
        this.oldDirection = entrySide.getOpposite();
    }

    public enum TickResult {
        TRAVELING,
        ADVANCE,
        ARRIVED
    }

    public TickResult tick() {
        ticksInDuct++;
        if (ticksInDuct < speed) {
            return TickResult.TRAVELING;
        }
        ticksInDuct = 0;
        if (pathIndex < route.path.size()) {
            return TickResult.ADVANCE;
        }
        return TickResult.ARRIVED;
    }

    public Direction advance() {
        Direction dir = route.path.get(pathIndex);
        currentPos = currentPos.relative(dir);
        oldDirection = dir;
        pathIndex++;
        return dir;
    }

    public boolean isAtEnd() {
        return pathIndex >= route.path.size();
    }

    public void reroute(Route newRoute) {
        this.route = newRoute;
        this.pathIndex = 0;
        this.ticksInDuct = 0;
        this.oldDirection = getCurrentDirection().getOpposite();
        this.bounceCount++;
    }

    public void reverse() {
        List<Direction> reversedPath = new ArrayList<>();
        for (int i = pathIndex - 1; i >= 0; i--) {
            reversedPath.add(route.path.get(i).getOpposite());
        }

        BlockPos originPos = currentPos;
        for (Direction dir : reversedPath) {
            originPos = originPos.relative(dir);
        }

        this.route = new Route(originPos, entrySide, reversedPath, 0);
        this.oldDirection = getCurrentDirection().getOpposite();
        this.pathIndex = 0;
        this.ticksInDuct = 0;
        this.bounceCount++;
    }

    public float getProgress() {
        return (float) ticksInDuct / speed;
    }

    public Direction getCurrentDirection() {
        if (pathIndex < route.path.size()) {
            return route.path.get(pathIndex);
        }
        return route.insertionSide;
    }

    public Direction getOldDirection() {
        return oldDirection;
    }
}
