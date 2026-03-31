package com.leclowndu93150.dynamiducts.core.network;

import com.leclowndu93150.dynamiducts.core.attachment.FilterLogic;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

public class RouteCache {

    private final Map<BlockPos, List<Route>> routesByOrigin = new HashMap<>();
    private boolean dirty = true;
    private int roundRobinIndex = 0;

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

    public List<Route> getSortedRoutes(ItemDuctUnit origin, Set<ItemDuctUnit> allNodes, int routeType) {
        List<Route> routes = getRoutes(origin, allNodes);
        if (routes.isEmpty()) return routes;

        if (routeType == FilterLogic.ROUTE_TYPE_NEAREST) {
            return routes;
        }

        List<Route> sorted = new ArrayList<>(routes);
        switch (routeType) {
            case FilterLogic.ROUTE_TYPE_FURTHEST -> sorted.sort(Comparator.comparingInt(r -> -r.pathWeight));
            case FilterLogic.ROUTE_TYPE_RANDOM -> Collections.shuffle(sorted);
            case FilterLogic.ROUTE_TYPE_ROUND_ROBIN -> {
                int offset = roundRobinIndex % sorted.size();
                roundRobinIndex++;
                List<Route> rotated = new ArrayList<>(sorted.size());
                for (int i = 0; i < sorted.size(); i++) {
                    rotated.add(sorted.get((i + offset) % sorted.size()));
                }
                sorted = rotated;
            }
        }
        return sorted;
    }

    public Route getRouteBetween(ItemDuctUnit from, ItemDuctUnit to, Direction insertionSide, Set<ItemDuctUnit> allNodes) {
        if (from.getPos().equals(to.getPos())) {
            return new Route(to.getPos(), insertionSide, List.of(), 0);
        }
        List<Route> routes = getRoutes(from, allNodes);
        Route fallback = null;
        for (Route route : routes) {
            if (!route.destination.equals(to.getPos())) continue;
            if (route.insertionSide == insertionSide) return route;
            if (fallback == null) fallback = route;
        }
        return fallback;
    }

    private void rebuildAll(Set<ItemDuctUnit> allNodes) {
        routesByOrigin.clear();
        for (ItemDuctUnit node : allNodes) {
            List<Route> routes = computeRoutes(node);
            if (!routes.isEmpty()) {
                routesByOrigin.put(node.getPos(), routes);
            }
        }
    }

    private List<Route> computeRoutes(ItemDuctUnit source) {
        List<Route> routes = new ArrayList<>();
        Map<BlockPos, Integer> visited = new HashMap<>();
        Deque<PathNode> queue = new ArrayDeque<>();

        visited.put(source.getPos(), 0);
        queue.add(new PathNode(source, null, null, 0));

        if (source.isNode()) {
            for (Direction dir : Direction.values()) {
                if (source.getTileCache(dir) != null) {
                    routes.add(new Route(source.getPos(), dir, List.of(), 0));
                }
            }
        }

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            if (current.unit != source && current.unit.isNode()) {
                List<Direction> path = buildPath(current);
                for (Direction dir : Direction.values()) {
                    if (current.unit.getTileCache(dir) != null) {
                        routes.add(new Route(current.unit.getPos(), dir, path, current.weight));
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
                queue.add(new PathNode(neighbor, current, dir, newWeight));
            }
        }

        routes.sort(Comparator.comparingInt(r -> r.pathWeight));
        return routes;
    }

    private static List<Direction> buildPath(PathNode node) {
        List<Direction> path = new ArrayList<>();
        PathNode current = node;
        while (current.parent != null) {
            path.add(current.direction);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static final class PathNode {
        final ItemDuctUnit unit;
        final PathNode parent;
        final Direction direction;
        final int weight;

        PathNode(ItemDuctUnit unit, PathNode parent, Direction direction, int weight) {
            this.unit = unit;
            this.parent = parent;
            this.direction = direction;
            this.weight = weight;
        }
    }
}
