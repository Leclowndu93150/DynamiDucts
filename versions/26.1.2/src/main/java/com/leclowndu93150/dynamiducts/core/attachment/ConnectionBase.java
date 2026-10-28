package com.leclowndu93150.dynamiducts.core.attachment;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.init.DDItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public ItemStack getDrop() {
        if (isServo()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.SERVO_BASIC.get();
                case 1 -> DDItems.SERVO_HARDENED.get();
                case 2 -> DDItems.SERVO_REINFORCED.get();
                case 3 -> DDItems.SERVO_SIGNALUM.get();
                default -> DDItems.SERVO_RESONANT.get();
            });
        }
        if (isFilter()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.FILTER_BASIC.get();
                case 1 -> DDItems.FILTER_HARDENED.get();
                case 2 -> DDItems.FILTER_REINFORCED.get();
                case 3 -> DDItems.FILTER_SIGNALUM.get();
                default -> DDItems.FILTER_RESONANT.get();
            });
        }
        if (isRetriever()) {
            return new ItemStack(switch (tier.index()) {
                case 0 -> DDItems.RETRIEVER_BASIC.get();
                case 1 -> DDItems.RETRIEVER_HARDENED.get();
                case 2 -> DDItems.RETRIEVER_REINFORCED.get();
                case 3 -> DDItems.RETRIEVER_SIGNALUM.get();
                default -> DDItems.RETRIEVER_RESONANT.get();
            });
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("TierIndex", tier.index());
        output.putInt("RedstoneMode", redstoneMode.ordinal());
        output.putBoolean("IsPowered", isPowered);
        filter.save(output.child("Filter"));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        input.getInt("RedstoneMode").ifPresent(ordinal -> redstoneMode = readRedstoneMode(ordinal));
        isPowered = input.getBooleanOr("IsPowered", redstoneMode.isDisabled());
        input.child("Filter").ifPresent(filter::load);
    }

    private static RedstoneMode readRedstoneMode(int ordinal) {
        RedstoneMode[] modes = RedstoneMode.values();
        if (ordinal >= 0 && ordinal < modes.length) {
            return modes[ordinal];
        }
        return RedstoneMode.HIGH;
    }
}
