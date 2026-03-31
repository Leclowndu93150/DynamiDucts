package com.leclowndu93150.dynamiducts.core.network;

import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class NetworkGrid<T extends DuctUnit<T, ?, ?>> {

    protected final ServerLevel level;
    protected final Set<T> nodeSet = new LinkedHashSet<>();
    protected final Set<T> idleSet = new LinkedHashSet<>();
    private boolean isValid = true;
    private boolean needsRecreate = false;

    protected NetworkGrid(ServerLevel level) {
        this.level = level;
    }

    public void addBlock(T unit) {
        idleSet.add(unit);
    }

    public void addNode(T unit) {
        idleSet.remove(unit);
        nodeSet.add(unit);
    }

    public void removeBlock(T unit) {
        nodeSet.remove(unit);
        idleSet.remove(unit);
        if (nodeSet.isEmpty() && idleSet.isEmpty()) {
            destroy();
        }
    }

    public void removeNode(T unit) {
        nodeSet.remove(unit);
        idleSet.add(unit);
    }

    public void destroy() {
        isValid = false;
        for (T unit : nodeSet) {
            unit.setGrid(null);
        }
        for (T unit : idleSet) {
            unit.setGrid(null);
        }
        nodeSet.clear();
        idleSet.clear();
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
    }

    public void onMinorGridChange() {
    }

    public void onMergeFrom(NetworkGrid<?> source) {
    }

    public boolean isFirstBlock(T block) {
        var iterator = nodeSet.iterator();
        return iterator.hasNext() && iterator.next() == block;
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
