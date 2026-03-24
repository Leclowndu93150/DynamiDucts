package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.init.MNBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MNLootTableProvider extends LootTableProvider {

    public MNLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(MNBlockLoot::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class MNBlockLoot extends BlockLootSubProvider {

        protected MNBlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            MNBlocks.BLOCKS.getEntries().forEach(entry -> dropSelf(entry.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return MNBlocks.BLOCKS.getEntries().stream().map(e -> (Block) e.get())::iterator;
        }
    }
}
