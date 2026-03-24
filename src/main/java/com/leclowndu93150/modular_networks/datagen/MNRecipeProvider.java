package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import com.leclowndu93150.modular_networks.recipe.CoverRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class MNRecipeProvider extends RecipeProvider {

    public MNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildEnergyDuctRecipes(output);
        buildFluidDuctRecipes(output);
        buildItemDuctRecipes(output);
        buildStructuralRecipes(output);
        buildTransportRecipes(output);
        buildAttachmentRecipes(output);

        SpecialRecipeBuilder.special(CoverRecipe::new)
                .save(output, ModularNetworks.MODID + ":cover_crafting");
    }

    private void buildEnergyDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_BASIC, 6)
                .pattern("RRR").pattern("IGI").pattern("RRR")
                .define('R', Items.REDSTONE).define('I', Items.IRON_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_redstone", has(Items.REDSTONE)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_HARDENED, 1)
                .requires(MNBlocks.ENERGY_DUCT_BASIC).requires(Items.IRON_INGOT).requires(Items.REDSTONE)
                .unlockedBy("has_basic", has(MNBlocks.ENERGY_DUCT_BASIC)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY, 6)
                .pattern("IGI")
                .define('I', Items.GOLD_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY, 1)
                .requires(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY).requires(Items.BLAZE_POWDER).requires(Items.REDSTONE)
                .unlockedBy("has_reinforced", has(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_RESONANT_EMPTY, 1)
                .requires(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY).requires(Items.ENDER_PEARL).requires(Items.REDSTONE)
                .unlockedBy("has_signalum", has(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, 6)
                .pattern("IGI").pattern("GEG").pattern("IGI")
                .define('I', Items.DIAMOND).define('G', Items.PACKED_ICE).define('E', MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)
                .unlockedBy("has_resonant", has(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_REINFORCED, 1)
                .requires(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY).requires(Items.REDSTONE).requires(Items.REDSTONE)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY)).save(output, key(MNBlocks.ENERGY_DUCT_REINFORCED) + "_from_empty");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SIGNALUM, 1)
                .requires(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY).requires(Items.REDSTONE).requires(Items.REDSTONE)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)).save(output, key(MNBlocks.ENERGY_DUCT_SIGNALUM) + "_from_empty");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_RESONANT, 1)
                .requires(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY).requires(Items.REDSTONE).requires(Items.REDSTONE)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)).save(output, key(MNBlocks.ENERGY_DUCT_RESONANT) + "_from_empty");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR, 1)
                .requires(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY).requires(Items.REDSTONE).requires(Items.REDSTONE).requires(Items.PACKED_ICE)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY)).save(output, key(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR) + "_from_empty");
    }

    private void buildFluidDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_BASIC, 6)
                .pattern("IGI")
                .define('I', Items.COPPER_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_BASIC_OPAQUE, 6)
                .pattern("ILI")
                .define('I', Items.COPPER_INGOT).define('L', Items.IRON_INGOT)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_HARDENED, 6)
                .pattern("IGI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_HARDENED_OPAQUE, 6)
                .pattern("ILI")
                .define('I', Items.IRON_INGOT).define('L', Items.IRON_NUGGET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_ENERGY, 1)
                .requires(MNBlocks.FLUID_DUCT_HARDENED).requires(Items.BLAZE_POWDER).requires(Items.GOLD_INGOT)
                .unlockedBy("has_hardened_fluid", has(MNBlocks.FLUID_DUCT_HARDENED)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_SUPER, 6)
                .pattern("IGI").pattern("GEG").pattern("IGI")
                .define('I', Items.GOLD_INGOT).define('G', Items.GLASS).define('E', MNBlocks.FLUID_DUCT_HARDENED)
                .unlockedBy("has_hardened_fluid", has(MNBlocks.FLUID_DUCT_HARDENED)).save(output);

        opaqueConversion(output, MNBlocks.FLUID_DUCT_BASIC, MNBlocks.FLUID_DUCT_BASIC_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_HARDENED, MNBlocks.FLUID_DUCT_HARDENED_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_ENERGY, MNBlocks.FLUID_DUCT_ENERGY_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_SUPER, MNBlocks.FLUID_DUCT_SUPER_OPAQUE);
    }

    private void buildItemDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_BASIC, 6)
                .pattern("IGI")
                .define('I', Items.IRON_NUGGET).define('G', Items.GLASS)
                .unlockedBy("has_glass", has(Items.GLASS)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_BASIC_OPAQUE, 6)
                .pattern("ILI")
                .define('I', Items.IRON_NUGGET).define('L', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_FAST, 1)
                .requires(MNBlocks.ITEM_DUCT_BASIC).requires(Items.GLOWSTONE_DUST).requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_basic_item", has(MNBlocks.ITEM_DUCT_BASIC)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_ENERGY, 1)
                .requires(MNBlocks.ITEM_DUCT_BASIC).requires(Items.BLAZE_POWDER).requires(Items.GOLD_INGOT)
                .unlockedBy("has_basic_item", has(MNBlocks.ITEM_DUCT_BASIC)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_ENERGY_FAST, 1)
                .requires(MNBlocks.ITEM_DUCT_ENERGY).requires(Items.GLOWSTONE_DUST).requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_energy_item", has(MNBlocks.ITEM_DUCT_ENERGY)).save(output);

        opaqueConversion(output, MNBlocks.ITEM_DUCT_BASIC, MNBlocks.ITEM_DUCT_BASIC_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_FAST, MNBlocks.ITEM_DUCT_FAST_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_ENERGY, MNBlocks.ITEM_DUCT_ENERGY_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_ENERGY_FAST, MNBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE);
    }

    private void buildStructuralRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.STRUCTURAL_DUCT, 6)
                .pattern("iIi")
                .define('i', Items.IRON_NUGGET).define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.LUX_DUCT, 1)
                .requires(MNBlocks.STRUCTURAL_DUCT).requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_structural", has(MNBlocks.STRUCTURAL_DUCT)).save(output);
    }

    private void buildTransportRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_FRAME, 4)
                .pattern("IGI").pattern("G G").pattern("IGI")
                .define('I', Items.GOLD_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_BASIC, 1)
                .requires(MNBlocks.TRANSPORT_DUCT_FRAME).requires(Items.ENDER_PEARL)
                .unlockedBy("has_frame", has(MNBlocks.TRANSPORT_DUCT_FRAME)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_LONG_RANGE, 8)
                .pattern("IGI").pattern("G G").pattern("IGI")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_LINKING, 1)
                .requires(MNBlocks.TRANSPORT_DUCT_BASIC).requires(Items.ENDER_EYE)
                .unlockedBy("has_transport", has(MNBlocks.TRANSPORT_DUCT_BASIC)).save(output);
    }

    private void buildAttachmentRecipes(RecipeOutput output) {
        buildTieredAttachment(output, MNItems.SERVO_BASIC, MNItems.SERVO_HARDENED, MNItems.SERVO_REINFORCED,
                MNItems.SERVO_SIGNALUM, MNItems.SERVO_RESONANT, Items.REDSTONE);

        buildTieredAttachment(output, MNItems.FILTER_BASIC, MNItems.FILTER_HARDENED, MNItems.FILTER_REINFORCED,
                MNItems.FILTER_SIGNALUM, MNItems.FILTER_RESONANT, Items.PAPER);

        buildTieredAttachment(output, MNItems.RETRIEVER_BASIC, MNItems.RETRIEVER_HARDENED, MNItems.RETRIEVER_REINFORCED,
                MNItems.RETRIEVER_SIGNALUM, MNItems.RETRIEVER_RESONANT, Items.ENDER_EYE);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNItems.RELAY, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.QUARTZ).define('I', Items.IRON_INGOT).define('R', Items.REDSTONE)
                .unlockedBy("has_structural", has(MNBlocks.STRUCTURAL_DUCT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MNItems.WRENCH, 1)
                .pattern("I I").pattern(" R ").pattern(" I ")
                .define('I', Items.IRON_INGOT).define('R', Items.IRON_NUGGET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);
    }

    private void buildTieredAttachment(RecipeOutput output, ItemLike basic, ItemLike hardened, ItemLike reinforced,
                                       ItemLike signalum, ItemLike resonant, ItemLike middleItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, basic, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', Items.IRON_INGOT).define('R', middleItem)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, hardened, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', Items.COPPER_INGOT).define('R', middleItem)
                .unlockedBy("has_basic", has(basic)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, hardened, 1)
                .requires(basic).requires(Items.COPPER_INGOT)
                .unlockedBy("has_basic", has(basic)).save(output, key(hardened) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, reinforced, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', Items.GOLD_INGOT).define('R', middleItem)
                .unlockedBy("has_hardened", has(hardened)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, reinforced, 1)
                .requires(hardened).requires(Items.GOLD_INGOT)
                .unlockedBy("has_hardened", has(hardened)).save(output, key(reinforced) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, signalum, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', Items.DIAMOND).define('R', middleItem)
                .unlockedBy("has_reinforced", has(reinforced)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, signalum, 1)
                .requires(reinforced).requires(Items.DIAMOND)
                .unlockedBy("has_reinforced", has(reinforced)).save(output, key(signalum) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resonant, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', Items.NETHERITE_SCRAP).define('R', middleItem)
                .unlockedBy("has_signalum", has(signalum)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, resonant, 1)
                .requires(signalum).requires(Items.NETHERITE_SCRAP)
                .unlockedBy("has_signalum", has(signalum)).save(output, key(resonant) + "_upgrade");
    }

    private void opaqueConversion(RecipeOutput output, ItemLike transparent, ItemLike opaque) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, opaque, 1)
                .requires(transparent).requires(Items.IRON_INGOT)
                .unlockedBy("has_transparent", has(transparent))
                .save(output, key(opaque) + "_from_transparent");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, transparent, 1)
                .requires(opaque).requires(Items.GLASS)
                .unlockedBy("has_opaque", has(opaque))
                .save(output, key(transparent) + "_from_opaque");
    }

    private String key(ItemLike item) {
        return ModularNetworks.MODID + ":" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }
}
