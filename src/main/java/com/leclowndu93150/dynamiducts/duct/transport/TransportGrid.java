package com.leclowndu93150.dynamiducts.duct.transport;

import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TransportGrid extends NetworkGrid<TransportDuctUnit> {

    private final TransportRouteCache routeCache = new TransportRouteCache();
    private final Set<TransportDuctUnit> endpoints = new LinkedHashSet<>();

    public TransportGrid(ServerLevel level) {
        super(level);
    }

    @Override
    public void onMajorGridChange() {
        super.onMajorGridChange();
        routeCache.markDirty();
        rebuildEndpoints();
    }

    @Override
    public void addNode(TransportDuctUnit unit) {
        super.addNode(unit);
        if (unit.isEndpoint()) endpoints.add(unit);
    }

    @Override
    public void removeBlock(TransportDuctUnit unit) {
        endpoints.remove(unit);
        super.removeBlock(unit);
    }

    @Override
    public void removeNode(TransportDuctUnit unit) {
        endpoints.remove(unit);
        super.removeNode(unit);
    }

    private void rebuildEndpoints() {
        endpoints.clear();
        for (TransportDuctUnit unit : nodeSet) {
            if (unit.isEndpoint()) endpoints.add(unit);
        }
        for (TransportDuctUnit unit : idleSet) {
            if (unit.isEndpoint()) endpoints.add(unit);
        }
    }

    public List<TransportRoute> getRoutesFrom(TransportDuctUnit origin) {
        Set<TransportDuctUnit> allUnits = new LinkedHashSet<>();
        allUnits.addAll(nodeSet);
        allUnits.addAll(idleSet);
        return routeCache.getRoutes(origin, allUnits);
    }

    public Set<TransportDuctUnit> getEndpoints() {
        return endpoints;
    }

    public TransportRouteCache getRouteCache() {
        return routeCache;
    }
}
