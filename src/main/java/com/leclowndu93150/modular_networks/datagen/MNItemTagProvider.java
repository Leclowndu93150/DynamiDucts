package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.init.MNItems;
import com.leclowndu93150.modular_networks.init.MNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MNItemTagProvider extends ItemTagsProvider {

    public MNItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ModularNetworks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MNTags.INGOTS_LEAD).add(MNItems.LEAD_INGOT.get());
        tag(MNTags.INGOTS_TIN).add(MNItems.TIN_INGOT.get());
        tag(MNTags.INGOTS_SILVER).add(MNItems.SILVER_INGOT.get());
        tag(MNTags.INGOTS_INVAR).add(MNItems.INVAR_INGOT.get());
        tag(MNTags.INGOTS_ELECTRUM).add(MNItems.ELECTRUM_INGOT.get());
        tag(MNTags.INGOTS_BRONZE).add(MNItems.BRONZE_INGOT.get());
        tag(MNTags.INGOTS_SIGNALUM).add(MNItems.SIGNALUM_INGOT.get());
        tag(MNTags.INGOTS_ENDERIUM).add(MNItems.ENDERIUM_INGOT.get());
        tag(MNTags.INGOTS_LUMIUM).add(MNItems.LUMIUM_INGOT.get());

        tag(MNTags.NUGGETS_LEAD).add(MNItems.LEAD_NUGGET.get());
        tag(MNTags.NUGGETS_TIN).add(MNItems.TIN_NUGGET.get());
        tag(MNTags.NUGGETS_SILVER).add(MNItems.SILVER_NUGGET.get());
        tag(MNTags.NUGGETS_INVAR).add(MNItems.INVAR_NUGGET.get());
        tag(MNTags.NUGGETS_ELECTRUM).add(MNItems.ELECTRUM_NUGGET.get());
        tag(MNTags.NUGGETS_BRONZE).add(MNItems.BRONZE_NUGGET.get());
        tag(MNTags.NUGGETS_SIGNALUM).add(MNItems.SIGNALUM_NUGGET.get());
        tag(MNTags.NUGGETS_ENDERIUM).add(MNItems.ENDERIUM_NUGGET.get());
        tag(MNTags.NUGGETS_LUMIUM).add(MNItems.LUMIUM_NUGGET.get());

        tag(MNTags.HARDENED_GLASS).add(Items.TINTED_GLASS);
    }
}
