package com.leclowndu93150.dynamiducts.duct.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

public class TransportRouteCache {

    private final Map<BlockPos, List<TransportRoute>> routesByOrigin = new HashMap<>();
    private boolean dirty = true;

    public void markDirty() {
        dirty = true;
        routesByOrigin.clear();
    }

    public List<TransportRoute> getRoutes(TransportDuctUnit origin, Set<TransportDuctUnit> allEndpoints) {
        if (dirty) {
            rebuildAll(allEndpoints);
            dirty = false;
        }
        return routesByOrigin.getOrDefault(origin.getPos(), Collections.emptyList());
    }

    private void rebuildAll(Set<TransportDuctUnit> allEndpoints) {
        routesByOrigin.clear();
        for (TransportDuctUnit endpoint : allEndpoints) {
            if (!endpoint.isEndpoint()) continue;
            List<TransportRoute> routes = computeRoutes(endpoint);
            if (!routes.isEmpty()) {
                routesByOrigin.put(endpoint.getPos(), routes);
            }
        }
    }

    private List<TransportRoute> computeRoutes(TransportDuctUnit source) {
        List<TransportRoute> routes = new ArrayList<>();
        Map<BlockPos, Integer> visited = new HashMap<>();
        Deque<PathNode> queue = new ArrayDeque<>();

        visited.put(source.getPos(), 0);
        queue.add(new PathNode(source, null, null, 0));

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            if (current.unit != source && current.unit.isEndpoint()) {
                List<Direction> path = buildPath(current);
                routes.add(new TransportRoute(
                        current.unit.getPos(),
                        current.unit.getEndpointSide(),
                        path,
                        current.weight
                ));
            }

            for (Direction dir : Direction.values()) {
                TransportDuctUnit neighbor = current.unit.getDuctNeighbor(dir);
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
        final TransportDuctUnit unit;
        final PathNode parent;
        final Direction direction;
        final int weight;

        PathNode(TransportDuctUnit unit, PathNode parent, Direction direction, int weight) {
            this.unit = unit;
            this.parent = parent;
            this.direction = direction;
            this.weight = weight;
        }
    }
}
