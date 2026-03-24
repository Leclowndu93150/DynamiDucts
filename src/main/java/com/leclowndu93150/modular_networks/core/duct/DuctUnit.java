package com.leclowndu93150.modular_networks.core.duct;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.network.ConnectionType;
import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("unchecked")
public abstract class DuctUnit<T extends DuctUnit<T, G, C>, G extends NetworkGrid<T>, C> {

    protected final DuctBlockEntity parent;
    protected final C[] tileCache;
    protected final T[] ductCache;
    protected G grid;
    protected byte nodeMask;
    protected byte inputMask;

    protected DuctUnit(DuctBlockEntity parent) {
        this.parent = parent;
        this.tileCache = createTileCacheArray();
        this.ductCache = createDuctCacheArray();
    }

    protected abstract C[] createTileCacheArray();

    protected abstract T[] createDuctCacheArray();

    public abstract DuctToken getToken();

    public abstract G createGrid(ServerLevel level);

    public abstract int getPathWeight();

    public C cacheTile(Direction side) {
        return null;
    }

    public boolean isNode() {
        return nodeMask != 0;
    }

    public boolean isInput(Direction side) {
        return (inputMask & (1 << side.ordinal())) != 0;
    }

    public void setGrid(G grid) {
        this.grid = grid;
    }

    public G getGrid() {
        return grid;
    }

    public DuctBlockEntity getParent() {
        return parent;
    }

    public BlockPos getPos() {
        return parent.getBlockPos();
    }

    public boolean canConnectTo(T other) {
        return true;
    }

    public boolean canConnectToTile(Direction side) {
        ConnectionType ct = parent.getConnectionType(side);
        return ct.allowsTransfer();
    }

    public void updateCaches() {
        nodeMask = 0;
        inputMask = 0;

        if (parent.getLevel() == null) return;

        for (Direction dir : Direction.values()) {
            ductCache[dir.ordinal()] = null;
            tileCache[dir.ordinal()] = null;

            ConnectionType ct = parent.getConnectionType(dir);
            if (ct == ConnectionType.BLOCKED) continue;

            BlockPos neighbor = parent.getBlockPos().relative(dir);
            if (!parent.getLevel().isLoaded(neighbor)) continue;

            if (parent.getLevel().getBlockEntity(neighbor) instanceof DuctBlockEntity otherDuct) {
                DuctUnit<?, ?, ?> otherUnit = otherDuct.getDuctUnit(getToken());
                if (otherUnit != null && canConnectTo((T) otherUnit)) {
                    ductCache[dir.ordinal()] = (T) otherUnit;
                    continue;
                }
            }

            if (ct.allowsTransfer() || (ct.allowsEnergy() && getToken() == DuctToken.ENERGY)) {
                C cached = cacheTile(dir);
                if (cached != null) {
                    tileCache[dir.ordinal()] = cached;
                    nodeMask |= (byte) (1 << dir.ordinal());
                    inputMask |= (byte) (1 << dir.ordinal());
                }
            }
        }
    }

    public boolean tickPass(int pass) {
        return true;
    }

    public void onGridChanged() {
    }

    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    }

    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    }

    public T getDuctNeighbor(Direction side) {
        return ductCache[side.ordinal()];
    }

    public C getTileCache(Direction side) {
        return tileCache[side.ordinal()];
    }

    public byte getNodeMask() {
        return nodeMask;
    }

    public void invalidate() {
        if (grid != null) {
            grid.removeBlock((T) this);
            grid = null;
        }
    }
}
