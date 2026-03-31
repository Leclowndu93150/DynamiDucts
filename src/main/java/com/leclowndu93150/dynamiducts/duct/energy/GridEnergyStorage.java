package com.leclowndu93150.dynamiducts.duct.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public class GridEnergyStorage extends EnergyStorage {

    public GridEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (energy > capacity) {
            energy = capacity;
        }
    }

    public void modifyEnergyStored(int amount) {
        energy = Math.max(0, Math.min(capacity, energy + amount));
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }
}
