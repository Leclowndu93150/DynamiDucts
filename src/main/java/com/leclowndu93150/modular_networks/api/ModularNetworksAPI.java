package com.leclowndu93150.modular_networks.api;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public final class ModularNetworksAPI {

    private ModularNetworksAPI() {
    }

    public static void registerAttachmentType(ResourceLocation id, BiFunction<DuctBlockEntity, Direction, Attachment> factory) {
        AttachmentRegistry.register(id, factory);
    }
}
