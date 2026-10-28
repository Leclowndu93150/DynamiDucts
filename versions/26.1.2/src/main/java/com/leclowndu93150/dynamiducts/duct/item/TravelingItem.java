package com.leclowndu93150.dynamiducts.duct.item;

import com.leclowndu93150.dynamiducts.core.network.Route;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void save(ValueOutput output) {
        TravelingItemSnapshot.fromTravelingItem(this).save(output);
        output.putLong("Destination", route.destination.asLong());
        output.putByte("InsertionSide", (byte) route.insertionSide.get3DDataValue());
        output.putIntArray("Path", route.path.stream().mapToInt(Direction::get3DDataValue).toArray());
        output.putInt("PathWeight", route.pathWeight);
        output.putByte("EntrySide", (byte) entrySide.get3DDataValue());
        output.putInt("PathIndex", pathIndex);
        output.putInt("Bounces", bounceCount);
    }

    public static TravelingItem load(ValueInput input, BlockPos pos) {
        Optional<Long> destination = input.getLong("Destination");
        if (destination.isEmpty()) {
            return null;
        }
        ItemStack stack = input.read("Stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return null;
        }

        List<Direction> path = new ArrayList<>();
        for (int dir : input.getIntArray("Path").orElse(new int[0])) {
            path.add(Direction.from3DDataValue(dir));
        }
        Route route = new Route(BlockPos.of(destination.get()), Direction.from3DDataValue(input.getByteOr("InsertionSide", (byte) 0)), path, input.getIntOr("PathWeight", 0));

        TravelingItem item = new TravelingItem(stack, route, pos, Direction.from3DDataValue(input.getByteOr("EntrySide", (byte) 0)), Math.max(1, input.getIntOr("Speed", 1)));
        item.pathIndex = Math.min(input.getIntOr("PathIndex", 0), path.size());
        item.ticksInDuct = Math.max(0, input.getIntOr("Progress", 0));
        item.bounceCount = input.getIntOr("Bounces", 0);
        item.oldDirection = Direction.from3DDataValue(input.getByteOr("OldDirection", (byte) 0));
        return item;
    }
}
