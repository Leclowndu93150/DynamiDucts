package com.leclowndu93150.dynamiducts.duct.structural;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.server.level.ServerLevel;

public class StructuralDuctUnit extends DuctUnit<StructuralDuctUnit, StructuralGrid, Void> {

    public StructuralDuctUnit(DuctBlockEntity parent) {
        super(parent);
    }

    @Override
    protected Void[] createTileCacheArray() {
        return new Void[6];
    }

    @Override
    protected StructuralDuctUnit[] createDuctCacheArray() {
        return new StructuralDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.STRUCTURAL;
    }

    @Override
    public StructuralGrid createGrid(ServerLevel level) {
        return new StructuralGrid(level);
    }

    @Override
    public int getPathWeight() {
        return 1;
    }
}
