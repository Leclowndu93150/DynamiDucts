package com.leclowndu93150.modular_networks.core.attachment;

import net.minecraft.util.StringRepresentable;

public enum RedstoneMode implements StringRepresentable {
    DISABLED(true),
    LOW(false),
    HIGH(true);

    private final boolean state;

    RedstoneMode(boolean state) {
        this.state = state;
    }

    public boolean isActive(boolean powered) {
        return isDisabled() || powered == state;
    }

    public boolean isDisabled() {
        return this == DISABLED;
    }

    public boolean isLow() {
        return this == LOW;
    }

    public boolean isHigh() {
        return this == HIGH;
    }

    public boolean getState() {
        return state;
    }

    public RedstoneMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
