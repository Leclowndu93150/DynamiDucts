package com.leclowndu93150.modular_networks.duct.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class TransportRoute {

    public final BlockPos destination;
    public final byte exitSide;
    public final List<Direction> path;
    public final int pathWeight;

    public TransportRoute(BlockPos destination, byte exitSide, List<Direction> path, int pathWeight) {
        this.destination = destination;
        this.exitSide = exitSide;
        this.path = List.copyOf(path);
        this.pathWeight = pathWeight;
    }
}
