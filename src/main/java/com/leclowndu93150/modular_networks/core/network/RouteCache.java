package com.leclowndu93150.modular_networks.core.network;

import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

public class RouteCache {

    private final Map<BlockPos, List<Route>> routesByOrigin = new HashMap<>();
    private boolean dirty = true;

    public void markDirty() {
        dirty = true;
        routesByOrigin.clear();
    }

    public List<Route> getRoutes(ItemDuctUnit origin, Set<ItemDuctUnit> allNodes) {
        if (dirty) {
            rebuildAll(allNodes);
            dirty = false;
        }
        return routesByOrigin.getOrDefault(origin.getPos(), Collections.emptyList());
    }

    private void rebuildAll(Set<ItemDuctUnit> allNodes) {
        routesByOrigin.clear();
        for (ItemDuctUnit node : allNodes) {
            List<Route> routes = computeRoutes(node, allNodes);
            if (!routes.isEmpty()) {
                routesByOrigin.put(node.getPos(), routes);
            }
        }
    }

    private List<Route> computeRoutes(ItemDuctUnit source, Set<ItemDuctUnit> allNodes) {
        List<Route> routes = new ArrayList<>();
        Map<BlockPos, Integer> visited = new HashMap<>();
        Queue<PathNode> queue = new LinkedList<>();

        visited.put(source.getPos(), 0);
        queue.add(new PathNode(source, new ArrayList<>(), 0));

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            if (current.unit != source && current.unit.isNode()) {
                for (Direction dir : Direction.values()) {
                    if (current.unit.getTileCache(dir) != null) {
                        List<Direction> fullPath = new ArrayList<>(current.path);
                        routes.add(new Route(current.unit.getPos(), dir, fullPath, current.weight));
                    }
                }
            }

            for (Direction dir : Direction.values()) {
                ItemDuctUnit neighbor = current.unit.getDuctNeighbor(dir);
                if (neighbor == null) continue;

                int newWeight = current.weight + neighbor.getPathWeight();
                Integer existingWeight = visited.get(neighbor.getPos());
                if (existingWeight != null && existingWeight <= newWeight) continue;

                visited.put(neighbor.getPos(), newWeight);
                List<Direction> newPath = new ArrayList<>(current.path);
                newPath.add(dir);
                queue.add(new PathNode(neighbor, newPath, newWeight));
            }
        }

        routes.sort(Comparator.comparingInt(r -> r.pathWeight));
        return routes;
    }

    private record PathNode(ItemDuctUnit unit, List<Direction> path, int weight) {
    }
}
