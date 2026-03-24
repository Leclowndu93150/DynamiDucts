package com.leclowndu93150.modular_networks.core.attachment;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class ConnectionBase extends Attachment {

    protected final AttachmentTier tier;
    protected final FilterLogic filter;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORED;
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
    }

    public boolean isActive() {
        return redstoneMode.isActive(isPowered);
    }

    @Override
    public void tick() {
        Level level = parent.getLevel();
        if (level == null || level.isClientSide) return;

        isPowered = level.hasNeighborSignal(parent.getBlockPos());

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
        tag.put("Filter", filter.save(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("RedstoneMode")) {
            redstoneMode = RedstoneMode.values()[tag.getInt("RedstoneMode")];
        }
        if (tag.contains("Filter")) {
            filter.load(tag.getCompound("Filter"), provider);
        }
    }
}
