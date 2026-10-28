package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;

public class DDBlockStateProvider extends BlockStateProvider {

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

    public DDBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DynamiDucts.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        DDBlocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof DuctBlock) {
                registerDuctBlock(entry);
            }
        });
    }

    private void registerDuctBlock(DeferredHolder<Block, ? extends Block> entry) {
        Block block = entry.get();
        String name = entry.getId().getPath();
        String baseTex = DUCT_TEXTURES.getOrDefault(name, "structure");
        ResourceLocation texture = modLoc("block/duct/base/" + baseTex);

        boolean transparentSpecial = name.equals("item_duct_dense") || name.equals("item_duct_vacuum");
        String renderType = (baseTex.contains("trans") || transparentSpecial) ? "cutout" : "solid";

        ModelFile center = models().getBuilder(name + "_center")
                .parent(new ModelFile.UncheckedModelFile("block/block"))
                .renderType(renderType)
                .texture("particle", texture)
                .texture("duct", texture)
                .element()
                .from(5, 5, 5).to(11, 11, 11)
                .face(Direction.DOWN).texture("#duct").uvs(5, 5, 11, 11).end()
                .face(Direction.UP).texture("#duct").uvs(5, 5, 11, 11).end()
                .face(Direction.NORTH).texture("#duct").uvs(5, 5, 11, 11).end()
                .face(Direction.SOUTH).texture("#duct").uvs(5, 5, 11, 11).end()
                .face(Direction.WEST).texture("#duct").uvs(5, 5, 11, 11).end()
                .face(Direction.EAST).texture("#duct").uvs(5, 5, 11, 11).end()
                .end();

        ModelFile armDown = armModel(name, "down", texture, renderType, 5, 0, 5, 11, 5, 11, Direction.DOWN, Direction.UP);
        ModelFile armUp = armModel(name, "up", texture, renderType, 5, 11, 5, 11, 16, 11, Direction.UP, Direction.DOWN);
        ModelFile armNorth = armModel(name, "north", texture, renderType, 5, 5, 0, 11, 11, 5, Direction.NORTH, Direction.SOUTH);
        ModelFile armSouth = armModel(name, "south", texture, renderType, 5, 5, 11, 11, 11, 16, Direction.SOUTH, Direction.NORTH);
        ModelFile armWest = armModel(name, "west", texture, renderType, 0, 5, 5, 5, 11, 11, Direction.WEST, Direction.EAST);
        ModelFile armEast = armModel(name, "east", texture, renderType, 11, 5, 5, 16, 11, 11, Direction.EAST, Direction.WEST);

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        builder.part().modelFile(center).addModel().end();
        builder.part().modelFile(armDown).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), true).end();
        builder.part().modelFile(armUp).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), true).end();
        builder.part().modelFile(armNorth).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.NORTH), true).end();
        builder.part().modelFile(armSouth).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.SOUTH), true).end();
        builder.part().modelFile(armWest).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.WEST), true).end();
        builder.part().modelFile(armEast).addModel().condition(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.EAST), true).end();
    }

    private ModelFile armModel(String ductName, String dirName, ResourceLocation texture, String renderType,
                                int x1, int y1, int z1, int x2, int y2, int z2,
                                Direction outward, Direction inward) {
        var builder = models().getBuilder(ductName + "_arm_" + dirName)
                .parent(new ModelFile.UncheckedModelFile("block/block"))
                .renderType(renderType)
                .texture("duct", texture)
                .element()
                .from(x1, y1, z1).to(x2, y2, z2);

        for (Direction face : Direction.values()) {
            if (face == outward || face == inward) continue;
            float[] uv = faceUVs(face, x1, y1, z1, x2, y2, z2);
            builder.face(face).texture("#duct").uvs(uv[0], uv[1], uv[2], uv[3]).end();
        }

        return builder.end();
    }

    private static float[] faceUVs(Direction face, int x1, int y1, int z1, int x2, int y2, int z2) {
        return switch (face) {
            case DOWN, UP -> new float[]{x1, z1, x2, z2};
            case NORTH, SOUTH -> new float[]{x1, 16 - y2, x2, 16 - y1};
            case WEST, EAST -> new float[]{z1, 16 - y2, z2, 16 - y1};
        };
    }
}
