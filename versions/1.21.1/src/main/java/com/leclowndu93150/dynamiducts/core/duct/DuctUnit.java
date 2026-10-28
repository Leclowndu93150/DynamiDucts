package com.leclowndu93150.dynamiducts.core.duct;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.network.ConnectionType;
import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public abstract class DuctUnit<T extends DuctUnit<T, G, C>, G extends NetworkGrid<T>, C> {

    protected final DuctBlockEntity parent;
    protected final C[] tileCache;
    protected final T[] ductCache;
    private final BlockCapabilityCache<C, Direction>[] tileWatchers = new BlockCapabilityCache[6];
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

    protected BlockCapability<C, Direction> getTileCapability() {
        return null;
    }

    public C cacheTile(Direction side) {
        BlockCapability<C, Direction> capability = getTileCapability();
        if (capability == null || parent.getLevel() == null) return null;
        return parent.getLevel().getCapability(capability, parent.getBlockPos().relative(side), side.getOpposite());
    }

    private void watchTile(Direction side) {
        BlockCapability<C, Direction> capability = getTileCapability();
        if (capability == null || parent.isRemoved() || !(parent.getLevel() instanceof ServerLevel serverLevel)) return;
        BlockCapabilityCache<C, Direction> watcher = tileWatchers[side.ordinal()];
        if (watcher == null) {
            watcher = BlockCapabilityCache.create(
                    capability,
                    serverLevel,
                    parent.getBlockPos().relative(side),
                    side.getOpposite(),
                    () -> !parent.isRemoved(),
                    parent::requestNeighborRefresh
            );
            tileWatchers[side.ordinal()] = watcher;
        }
        watcher.getCapability();
    }

    public void clearTileWatchers() {
        Arrays.fill(tileWatchers, null);
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

        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            ductCache[dir.ordinal()] = null;
            tileCache[dir.ordinal()] = null;

            ConnectionType ct = parent.getConnectionType(dir);
            if (ct == ConnectionType.BLOCKED) continue;
            boolean tileAllowed = ct.allowsTransfer() || (ct.allowsEnergy() && getToken() == DuctToken.ENERGY);

            neighbor.setWithOffset(parent.getBlockPos(), dir);
            if (!parent.getLevel().isLoaded(neighbor)) {
                if (tileAllowed) watchTile(dir);
                continue;
            }

            if (parent.getLevel().getBlockEntity(neighbor) instanceof DuctBlockEntity otherDuct) {
                DuctUnit<?, ?, ?> otherUnit = otherDuct.getDuctUnit(getToken());
                if (otherUnit != null && canConnectTo((T) otherUnit)) {
                    ductCache[dir.ordinal()] = (T) otherUnit;
                    continue;
                }
            }

            if (tileAllowed) {
                watchTile(dir);
                C cached = cacheTile(dir);
                if (cached != null) {
                    tileCache[dir.ordinal()] = cached;
                    nodeMask |= (byte) (1 << dir.ordinal());
                    inputMask |= (byte) (1 << dir.ordinal());
                }
            }
        }
    }

    public boolean refreshCaches() {
        byte previousNodeMask = nodeMask;
        T[] previousDucts = ductCache.clone();
        updateCaches();
        return nodeMask != previousNodeMask || !Arrays.equals(previousDucts, ductCache);
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
