package com.leclowndu93150.dynamiducts.duct.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public record TransportDirectoryEntry(BlockPos pos, String name, ItemStack icon) {
}
