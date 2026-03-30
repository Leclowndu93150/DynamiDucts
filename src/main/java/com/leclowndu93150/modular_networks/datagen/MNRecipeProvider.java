package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import com.leclowndu93150.modular_networks.init.MNTags;
import com.leclowndu93150.modular_networks.recipe.CoverRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class MNRecipeProvider extends RecipeProvider {

    public MNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildMetalRecipes(output);
        buildEnergyDuctRecipes(output);
        buildFluidDuctRecipes(output);
        buildItemDuctRecipes(output);
        buildStructuralRecipes(output);
        buildTransportRecipes(output);
        buildAttachmentRecipes(output);

        SpecialRecipeBuilder.special(CoverRecipe::new)
                .save(output, ModularNetworks.MODID + ":cover_crafting");
    }

    private void buildMetalRecipes(RecipeOutput output) {
        ingotNugget(output, MNItems.LEAD_INGOT, MNItems.LEAD_NUGGET, "lead");
        ingotNugget(output, MNItems.TIN_INGOT, MNItems.TIN_NUGGET, "tin");
        ingotNugget(output, MNItems.SILVER_INGOT, MNItems.SILVER_NUGGET, "silver");
        ingotNugget(output, MNItems.INVAR_INGOT, MNItems.INVAR_NUGGET, "invar");
        ingotNugget(output, MNItems.ELECTRUM_INGOT, MNItems.ELECTRUM_NUGGET, "electrum");
        ingotNugget(output, MNItems.BRONZE_INGOT, MNItems.BRONZE_NUGGET, "bronze");
        ingotNugget(output, MNItems.SIGNALUM_INGOT, MNItems.SIGNALUM_NUGGET, "signalum");
        ingotNugget(output, MNItems.ENDERIUM_INGOT, MNItems.ENDERIUM_NUGGET, "enderium");
        ingotNugget(output, MNItems.LUMIUM_INGOT, MNItems.LUMIUM_NUGGET, "lumium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.LEAD_INGOT, 2)
                .requires(Items.IRON_INGOT).requires(Items.CHARCOAL)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, id("lead_ingot_from_iron"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.TIN_INGOT, 2)
                .requires(Items.IRON_INGOT).requires(Items.QUARTZ)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, id("tin_ingot_from_iron"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.SILVER_INGOT, 2)
                .requires(Items.IRON_INGOT).requires(Items.GLOWSTONE_DUST)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, id("silver_ingot_from_iron"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.INVAR_INGOT, 3)
                .requires(Items.IRON_INGOT).requires(Items.IRON_INGOT).requires(Items.REDSTONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, id("invar_ingot_from_iron"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.ELECTRUM_INGOT, 2)
                .requires(Items.GOLD_INGOT).requires(MNTags.INGOTS_SILVER)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(output, id("electrum_ingot_from_gold"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MNItems.BRONZE_INGOT, 4)
                .requires(Items.COPPER_INGOT).requires(Items.COPPER_INGOT).requires(Items.COPPER_INGOT).requires(MNTags.INGOTS_TIN)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output, id("bronze_ingot_from_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MNItems.SIGNALUM_INGOT, 4)
                .pattern("RCR").pattern("CSC").pattern("RCR")
                .define('R', Items.REDSTONE).define('C', Items.COPPER_INGOT).define('S', MNTags.INGOTS_SILVER)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output, id("signalum_ingot_craft"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MNItems.ENDERIUM_INGOT, 4)
                .pattern("ETE").pattern("TST").pattern("ETE")
                .define('E', Items.ENDER_PEARL).define('T', MNTags.INGOTS_TIN).define('S', MNTags.INGOTS_SILVER)
                .unlockedBy("has_ender", has(Items.ENDER_PEARL)).save(output, id("enderium_ingot_craft"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MNItems.LUMIUM_INGOT, 4)
                .pattern("GTG").pattern("TST").pattern("GTG")
                .define('G', Items.GLOWSTONE_DUST).define('T', MNTags.INGOTS_TIN).define('S', MNTags.INGOTS_SILVER)
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE_DUST)).save(output, id("lumium_ingot_craft"));
    }

    private void buildEnergyDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_BASIC, 6)
                .pattern("RRR").pattern("IGI").pattern("RRR")
                .define('R', Items.REDSTONE).define('I', MNTags.INGOTS_LEAD).define('G', Items.GLASS)
                .unlockedBy("has_lead", has(MNTags.INGOTS_LEAD)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_HARDENED, 1)
                .requires(MNBlocks.ENERGY_DUCT_BASIC).requires(MNTags.NUGGETS_INVAR)
                .requires(MNTags.NUGGETS_INVAR).requires(MNTags.NUGGETS_INVAR).requires(Items.REDSTONE)
                .unlockedBy("has_basic", has(MNBlocks.ENERGY_DUCT_BASIC)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY, 6)
                .pattern("IGI")
                .define('I', MNTags.INGOTS_ELECTRUM).define('G', MNTags.HARDENED_GLASS)
                .unlockedBy("has_electrum", has(MNTags.INGOTS_ELECTRUM)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY, 1)
                .requires(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY).requires(MNTags.NUGGETS_SIGNALUM)
                .requires(MNTags.NUGGETS_SIGNALUM).requires(MNTags.NUGGETS_SIGNALUM).requires(Items.REDSTONE)
                .unlockedBy("has_reinforced", has(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_RESONANT_EMPTY, 1)
                .requires(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY).requires(MNTags.NUGGETS_ENDERIUM)
                .requires(MNTags.NUGGETS_ENDERIUM).requires(MNTags.NUGGETS_ENDERIUM).requires(Items.REDSTONE)
                .unlockedBy("has_signalum", has(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, 6)
                .pattern("IGI").pattern("GEG").pattern("IGI")
                .define('I', MNTags.INGOTS_ELECTRUM).define('G', MNTags.HARDENED_GLASS).define('E', MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)
                .unlockedBy("has_resonant", has(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_REINFORCED, 1)
                .requires(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY).requires(Items.REDSTONE_BLOCK)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY)).save(output, id("energy_duct_reinforced_from_empty"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SIGNALUM, 1)
                .requires(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY).requires(Items.REDSTONE_BLOCK)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)).save(output, id("energy_duct_signalum_from_empty"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_RESONANT, 1)
                .requires(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY).requires(Items.REDSTONE_BLOCK)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY)).save(output, id("energy_duct_resonant_from_empty"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR, 1)
                .requires(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY).requires(Items.BLUE_ICE)
                .unlockedBy("has_empty", has(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY)).save(output, id("energy_duct_superconductor_from_empty"));
    }

    private void buildFluidDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_BASIC, 6)
                .pattern("IGI")
                .define('I', Items.COPPER_INGOT).define('G', Items.GLASS)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_BASIC_OPAQUE, 6)
                .pattern("ILI")
                .define('I', Items.COPPER_INGOT).define('L', MNTags.INGOTS_LEAD)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_HARDENED, 6)
                .pattern("IGI")
                .define('I', MNTags.INGOTS_INVAR).define('G', MNTags.HARDENED_GLASS)
                .unlockedBy("has_invar", has(MNTags.INGOTS_INVAR)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_HARDENED_OPAQUE, 6)
                .pattern("ILI")
                .define('I', MNTags.INGOTS_INVAR).define('L', MNTags.INGOTS_LEAD)
                .unlockedBy("has_invar", has(MNTags.INGOTS_INVAR)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_ENERGY, 1)
                .requires(MNBlocks.FLUID_DUCT_HARDENED).requires(MNTags.NUGGETS_SIGNALUM)
                .requires(MNTags.NUGGETS_SIGNALUM).requires(MNTags.NUGGETS_SIGNALUM)
                .requires(MNTags.NUGGETS_ELECTRUM).requires(MNTags.NUGGETS_ELECTRUM).requires(MNTags.NUGGETS_ELECTRUM)
                .unlockedBy("has_hardened", has(MNBlocks.FLUID_DUCT_HARDENED)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.FLUID_DUCT_SUPER, 6)
                .pattern("IGI").pattern("GEG").pattern("IGI")
                .define('I', MNTags.INGOTS_BRONZE).define('G', MNTags.HARDENED_GLASS).define('E', MNBlocks.FLUID_DUCT_HARDENED)
                .unlockedBy("has_hardened", has(MNBlocks.FLUID_DUCT_HARDENED)).save(output);

        opaqueConversion(output, MNBlocks.FLUID_DUCT_BASIC, MNBlocks.FLUID_DUCT_BASIC_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_HARDENED, MNBlocks.FLUID_DUCT_HARDENED_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_ENERGY, MNBlocks.FLUID_DUCT_ENERGY_OPAQUE);
        opaqueConversion(output, MNBlocks.FLUID_DUCT_SUPER, MNBlocks.FLUID_DUCT_SUPER_OPAQUE);
    }

    private void buildItemDuctRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_BASIC, 6)
                .pattern("IGI")
                .define('I', MNTags.INGOTS_TIN).define('G', MNTags.HARDENED_GLASS)
                .unlockedBy("has_tin", has(MNTags.INGOTS_TIN)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_BASIC_OPAQUE, 6)
                .pattern("ILI")
                .define('I', MNTags.INGOTS_TIN).define('L', MNTags.INGOTS_LEAD)
                .unlockedBy("has_tin", has(MNTags.INGOTS_TIN)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_FAST, 1)
                .requires(MNBlocks.ITEM_DUCT_BASIC).requires(Items.GLOWSTONE)
                .unlockedBy("has_basic", has(MNBlocks.ITEM_DUCT_BASIC)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_ENERGY, 1)
                .requires(MNBlocks.ITEM_DUCT_BASIC).requires(MNTags.NUGGETS_SIGNALUM)
                .requires(MNTags.NUGGETS_SIGNALUM).requires(MNTags.NUGGETS_SIGNALUM)
                .requires(MNTags.NUGGETS_ELECTRUM).requires(MNTags.NUGGETS_ELECTRUM).requires(MNTags.NUGGETS_ELECTRUM)
                .unlockedBy("has_basic", has(MNBlocks.ITEM_DUCT_BASIC)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.ITEM_DUCT_ENERGY_FAST, 1)
                .requires(MNBlocks.ITEM_DUCT_ENERGY).requires(Items.GLOWSTONE)
                .unlockedBy("has_energy", has(MNBlocks.ITEM_DUCT_ENERGY)).save(output);

        opaqueConversion(output, MNBlocks.ITEM_DUCT_BASIC, MNBlocks.ITEM_DUCT_BASIC_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_FAST, MNBlocks.ITEM_DUCT_FAST_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_ENERGY, MNBlocks.ITEM_DUCT_ENERGY_OPAQUE);
        opaqueConversion(output, MNBlocks.ITEM_DUCT_ENERGY_FAST, MNBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE);
    }

    private void buildStructuralRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.STRUCTURAL_DUCT, 6)
                .pattern("iIi")
                .define('i', Items.IRON_NUGGET).define('I', MNTags.INGOTS_LEAD)
                .unlockedBy("has_lead", has(MNTags.INGOTS_LEAD)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.LUX_DUCT, 1)
                .requires(MNBlocks.STRUCTURAL_DUCT).requires(MNTags.INGOTS_LUMIUM)
                .unlockedBy("has_lumium", has(MNTags.INGOTS_LUMIUM)).save(output);
    }

    private void buildTransportRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_FRAME, 4)
                .pattern("IGI").pattern("G G").pattern("IGI")
                .define('I', MNTags.INGOTS_BRONZE).define('G', MNTags.HARDENED_GLASS)
                .unlockedBy("has_bronze", has(MNTags.INGOTS_BRONZE)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_BASIC, 1)
                .requires(MNBlocks.TRANSPORT_DUCT_FRAME).requires(Items.PHANTOM_MEMBRANE)
                .unlockedBy("has_frame", has(MNBlocks.TRANSPORT_DUCT_FRAME)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, MNBlocks.TRANSPORT_DUCT_LONG_RANGE, 8)
                .pattern("IGI").pattern("G G").pattern("IGI")
                .define('I', MNTags.INGOTS_LEAD).define('G', MNTags.HARDENED_GLASS)
                .unlockedBy("has_lead", has(MNTags.INGOTS_LEAD)).save(output);

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
                .define('i', MNTags.NUGGETS_SIGNALUM).define('G', Items.QUARTZ).define('I', MNTags.INGOTS_LEAD).define('R', Items.REDSTONE)
                .unlockedBy("has_lead", has(MNTags.INGOTS_LEAD)).save(output);

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
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', MNTags.INGOTS_INVAR).define('R', middleItem)
                .unlockedBy("has_invar", has(MNTags.INGOTS_INVAR)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, hardened, 1)
                .requires(basic).requires(MNTags.INGOTS_INVAR)
                .unlockedBy("has_basic", has(basic)).save(output, key(hardened) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, reinforced, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', MNTags.INGOTS_ELECTRUM).define('R', middleItem)
                .unlockedBy("has_electrum", has(MNTags.INGOTS_ELECTRUM)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, reinforced, 1)
                .requires(hardened).requires(MNTags.INGOTS_ELECTRUM)
                .unlockedBy("has_hardened", has(hardened)).save(output, key(reinforced) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, signalum, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', MNTags.INGOTS_SIGNALUM).define('R', middleItem)
                .unlockedBy("has_signalum", has(MNTags.INGOTS_SIGNALUM)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, signalum, 1)
                .requires(reinforced).requires(MNTags.INGOTS_SIGNALUM)
                .unlockedBy("has_reinforced", has(reinforced)).save(output, key(signalum) + "_upgrade");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, resonant, 2)
                .pattern("iGi").pattern("IRI")
                .define('i', Items.IRON_NUGGET).define('G', Items.GLASS).define('I', MNTags.INGOTS_ENDERIUM).define('R', middleItem)
                .unlockedBy("has_enderium", has(MNTags.INGOTS_ENDERIUM)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, resonant, 1)
                .requires(signalum).requires(MNTags.INGOTS_ENDERIUM)
                .unlockedBy("has_signalum", has(signalum)).save(output, key(resonant) + "_upgrade");
    }

    private void opaqueConversion(RecipeOutput output, ItemLike transparent, ItemLike opaque) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, opaque, 1)
                .requires(transparent).requires(MNTags.INGOTS_LEAD)
                .unlockedBy("has_transparent", has(transparent))
                .save(output, key(opaque) + "_from_transparent");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, transparent, 1)
                .requires(opaque).requires(MNTags.HARDENED_GLASS)
                .unlockedBy("has_opaque", has(opaque))
                .save(output, key(transparent) + "_from_opaque");
    }

    private void ingotNugget(RecipeOutput output, ItemLike ingot, ItemLike nugget, String name) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot, 1)
                .pattern("NNN").pattern("NNN").pattern("NNN")
                .define('N', nugget)
                .unlockedBy("has_nugget", has(nugget)).save(output, id(name + "_ingot_from_nuggets"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                .requires(ingot)
                .unlockedBy("has_ingot", has(ingot)).save(output, id(name + "_nugget_from_ingot"));
    }

    private String key(ItemLike item) {
        return ModularNetworks.MODID + ":" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private String id(String name) {
        return ModularNetworks.MODID + ":" + name;
    }
}
