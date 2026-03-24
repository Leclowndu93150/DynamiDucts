package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Map;

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
                        .parent(new ModelFile.UncheckedModelFile("block/block"))
                        .texture("particle", texture)
                        .texture("duct", texture)
                        .element()
                            .from(5, 0, 5).to(11, 16, 11)
                            .face(Direction.NORTH).texture("#duct").uvs(5, 0, 11, 16).end()
                            .face(Direction.SOUTH).texture("#duct").uvs(5, 0, 11, 16).end()
                            .face(Direction.WEST).texture("#duct").uvs(5, 0, 11, 16).end()
                            .face(Direction.EAST).texture("#duct").uvs(5, 0, 11, 16).end()
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
        simpleItem(MNItems.COVER, "cover");
        simpleItem(MNItems.WRENCH, "wrench");
    }

    private void simpleItem(DeferredItem<?> item, String texture) {
        singleTexture("item/" + item.getId().getPath(),
                mcLoc("item/generated"),
                "layer0",
                modLoc("item/" + texture));
    }
}
