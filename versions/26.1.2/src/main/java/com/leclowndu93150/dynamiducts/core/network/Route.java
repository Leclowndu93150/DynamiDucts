package com.leclowndu93150.dynamiducts.core.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class Route {

    public final BlockPos destination;
    public final Direction insertionSide;
    public final List<Direction> path;
    public final int pathWeight;

    public Route(BlockPos destination, Direction insertionSide, List<Direction> path, int pathWeight) {
        this.destination = destination;
        this.insertionSide = insertionSide;
        this.path = List.copyOf(path);
        this.pathWeight = pathWeight;
    }

    public static class Builder {
        private final BlockPos destination;
        private final Direction insertionSide;
        private final List<Direction> path = new ArrayList<>();
        private int pathWeight;

        public Builder(BlockPos destination, Direction insertionSide) {
            this.destination = destination;
            this.insertionSide = insertionSide;
        }

        public Builder addStep(Direction dir) {
            path.add(dir);
            return this;
        }

        public Builder weight(int weight) {
            this.pathWeight = weight;
            return this;
        }

        public Route build() {
            return new Route(destination, insertionSide, path, pathWeight);
        }
    }
}
