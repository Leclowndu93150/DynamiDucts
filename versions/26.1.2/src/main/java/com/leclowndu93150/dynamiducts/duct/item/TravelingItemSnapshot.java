package com.leclowndu93150.dynamiducts.duct.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TravelingItemSnapshot {

    private final ItemStack stack;
    private final Direction direction;
    private final Direction oldDirection;
    private int progress;
    private final int speed;

    public TravelingItemSnapshot(ItemStack stack, Direction direction, Direction oldDirection, int progress, int speed) {
        this.stack = stack.copy();
        this.direction = direction;
        this.oldDirection = oldDirection;
        this.progress = Math.max(0, progress);
        this.speed = Math.max(1, speed);
    }

    public static TravelingItemSnapshot fromTravelingItem(TravelingItem item) {
        return new TravelingItemSnapshot(
                item.stack,
                item.getCurrentDirection(),
                item.getOldDirection(),
                item.ticksInDuct,
                item.speed
        );
    }

    public static TravelingItemSnapshot load(ValueInput input) {
        ItemStack stack = input.read("Stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return null;
        }

        Direction direction = Direction.from3DDataValue(input.getByteOr("Direction", (byte) 0));
        Direction oldDirection = Direction.from3DDataValue(input.getByteOr("OldDirection", (byte) 0));
        int progress = input.getIntOr("Progress", 0);
        int speed = input.getIntOr("Speed", 0);
        return new TravelingItemSnapshot(stack, direction, oldDirection, progress, speed);
    }

    public void save(ValueOutput output) {
        output.store("Stack", ItemStack.OPTIONAL_CODEC, stack);
        output.putByte("Direction", (byte) direction.get3DDataValue());
        output.putByte("OldDirection", (byte) oldDirection.get3DDataValue());
        output.putInt("Progress", progress);
        output.putInt("Speed", speed);
    }

    public ItemStack getStack() {
        return stack;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOldDirection() {
        return oldDirection;
    }

    public int getRawProgress() {
        return progress;
    }

    public int getSpeed() {
        return speed;
    }

    public float getProgress(float partialTick) {
        return Math.min(1.0F, (progress + partialTick) / (float) speed);
    }

    public boolean tickClient() {
        progress++;
        return progress > speed;
    }
}
