package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DDBlockStateProvider extends ModelProvider {

    public static final Map<String, String> DUCT_TEXTURES = new HashMap<>();

    static {
        DUCT_TEXTURES.put("energy_duct_basic", "lead");
        DUCT_TEXTURES.put("energy_duct_hardened", "invar");
        DUCT_TEXTURES.put("energy_duct_reinforced", "electrum");
        DUCT_TEXTURES.put("energy_duct_signalum", "signalum");
        DUCT_TEXTURES.put("energy_duct_resonant", "enderium");
        DUCT_TEXTURES.put("energy_duct_superconductor", "enderium");
        DUCT_TEXTURES.put("energy_duct_reinforced_empty", "electrum");
        DUCT_TEXTURES.put("energy_duct_signalum_empty", "signalum");
        DUCT_TEXTURES.put("energy_duct_resonant_empty", "enderium");
        DUCT_TEXTURES.put("energy_duct_superconductor_empty", "enderium");

        DUCT_TEXTURES.put("fluid_duct_basic", "copper_trans");
        DUCT_TEXTURES.put("fluid_duct_basic_opaque", "copper");
        DUCT_TEXTURES.put("fluid_duct_hardened", "invar_trans");
        DUCT_TEXTURES.put("fluid_duct_hardened_opaque", "invar");
        DUCT_TEXTURES.put("fluid_duct_energy", "invar_signalum_trans");
        DUCT_TEXTURES.put("fluid_duct_energy_opaque", "invar_signalum");
        DUCT_TEXTURES.put("fluid_duct_super", "invar_trans");
        DUCT_TEXTURES.put("fluid_duct_super_opaque", "invar");

        DUCT_TEXTURES.put("item_duct_basic", "tin_trans");
        DUCT_TEXTURES.put("item_duct_basic_opaque", "tin");
        DUCT_TEXTURES.put("item_duct_dense", "tin_trans");
        DUCT_TEXTURES.put("item_duct_dense_opaque", "tin");
        DUCT_TEXTURES.put("item_duct_vacuum", "tin_trans");
        DUCT_TEXTURES.put("item_duct_vacuum_opaque", "tin");
        DUCT_TEXTURES.put("item_duct_fast", "tin_trans");
        DUCT_TEXTURES.put("item_duct_fast_opaque", "tin_alt");
        DUCT_TEXTURES.put("item_duct_energy", "tin_signalum_trans");
        DUCT_TEXTURES.put("item_duct_energy_opaque", "tin_signalum");
        DUCT_TEXTURES.put("item_duct_energy_fast", "tin_signalum_trans");
        DUCT_TEXTURES.put("item_duct_energy_fast_opaque", "tin_alt_signalum");

        DUCT_TEXTURES.put("transport_duct_basic", "copper");
        DUCT_TEXTURES.put("transport_duct_long_range", "lead");
        DUCT_TEXTURES.put("transport_duct_linking", "enderium");
        DUCT_TEXTURES.put("transport_duct_frame", "copper");

        DUCT_TEXTURES.put("structural_duct", "structure");
        DUCT_TEXTURES.put("lux_duct", "lumium");
    }

    public DDBlockStateProvider(PackOutput output) {
        super(output, DynamiDucts.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        DDBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof DuctBlock) {
                registerDuctBlock(blockModels, entry);
            }
        });
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    public String getName() {
        return "Block States: " + modId;
    }

    private void registerDuctBlock(BlockModelGenerators blockModels, DeferredHolder<Block, ? extends Block> entry) {
        Block block = entry.get();
        String baseTex = DUCT_TEXTURES.getOrDefault(entry.getId().getPath(), "structure");
        Material texture = new Material(modLocation("block/duct/base/" + baseTex));
        Identifier model = ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(texture), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
    }
}
