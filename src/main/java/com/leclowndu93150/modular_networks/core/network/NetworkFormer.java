package com.leclowndu93150.modular_networks.core.network;

import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.Queue;

@SuppressWarnings("unchecked")
public class NetworkFormer {

    public static <T extends DuctUnit<T, G, ?>, G extends NetworkGrid<T>> void formGrid(T startUnit, ServerLevel level) {
        G grid = startUnit.createGrid(level);
        startUnit.setGrid(grid);
        grid.addBlock(startUnit);

        if (startUnit.isNode()) {
            grid.addNode(startUnit);
        }

        Queue<T> toCheck = new ArrayDeque<>();
        toCheck.add(startUnit);

        while (!toCheck.isEmpty()) {
            T current = toCheck.poll();

            for (Direction dir : Direction.values()) {
                T neighbor = current.getDuctNeighbor(dir);
                if (neighbor == null) continue;

                G neighborGrid = (G) neighbor.getGrid();

                if (neighborGrid == null) {
                    neighbor.setGrid(grid);
                    grid.addBlock(neighbor);
                    if (neighbor.isNode()) {
                        grid.addNode(neighbor);
                    }
                    toCheck.add(neighbor);
                } else if (neighborGrid != grid && neighborGrid.isValid()) {
                    if (grid.canGridsMerge(neighborGrid)) {
                        grid = mergeGrids(grid, neighborGrid);
                    }
                }
            }
        }

        grid.onMajorGridChange();
        NetworkManager.get(level).addGrid(grid);
    }

    private static <T extends DuctUnit<T, G, ?>, G extends NetworkGrid<T>> G mergeGrids(G target, G source) {
        if (source.size() > target.size()) {
            return mergeGrids(source, target);
        }

        NetworkManager.get(target.getLevel()).removeGrid(source);

        target.onMergeFrom(source);

        for (T unit : source.getNodeSet()) {
            unit.setGrid(target);
            target.addBlock(unit);
            target.addNode(unit);
        }
        for (T unit : source.getIdleSet()) {
            unit.setGrid(target);
            target.addBlock(unit);
        }

        source.getNodeSet().clear();
        source.getIdleSet().clear();

        return target;
    }
}
