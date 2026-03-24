package com.leclowndu93150.modular_networks.attachment.relay;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Relay extends Attachment {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "relay");

    public static final int TYPE_REDSTONE_INPUT = 0;
    public static final int TYPE_REDSTONE_OUTPUT = 1;
    public static final int TYPE_COMPARATOR_INPUT = 2;

    private int color;
    private int type = TYPE_REDSTONE_INPUT;
    private int invertMode;
    private int threshold;
    private int outputStrength;

    public Relay(DuctBlockEntity parent, Direction side) {
        super(parent, side);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isNode() {
        return true;
    }

    public boolean isInput() {
        return type == TYPE_REDSTONE_INPUT || type == TYPE_COMPARATOR_INPUT;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = Math.max(0, Math.min(15, color));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int readInputSignal() {
        Level level = parent.getLevel();
        if (level == null) return 0;

        BlockPos adjacent = parent.getBlockPos().relative(side);
        int signal;

        if (type == TYPE_COMPARATOR_INPUT) {
            signal = level.getBlockState(adjacent).getAnalogOutputSignal(level, adjacent);
        } else {
            signal = level.getSignal(adjacent, side);
        }

        return applyInversion(signal);
    }

    public void outputSignal(int strength) {
        this.outputStrength = applyInversion(strength);
    }

    public int getOutputStrength() {
        return outputStrength;
    }

    private int applyInversion(int signal) {
        return switch (invertMode) {
            case 0 -> signal;
            case 1 -> 15 - signal;
            case 2 -> signal >= threshold ? 15 : 0;
            case 3 -> signal >= threshold ? 0 : 15;
            default -> signal;
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("Color", color);
        tag.putInt("Type", type);
        tag.putInt("InvertMode", invertMode);
        tag.putInt("Threshold", threshold);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        color = tag.getInt("Color");
        type = tag.getInt("Type");
        invertMode = tag.getInt("InvertMode");
        threshold = tag.getInt("Threshold");
    }
}
