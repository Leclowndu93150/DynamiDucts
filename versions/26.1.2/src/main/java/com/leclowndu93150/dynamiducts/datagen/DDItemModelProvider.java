package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.client.renderer.DuctBlockItemRenderer;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import com.leclowndu93150.dynamiducts.init.DDItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.stream.Stream;

public class DDItemModelProvider extends ModelProvider {

    public DDItemModelProvider(PackOutput output) {
        super(output, DynamiDucts.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        DDBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof DuctBlock) {
                String name = entry.getId().getPath();
                String baseTex = DDBlockStateProvider.DUCT_TEXTURES.getOrDefault(name, "structure");
                Identifier texture = modLocation("block/duct/base/" + baseTex);

                builtinEntityItem(itemModels, block.asItem(), texture);
            }
        });

        simpleItem(itemModels, DDItems.SERVO_BASIC, "servo_0");
        simpleItem(itemModels, DDItems.SERVO_HARDENED, "servo_1");
        simpleItem(itemModels, DDItems.SERVO_REINFORCED, "servo_2");
        simpleItem(itemModels, DDItems.SERVO_SIGNALUM, "servo_3");
        simpleItem(itemModels, DDItems.SERVO_RESONANT, "servo_4");

        simpleItem(itemModels, DDItems.FILTER_BASIC, "filter_0");
        simpleItem(itemModels, DDItems.FILTER_HARDENED, "filter_1");
        simpleItem(itemModels, DDItems.FILTER_REINFORCED, "filter_2");
        simpleItem(itemModels, DDItems.FILTER_SIGNALUM, "filter_3");
        simpleItem(itemModels, DDItems.FILTER_RESONANT, "filter_4");

        simpleItem(itemModels, DDItems.RETRIEVER_BASIC, "retriever_0");
        simpleItem(itemModels, DDItems.RETRIEVER_HARDENED, "retriever_1");
        simpleItem(itemModels, DDItems.RETRIEVER_REINFORCED, "retriever_2");
        simpleItem(itemModels, DDItems.RETRIEVER_SIGNALUM, "retriever_3");
        simpleItem(itemModels, DDItems.RETRIEVER_RESONANT, "retriever_4");

        simpleItem(itemModels, DDItems.RELAY, "relay");
        simpleItem(itemModels, DDItems.WRENCH, "crescent_hammer");

        simpleItem(itemModels, DDItems.LEAD_INGOT, "ingot_lead");
        simpleItem(itemModels, DDItems.LEAD_NUGGET, "nugget_lead");
        simpleItem(itemModels, DDItems.TIN_INGOT, "ingot_tin");
        simpleItem(itemModels, DDItems.TIN_NUGGET, "nugget_tin");
        simpleItem(itemModels, DDItems.SILVER_INGOT, "ingot_silver");
        simpleItem(itemModels, DDItems.SILVER_NUGGET, "nugget_silver");
        simpleItem(itemModels, DDItems.INVAR_INGOT, "ingot_invar");
        simpleItem(itemModels, DDItems.INVAR_NUGGET, "nugget_invar");
        simpleItem(itemModels, DDItems.ELECTRUM_INGOT, "ingot_electrum");
        simpleItem(itemModels, DDItems.ELECTRUM_NUGGET, "nugget_electrum");
        simpleItem(itemModels, DDItems.BRONZE_INGOT, "ingot_bronze");
        simpleItem(itemModels, DDItems.BRONZE_NUGGET, "nugget_bronze");
        simpleItem(itemModels, DDItems.SIGNALUM_INGOT, "ingot_signalum");
        simpleItem(itemModels, DDItems.SIGNALUM_NUGGET, "nugget_signalum");
        simpleItem(itemModels, DDItems.ENDERIUM_INGOT, "ingot_enderium");
        simpleItem(itemModels, DDItems.ENDERIUM_NUGGET, "nugget_enderium");
        simpleItem(itemModels, DDItems.LUMIUM_INGOT, "ingot_lumium");
        simpleItem(itemModels, DDItems.LUMIUM_NUGGET, "nugget_lumium");
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    public String getName() {
        return "Item Models: " + modId;
    }

    private void simpleItem(ItemModelGenerators itemModels, DeferredItem<?> item, String texture) {
        Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item.get()),
                TextureMapping.layer0(new Material(modLocation("item/" + texture))),
                itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(model));
    }

    private void builtinEntityItem(ItemModelGenerators itemModels, Item item, Identifier particle) {
        Identifier model = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .transform(ItemDisplayContext.GUI, transform -> transform.rotation(30, 225, 0).scale(0.625F))
                .transform(ItemDisplayContext.GROUND, transform -> transform.translation(0, 3, 0).scale(0.25F))
                .transform(ItemDisplayContext.FIXED, transform -> transform.scale(0.5F))
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transform -> transform.translation(0, 2.5F, 0).rotation(75, 45, 0).scale(0.375F))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transform -> transform.translation(0, 2.5F, 0).rotation(75, 225, 0).scale(0.375F))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transform -> transform.rotation(0, 45, 0).scale(0.4F))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transform -> transform.rotation(0, 225, 0).scale(0.4F))
                .build()
                .create(ModelLocationUtils.getModelLocation(item),
                        TextureMapping.particle(new Material(particle)),
                        itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.specialModel(model, new DuctBlockItemRenderer.Unbaked()));
    }
}
