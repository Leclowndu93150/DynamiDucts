package com.leclowndu93150.dynamiducts.duct.structural;

import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.network.NetworkGrid;
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
        beginTick();
        try {
            updateRelays();
            updateSignals();
            broadcastSignals();
        } finally {
            endTick();
        }
    }

    private void updateRelays() {
        inputRelays.clear();
        outputRelays.clear();

        collectRelaysFrom(getNodeSnapshot());
        collectRelaysFrom(getIdleSnapshot());
    }

    private void collectRelaysFrom(List<StructuralDuctUnit> units) {
        for (StructuralDuctUnit unit : units) {
            Attachment[] attachments = unit.getParent().getAttachments();
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
