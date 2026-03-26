package com.leclowndu93150.modular_networks.core.attachment;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class ConnectionBase extends Attachment {

    protected final AttachmentTier tier;
    protected final FilterLogic filter;
    protected RedstoneMode redstoneMode = RedstoneMode.HIGH;
    protected boolean isPowered;
    private int tickCounter;

    protected ConnectionBase(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side);
        this.tier = tier;
        this.filter = new FilterLogic(tier.filterSlots());
    }

    public AttachmentTier getTier() {
        return tier;
    }

    public FilterLogic getFilter() {
        return filter;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;
        Level level = parent.getLevel();
        if (level != null) {
            updatePowerState(level.hasNeighborSignal(parent.getBlockPos()));
        }
    }

    public boolean isActive() {
        return isPowered;
    }

    public boolean updatePowerState(boolean powered) {
        boolean wasPowered = isPowered;
        isPowered = redstoneMode.isActive(powered);
        return wasPowered != isPowered;
    }

    @Override
    public void tick() {
        if (!isActive()) return;

        tickCounter++;
        if (tickCounter >= tier.tickRate()) {
            tickCounter = 0;
            performAction();
        }
    }

    protected abstract void performAction();

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("TierIndex", tier.index());
        tag.putInt("RedstoneMode", redstoneMode.ordinal());
        tag.putBoolean("IsPowered", isPowered);
        tag.put("Filter", filter.save(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("RedstoneMode")) {
            redstoneMode = readRedstoneMode(tag.getInt("RedstoneMode"));
        }
        if (tag.contains("IsPowered")) {
            isPowered = tag.getBoolean("IsPowered");
        } else {
            isPowered = redstoneMode.isDisabled();
        }
        if (tag.contains("Filter")) {
            filter.load(tag.getCompound("Filter"), provider);
        }
    }

    private static RedstoneMode readRedstoneMode(int ordinal) {
        RedstoneMode[] modes = RedstoneMode.values();
        if (ordinal >= 0 && ordinal < modes.length) {
            return modes[ordinal];
        }
        return RedstoneMode.HIGH;
    }
}
