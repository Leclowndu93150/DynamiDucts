package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID)
public class DDDataGenerators {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        event.createProvider(DDLanguageProvider::new);
        event.createProvider(DDBlockStateProvider::new);
        event.createProvider(DDItemModelProvider::new);

        addServerProviders(event);
    }

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        addServerProviders(event);
    }

    private static void addServerProviders(GatherDataEvent event) {
        event.createProvider(DDBlockTagProvider::new);
        event.createProvider(DDItemTagProvider::new);
        event.createProvider(DDLootTableProvider::new);
        event.createProvider(DDRecipeProvider.Runner::new);
    }
}
