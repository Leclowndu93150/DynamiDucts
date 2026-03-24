package com.leclowndu93150.modular_networks.duct.structural;

import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.network.NetworkGrid;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public class StructuralGrid extends NetworkGrid<StructuralDuctUnit> {

    private final int[] signalStrength = new int[16];
    private final List<Relay> inputRelays = new ArrayList<>();
    private final List<Relay> outputRelays = new ArrayList<>();

    public StructuralGrid(ServerLevel level) {
        super(level);
    }

    @Override
    public void tickGrid() {
        super.tickGrid();
        updateRelays();
        updateSignals();
        broadcastSignals();
    }

    private void updateRelays() {
        inputRelays.clear();
        outputRelays.clear();

        for (StructuralDuctUnit node : nodeSet) {
            Attachment[] attachments = node.getParent().getAttachments();
            if (attachments == null) continue;
            for (Attachment att : attachments) {
                if (att instanceof Relay relay) {
                    if (relay.isInput()) {
                        inputRelays.add(relay);
                    } else {
                        outputRelays.add(relay);
                    }
                }
            }
        }
    }

    private void updateSignals() {
        for (int i = 0; i < 16; i++) {
            signalStrength[i] = 0;
        }

        for (Relay relay : inputRelays) {
            int color = relay.getColor();
            int power = relay.readInputSignal();
            if (power > signalStrength[color]) {
                signalStrength[color] = power;
            }
        }
    }

    private void broadcastSignals() {
        for (Relay relay : outputRelays) {
            relay.outputSignal(signalStrength[relay.getColor()]);
        }
    }

    public int getSignal(int color) {
        if (color < 0 || color >= 16) return 0;
        return signalStrength[color];
    }
}
