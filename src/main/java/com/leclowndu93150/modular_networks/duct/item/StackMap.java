package com.leclowndu93150.modular_networks.duct.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StackMap {

    private final Map<BlockPos, Integer> inTransit = new HashMap<>();

    public void add(BlockPos dest, int count) {
        inTransit.merge(dest, count, Integer::sum);
    }

    public void remove(BlockPos dest, int count) {
        inTransit.computeIfPresent(dest, (k, v) -> {
            int result = v - count;
            return result <= 0 ? null : result;
        });
    }

    public int getCount(BlockPos dest) {
        return inTransit.getOrDefault(dest, 0);
    }

    public int getTotalCount() {
        return inTransit.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void clear() {
        inTransit.clear();
    }
}
