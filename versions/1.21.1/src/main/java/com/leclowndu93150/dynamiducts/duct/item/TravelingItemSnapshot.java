package com.leclowndu93150.dynamiducts.duct.item;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

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

    public static TravelingItemSnapshot load(CompoundTag tag, HolderLookup.Provider provider) {
        ItemStack stack = ItemStack.parseOptional(provider, tag.getCompound("Stack"));
        if (stack.isEmpty()) {
            return null;
        }

        Direction direction = Direction.from3DDataValue(tag.getByte("Direction"));
        Direction oldDirection = Direction.from3DDataValue(tag.getByte("OldDirection"));
        int progress = tag.getInt("Progress");
        int speed = tag.getInt("Speed");
        return new TravelingItemSnapshot(stack, direction, oldDirection, progress, speed);
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("Stack", stack.saveOptional(provider));
        tag.putByte("Direction", (byte) direction.get3DDataValue());
        tag.putByte("OldDirection", (byte) oldDirection.get3DDataValue());
        tag.putInt("Progress", progress);
        tag.putInt("Speed", speed);
        return tag;
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
