package com.leclowndu93150.modular_networks.init;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MNDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModularNetworks.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockState>> COVER_STATE =
            DATA_COMPONENTS.registerComponentType("cover_state", builder -> builder.persistent(BlockState.CODEC).cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> DUCT_PATH_WEIGHT =
            DATA_COMPONENTS.registerComponentType("duct_path_weight", builder -> builder.persistent(Codec.BYTE).cacheEncoding());

    private MNDataComponents() {
    }
}
