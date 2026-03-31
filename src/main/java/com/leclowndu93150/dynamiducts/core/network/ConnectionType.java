package com.leclowndu93150.dynamiducts.core.network;

import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable {
    NORMAL(true, true),
    BLOCKED(false, false),
    ENERGY(false, true),
    FORCED(true, true);

    private final boolean allowTransfer;
    private final boolean allowEnergy;

    ConnectionType(boolean allowTransfer, boolean allowEnergy) {
        this.allowTransfer = allowTransfer;
        this.allowEnergy = allowEnergy;
    }

    public boolean allowsTransfer() {
        return allowTransfer;
    }

    public boolean allowsEnergy() {
        return allowEnergy;
    }

    public ConnectionType next() {
        return switch (this) {
            case NORMAL -> BLOCKED;
            case BLOCKED -> ENERGY;
            case ENERGY -> FORCED;
            case FORCED -> NORMAL;
        };
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
