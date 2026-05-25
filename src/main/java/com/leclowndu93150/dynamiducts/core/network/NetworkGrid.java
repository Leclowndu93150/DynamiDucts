package com.leclowndu93150.dynamiducts.core.network;

import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class NetworkGrid<T extends DuctUnit<T, ?, ?>> {

    protected final ServerLevel level;
    protected final Set<T> nodeSet = new ReferenceOpenHashSet<>();
    protected final Set<T> idleSet = new ReferenceOpenHashSet<>();
    private boolean isValid = true;
    private boolean needsRecreate = false;

    private boolean ticking = false;
    private List<Runnable> deferred;

    private List<T> nodeSnapshot;
    private List<T> idleSnapshot;
    private boolean nodesDirty = true;
    private boolean idlesDirty = true;
    private static final List<?> EMPTY = List.of();

    protected NetworkGrid(ServerLevel level) {
        this.level = level;
    }

    public void addBlock(T unit) {
        if (ticking) {
            defer(() -> addBlock(unit));
            return;
        }
        idleSet.add(unit);
        idlesDirty = true;
    }

    public void addNode(T unit) {
        if (ticking) {
            defer(() -> addNode(unit));
            return;
        }
        idleSet.remove(unit);
        nodeSet.add(unit);
        nodesDirty = true;
        idlesDirty = true;
    }

    public void removeBlock(T unit) {
        if (ticking) {
            defer(() -> removeBlock(unit));
            return;
        }
        nodeSet.remove(unit);
        idleSet.remove(unit);
        nodesDirty = true;
        idlesDirty = true;
        if (nodeSet.isEmpty() && idleSet.isEmpty()) {
            destroy();
        }
    }

    public void removeNode(T unit) {
        if (ticking) {
            defer(() -> removeNode(unit));
            return;
        }
        nodeSet.remove(unit);
        idleSet.add(unit);
        nodesDirty = true;
        idlesDirty = true;
    }

    public void destroy() {
        isValid = false;
        for (T unit : getNodeSnapshot()) {
            unit.setGrid(null);
        }
        for (T unit : getIdleSnapshot()) {
            unit.setGrid(null);
        }
        nodeSet.clear();
        idleSet.clear();
        nodesDirty = true;
        idlesDirty = true;
    }

    @SuppressWarnings("unchecked")
    public List<T> getNodeSnapshot() {
        if (nodesDirty) {
            nodeSnapshot = new ArrayList<>(nodeSet);
            nodesDirty = false;
        }
        return nodeSnapshot != null ? nodeSnapshot : (List<T>) EMPTY;
    }

    @SuppressWarnings("unchecked")
    public List<T> getIdleSnapshot() {
        if (idlesDirty) {
            idleSnapshot = new ArrayList<>(idleSet);
            idlesDirty = false;
        }
        return idleSnapshot != null ? idleSnapshot : (List<T>) EMPTY;
    }

    protected void beginTick() {
        ticking = true;
    }

    protected void endTick() {
        ticking = false;
        if (deferred != null && !deferred.isEmpty()) {
            List<Runnable> pending = new ArrayList<>(deferred);
            deferred.clear();
            for (Runnable r : pending) r.run();
        }
    }

    private void defer(Runnable action) {
        if (deferred == null) deferred = new ArrayList<>();
        deferred.add(action);
    }

    public void tickGrid() {
    }

    public void balanceGrid() {
    }

    public boolean canAddBlock(T block) {
        return true;
    }

    public boolean canGridsMerge(NetworkGrid<?> other) {
        return this.getClass() == other.getClass();
    }

    public void onMajorGridChange() {
        balanceGrid();
        nodesDirty = true;
        idlesDirty = true;
    }

    public void onMinorGridChange() {
    }

    public void onMergeFrom(NetworkGrid<?> source) {
    }

    public boolean isFirstBlock(T block) {
        List<T> snap = getNodeSnapshot();
        return !snap.isEmpty() && snap.getFirst() == block;
    }

    public boolean isValid() {
        return isValid;
    }

    public void markForRecreation() {
        needsRecreate = true;
    }

    public boolean needsRecreation() {
        return needsRecreate;
    }

    public void clearRecreationFlag() {
        needsRecreate = false;
    }

    public Set<T> getNodeSet() {
        return nodeSet;
    }

    public Set<T> getIdleSet() {
        return idleSet;
    }

    public int size() {
        return nodeSet.size() + idleSet.size();
    }

    public ServerLevel getLevel() {
        return level;
    }
}
