package com.leclowndu93150.modular_networks.duct.transport;

import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;

public class TransportGrid extends NetworkGrid<TransportDuctUnit> {

    public TransportGrid(ServerLevel level) {
        super(level);
    }
}
