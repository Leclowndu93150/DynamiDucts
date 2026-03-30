package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class MNItemModelProvider extends ItemModelProvider {

    public MNItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModularNetworks.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        MNBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof DuctBlock) {
                String name = entry.getId().getPath();
                String baseTex = MNBlockStateProvider.DUCT_TEXTURES.getOrDefault(name, "structure");
                ResourceLocation texture = modLoc("block/duct/base/" + baseTex);

                getBuilder("item/" + name)
                        .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"))
                        .texture("particle", texture)
                        .transforms()
                        .transform(ItemDisplayContext.GUI).rotation(30, 225, 0).scale(0.625F).end()
                        .transform(ItemDisplayContext.GROUND).translation(0, 3, 0).scale(0.25F).end()
                        .transform(ItemDisplayContext.FIXED).scale(0.5F).end()
                        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).translation(0, 2.5F, 0).rotation(75, 45, 0).scale(0.375F).end()
                        .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).translation(0, 2.5F, 0).rotation(75, 225, 0).scale(0.375F).end()
                        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).scale(0.4F).end()
                        .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).scale(0.4F).end()
                        .end();
            }
        });

        simpleItem(MNItems.SERVO_BASIC, "servo_0");
        simpleItem(MNItems.SERVO_HARDENED, "servo_1");
        simpleItem(MNItems.SERVO_REINFORCED, "servo_2");
        simpleItem(MNItems.SERVO_SIGNALUM, "servo_3");
        simpleItem(MNItems.SERVO_RESONANT, "servo_4");

        simpleItem(MNItems.FILTER_BASIC, "filter_0");
        simpleItem(MNItems.FILTER_HARDENED, "filter_1");
        simpleItem(MNItems.FILTER_REINFORCED, "filter_2");
        simpleItem(MNItems.FILTER_SIGNALUM, "filter_3");
        simpleItem(MNItems.FILTER_RESONANT, "filter_4");

        simpleItem(MNItems.RETRIEVER_BASIC, "retriever_0");
        simpleItem(MNItems.RETRIEVER_HARDENED, "retriever_1");
        simpleItem(MNItems.RETRIEVER_REINFORCED, "retriever_2");
        simpleItem(MNItems.RETRIEVER_SIGNALUM, "retriever_3");
        simpleItem(MNItems.RETRIEVER_RESONANT, "retriever_4");

        simpleItem(MNItems.RELAY, "relay");
        builtinEntityItem(MNItems.COVER, modLoc("item/cover"));
        simpleItem(MNItems.WRENCH, "crescent_hammer");

        simpleItem(MNItems.LEAD_INGOT, "ingot_lead");
        simpleItem(MNItems.LEAD_NUGGET, "nugget_lead");
        simpleItem(MNItems.TIN_INGOT, "ingot_tin");
        simpleItem(MNItems.TIN_NUGGET, "nugget_tin");
        simpleItem(MNItems.SILVER_INGOT, "ingot_silver");
        simpleItem(MNItems.SILVER_NUGGET, "nugget_silver");
        simpleItem(MNItems.INVAR_INGOT, "ingot_invar");
        simpleItem(MNItems.INVAR_NUGGET, "nugget_invar");
        simpleItem(MNItems.ELECTRUM_INGOT, "ingot_electrum");
        simpleItem(MNItems.ELECTRUM_NUGGET, "nugget_electrum");
        simpleItem(MNItems.BRONZE_INGOT, "ingot_bronze");
        simpleItem(MNItems.BRONZE_NUGGET, "nugget_bronze");
        simpleItem(MNItems.SIGNALUM_INGOT, "ingot_signalum");
        simpleItem(MNItems.SIGNALUM_NUGGET, "nugget_signalum");
        simpleItem(MNItems.ENDERIUM_INGOT, "ingot_enderium");
        simpleItem(MNItems.ENDERIUM_NUGGET, "nugget_enderium");
        simpleItem(MNItems.LUMIUM_INGOT, "ingot_lumium");
        simpleItem(MNItems.LUMIUM_NUGGET, "nugget_lumium");
    }

    private void simpleItem(DeferredItem<?> item, String texture) {
        singleTexture("item/" + item.getId().getPath(),
                mcLoc("item/generated"),
                "layer0",
                modLoc("item/" + texture));
    }

    private void builtinEntityItem(DeferredItem<?> item, ResourceLocation particle) {
        getBuilder("item/" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"))
                .texture("particle", particle)
                .transforms()
                .transform(ItemDisplayContext.GUI).rotation(30, 225, 0).scale(0.625F).end()
                .transform(ItemDisplayContext.GROUND).translation(0, 3, 0).scale(0.25F).end()
                .transform(ItemDisplayContext.FIXED).scale(0.5F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).translation(0, 2.5F, 0).rotation(75, 45, 0).scale(0.375F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).translation(0, 2.5F, 0).rotation(75, 225, 0).scale(0.375F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).scale(0.4F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).scale(0.4F).end()
                .end();
    }
}
