package com.leclowndu93150.modular_networks.core.attachment;

import net.minecraft.util.StringRepresentable;

public enum RedstoneMode implements StringRepresentable {
    IGNORED,
    HIGH,
    LOW,
    DISABLED;

    public boolean isActive(boolean powered) {
        return switch (this) {
            case IGNORED -> true;
            case HIGH -> powered;
            case LOW -> !powered;
            case DISABLED -> false;
        };
    }

    public RedstoneMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
