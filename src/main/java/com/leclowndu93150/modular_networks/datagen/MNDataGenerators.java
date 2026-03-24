package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModularNetworks.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MNDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        var lookup = event.getLookupProvider();
        var helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new MNLanguageProvider(output));
        gen.addProvider(event.includeClient(), new MNBlockStateProvider(output, helper));
        gen.addProvider(event.includeClient(), new MNItemModelProvider(output, helper));

        var blockTags = new MNBlockTagProvider(output, lookup, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new MNItemTagProvider(output, lookup, blockTags.contentsGetter(), helper));
        gen.addProvider(event.includeServer(), new MNLootTableProvider(output, lookup));
        gen.addProvider(event.includeServer(), new MNRecipeProvider(output, lookup));
    }
}
