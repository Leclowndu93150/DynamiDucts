package com.leclowndu93150.dynamiducts.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DDTags {

    public static final TagKey<Item> INGOTS_LEAD = cTag("ingots/lead");
    public static final TagKey<Item> INGOTS_TIN = cTag("ingots/tin");
    public static final TagKey<Item> INGOTS_SILVER = cTag("ingots/silver");
    public static final TagKey<Item> INGOTS_INVAR = cTag("ingots/invar");
    public static final TagKey<Item> INGOTS_ELECTRUM = cTag("ingots/electrum");
    public static final TagKey<Item> INGOTS_BRONZE = cTag("ingots/bronze");
    public static final TagKey<Item> INGOTS_SIGNALUM = cTag("ingots/signalum");
    public static final TagKey<Item> INGOTS_ENDERIUM = cTag("ingots/enderium");
    public static final TagKey<Item> INGOTS_LUMIUM = cTag("ingots/lumium");

    public static final TagKey<Item> NUGGETS_LEAD = cTag("nuggets/lead");
    public static final TagKey<Item> NUGGETS_TIN = cTag("nuggets/tin");
    public static final TagKey<Item> NUGGETS_SILVER = cTag("nuggets/silver");
    public static final TagKey<Item> NUGGETS_INVAR = cTag("nuggets/invar");
    public static final TagKey<Item> NUGGETS_ELECTRUM = cTag("nuggets/electrum");
    public static final TagKey<Item> NUGGETS_BRONZE = cTag("nuggets/bronze");
    public static final TagKey<Item> NUGGETS_SIGNALUM = cTag("nuggets/signalum");
    public static final TagKey<Item> NUGGETS_ENDERIUM = cTag("nuggets/enderium");
    public static final TagKey<Item> NUGGETS_LUMIUM = cTag("nuggets/lumium");

    public static final TagKey<Item> HARDENED_GLASS = cTag("glass_blocks/hardened");

    private static TagKey<Item> cTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
