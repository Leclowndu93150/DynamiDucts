package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DDDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        var lookup = event.getLookupProvider();
        var helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new DDLanguageProvider(output));
        gen.addProvider(event.includeClient(), new DDBlockStateProvider(output, helper));
        gen.addProvider(event.includeClient(), new DDItemModelProvider(output, helper));

        var blockTags = new DDBlockTagProvider(output, lookup, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new DDItemTagProvider(output, lookup, blockTags.contentsGetter(), helper));
        gen.addProvider(event.includeServer(), new DDLootTableProvider(output, lookup));
        gen.addProvider(event.includeServer(), new DDRecipeProvider(output, lookup));
    }
}
