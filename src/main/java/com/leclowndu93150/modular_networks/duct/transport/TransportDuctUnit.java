package com.leclowndu93150.modular_networks.duct.transport;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import net.minecraft.server.level.ServerLevel;

public class TransportDuctUnit extends DuctUnit<TransportDuctUnit, TransportGrid, Void> {

    private final boolean longRange;

    public TransportDuctUnit(DuctBlockEntity parent, boolean longRange) {
        super(parent);
        this.longRange = longRange;
    }

    @Override
    protected Void[] createTileCacheArray() {
        return new Void[6];
    }

    @Override
    protected TransportDuctUnit[] createDuctCacheArray() {
        return new TransportDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.TRANSPORT;
    }

    @Override
    public TransportGrid createGrid(ServerLevel level) {
        return new TransportGrid(level);
    }

    @Override
    public int getPathWeight() {
        return longRange ? 0 : 1;
    }

    public boolean isLongRange() {
        return longRange;
    }
}
