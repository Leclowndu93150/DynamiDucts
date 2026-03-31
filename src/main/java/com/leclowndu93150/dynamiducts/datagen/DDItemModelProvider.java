package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import com.leclowndu93150.dynamiducts.init.DDItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class DDItemModelProvider extends ItemModelProvider {

    public DDItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DynamiDucts.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        DDBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof DuctBlock) {
                String name = entry.getId().getPath();
                String baseTex = DDBlockStateProvider.DUCT_TEXTURES.getOrDefault(name, "structure");
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

        simpleItem(DDItems.SERVO_BASIC, "servo_0");
        simpleItem(DDItems.SERVO_HARDENED, "servo_1");
        simpleItem(DDItems.SERVO_REINFORCED, "servo_2");
        simpleItem(DDItems.SERVO_SIGNALUM, "servo_3");
        simpleItem(DDItems.SERVO_RESONANT, "servo_4");

        simpleItem(DDItems.FILTER_BASIC, "filter_0");
        simpleItem(DDItems.FILTER_HARDENED, "filter_1");
        simpleItem(DDItems.FILTER_REINFORCED, "filter_2");
        simpleItem(DDItems.FILTER_SIGNALUM, "filter_3");
        simpleItem(DDItems.FILTER_RESONANT, "filter_4");

        simpleItem(DDItems.RETRIEVER_BASIC, "retriever_0");
        simpleItem(DDItems.RETRIEVER_HARDENED, "retriever_1");
        simpleItem(DDItems.RETRIEVER_REINFORCED, "retriever_2");
        simpleItem(DDItems.RETRIEVER_SIGNALUM, "retriever_3");
        simpleItem(DDItems.RETRIEVER_RESONANT, "retriever_4");

        simpleItem(DDItems.RELAY, "relay");
        builtinEntityItem(DDItems.COVER, modLoc("item/cover"));
        simpleItem(DDItems.WRENCH, "crescent_hammer");

        simpleItem(DDItems.LEAD_INGOT, "ingot_lead");
        simpleItem(DDItems.LEAD_NUGGET, "nugget_lead");
        simpleItem(DDItems.TIN_INGOT, "ingot_tin");
        simpleItem(DDItems.TIN_NUGGET, "nugget_tin");
        simpleItem(DDItems.SILVER_INGOT, "ingot_silver");
        simpleItem(DDItems.SILVER_NUGGET, "nugget_silver");
        simpleItem(DDItems.INVAR_INGOT, "ingot_invar");
        simpleItem(DDItems.INVAR_NUGGET, "nugget_invar");
        simpleItem(DDItems.ELECTRUM_INGOT, "ingot_electrum");
        simpleItem(DDItems.ELECTRUM_NUGGET, "nugget_electrum");
        simpleItem(DDItems.BRONZE_INGOT, "ingot_bronze");
        simpleItem(DDItems.BRONZE_NUGGET, "nugget_bronze");
        simpleItem(DDItems.SIGNALUM_INGOT, "ingot_signalum");
        simpleItem(DDItems.SIGNALUM_NUGGET, "nugget_signalum");
        simpleItem(DDItems.ENDERIUM_INGOT, "ingot_enderium");
        simpleItem(DDItems.ENDERIUM_NUGGET, "nugget_enderium");
        simpleItem(DDItems.LUMIUM_INGOT, "ingot_lumium");
        simpleItem(DDItems.LUMIUM_NUGGET, "nugget_lumium");
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
