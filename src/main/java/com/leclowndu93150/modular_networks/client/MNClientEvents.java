package com.leclowndu93150.modular_networks.client;

import com.leclowndu93150.modular_networks.ModularNetworks;
import net.minecraft.resources.ResourceLocation;

public class MNClientEvents {

    private static final String[] SERVO_TEXTURES = {
            "block/duct/attachment/servo/servo_base_0_0",
            "block/duct/attachment/servo/servo_base_0_1",
            "block/duct/attachment/servo/servo_base_0_2",
            "block/duct/attachment/servo/servo_base_0_3",
            "block/duct/attachment/servo/servo_base_0_4",
    };

    private static final String[] FILTER_TEXTURES = {
            "block/duct/attachment/filter/filter_0",
            "block/duct/attachment/filter/filter_1",
            "block/duct/attachment/filter/filter_2",
            "block/duct/attachment/filter/filter_3",
            "block/duct/attachment/filter/filter_4",
    };

    private static final String[] RETRIEVER_TEXTURES = {
            "block/duct/attachment/retriever/retriever_base_0_0",
            "block/duct/attachment/retriever/retriever_base_0_1",
            "block/duct/attachment/retriever/retriever_base_0_2",
            "block/duct/attachment/retriever/retriever_base_0_3",
            "block/duct/attachment/retriever/retriever_base_0_4",
    };

    public static ResourceLocation attachmentTexture(String type, int tier) {
        return ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, switch (type) {
            case "servo" -> SERVO_TEXTURES[tier];
            case "filter" -> FILTER_TEXTURES[tier];
            case "retriever" -> RETRIEVER_TEXTURES[tier];
            default -> SERVO_TEXTURES[0];
        });
    }
}
