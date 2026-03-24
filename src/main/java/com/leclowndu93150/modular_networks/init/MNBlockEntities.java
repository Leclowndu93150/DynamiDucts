package com.leclowndu93150.modular_networks.init;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.EnergyDuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.FluidDuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.StructuralDuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.TransportDuctBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MNBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModularNetworks.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_BASIC =
            BLOCK_ENTITIES.register("energy_duct_basic", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_BASIC.get(), pos, state, EnergyDuctBlockEntity.Tier.BASIC),
                    MNBlocks.ENERGY_DUCT_BASIC.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_HARDENED =
            BLOCK_ENTITIES.register("energy_duct_hardened", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_HARDENED.get(), pos, state, EnergyDuctBlockEntity.Tier.HARDENED),
                    MNBlocks.ENERGY_DUCT_HARDENED.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_REINFORCED =
            BLOCK_ENTITIES.register("energy_duct_reinforced", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_REINFORCED.get(), pos, state, EnergyDuctBlockEntity.Tier.REINFORCED),
                    MNBlocks.ENERGY_DUCT_REINFORCED.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_SIGNALUM =
            BLOCK_ENTITIES.register("energy_duct_signalum", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_SIGNALUM.get(), pos, state, EnergyDuctBlockEntity.Tier.SIGNALUM),
                    MNBlocks.ENERGY_DUCT_SIGNALUM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_RESONANT =
            BLOCK_ENTITIES.register("energy_duct_resonant", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_RESONANT.get(), pos, state, EnergyDuctBlockEntity.Tier.RESONANT),
                    MNBlocks.ENERGY_DUCT_RESONANT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_SUPERCONDUCTOR =
            BLOCK_ENTITIES.register("energy_duct_superconductor", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR.get(), pos, state, EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR),
                    MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_REINFORCED_EMPTY =
            BLOCK_ENTITIES.register("energy_duct_reinforced_empty", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_REINFORCED_EMPTY.get(), pos, state, EnergyDuctBlockEntity.Tier.REINFORCED_EMPTY),
                    MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_SIGNALUM_EMPTY =
            BLOCK_ENTITIES.register("energy_duct_signalum_empty", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_SIGNALUM_EMPTY.get(), pos, state, EnergyDuctBlockEntity.Tier.SIGNALUM_EMPTY),
                    MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_RESONANT_EMPTY =
            BLOCK_ENTITIES.register("energy_duct_resonant_empty", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_RESONANT_EMPTY.get(), pos, state, EnergyDuctBlockEntity.Tier.RESONANT_EMPTY),
                    MNBlocks.ENERGY_DUCT_RESONANT_EMPTY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> ENERGY_DUCT_SUPERCONDUCTOR_EMPTY =
            BLOCK_ENTITIES.register("energy_duct_superconductor_empty", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EnergyDuctBlockEntity(MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY.get(), pos, state, EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR_EMPTY),
                    MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_BASIC =
            BLOCK_ENTITIES.register("fluid_duct_basic", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_BASIC.get(), pos, state, FluidDuctBlockEntity.Tier.BASIC, false),
                    MNBlocks.FLUID_DUCT_BASIC.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_BASIC_OPAQUE =
            BLOCK_ENTITIES.register("fluid_duct_basic_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_BASIC_OPAQUE.get(), pos, state, FluidDuctBlockEntity.Tier.BASIC, true),
                    MNBlocks.FLUID_DUCT_BASIC_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_HARDENED =
            BLOCK_ENTITIES.register("fluid_duct_hardened", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_HARDENED.get(), pos, state, FluidDuctBlockEntity.Tier.HARDENED, false),
                    MNBlocks.FLUID_DUCT_HARDENED.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_HARDENED_OPAQUE =
            BLOCK_ENTITIES.register("fluid_duct_hardened_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_HARDENED_OPAQUE.get(), pos, state, FluidDuctBlockEntity.Tier.HARDENED, true),
                    MNBlocks.FLUID_DUCT_HARDENED_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_ENERGY =
            BLOCK_ENTITIES.register("fluid_duct_energy", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_ENERGY.get(), pos, state, FluidDuctBlockEntity.Tier.ENERGY, false),
                    MNBlocks.FLUID_DUCT_ENERGY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_ENERGY_OPAQUE =
            BLOCK_ENTITIES.register("fluid_duct_energy_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get(), pos, state, FluidDuctBlockEntity.Tier.ENERGY, true),
                    MNBlocks.FLUID_DUCT_ENERGY_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_SUPER =
            BLOCK_ENTITIES.register("fluid_duct_super", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_SUPER.get(), pos, state, FluidDuctBlockEntity.Tier.SUPER, false),
                    MNBlocks.FLUID_DUCT_SUPER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> FLUID_DUCT_SUPER_OPAQUE =
            BLOCK_ENTITIES.register("fluid_duct_super_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new FluidDuctBlockEntity(MNBlockEntities.FLUID_DUCT_SUPER_OPAQUE.get(), pos, state, FluidDuctBlockEntity.Tier.SUPER, true),
                    MNBlocks.FLUID_DUCT_SUPER_OPAQUE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_BASIC =
            BLOCK_ENTITIES.register("item_duct_basic", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_BASIC.get(), pos, state, ItemDuctBlockEntity.Tier.BASIC, false),
                    MNBlocks.ITEM_DUCT_BASIC.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_BASIC_OPAQUE =
            BLOCK_ENTITIES.register("item_duct_basic_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_BASIC_OPAQUE.get(), pos, state, ItemDuctBlockEntity.Tier.BASIC, true),
                    MNBlocks.ITEM_DUCT_BASIC_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_FAST =
            BLOCK_ENTITIES.register("item_duct_fast", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_FAST.get(), pos, state, ItemDuctBlockEntity.Tier.FAST, false),
                    MNBlocks.ITEM_DUCT_FAST.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_FAST_OPAQUE =
            BLOCK_ENTITIES.register("item_duct_fast_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_FAST_OPAQUE.get(), pos, state, ItemDuctBlockEntity.Tier.FAST, true),
                    MNBlocks.ITEM_DUCT_FAST_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_ENERGY =
            BLOCK_ENTITIES.register("item_duct_energy", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_ENERGY.get(), pos, state, ItemDuctBlockEntity.Tier.ENERGY, false),
                    MNBlocks.ITEM_DUCT_ENERGY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_ENERGY_OPAQUE =
            BLOCK_ENTITIES.register("item_duct_energy_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get(), pos, state, ItemDuctBlockEntity.Tier.ENERGY, true),
                    MNBlocks.ITEM_DUCT_ENERGY_OPAQUE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_ENERGY_FAST =
            BLOCK_ENTITIES.register("item_duct_energy_fast", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_ENERGY_FAST.get(), pos, state, ItemDuctBlockEntity.Tier.ENERGY_FAST, false),
                    MNBlocks.ITEM_DUCT_ENERGY_FAST.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> ITEM_DUCT_ENERGY_FAST_OPAQUE =
            BLOCK_ENTITIES.register("item_duct_energy_fast_opaque", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ItemDuctBlockEntity(MNBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(), pos, state, ItemDuctBlockEntity.Tier.ENERGY_FAST, true),
                    MNBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransportDuctBlockEntity>> TRANSPORT_DUCT_BASIC =
            BLOCK_ENTITIES.register("transport_duct_basic", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new TransportDuctBlockEntity(MNBlockEntities.TRANSPORT_DUCT_BASIC.get(), pos, state, TransportDuctBlockEntity.Tier.BASIC),
                    MNBlocks.TRANSPORT_DUCT_BASIC.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransportDuctBlockEntity>> TRANSPORT_DUCT_LONG_RANGE =
            BLOCK_ENTITIES.register("transport_duct_long_range", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new TransportDuctBlockEntity(MNBlockEntities.TRANSPORT_DUCT_LONG_RANGE.get(), pos, state, TransportDuctBlockEntity.Tier.LONG_RANGE),
                    MNBlocks.TRANSPORT_DUCT_LONG_RANGE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransportDuctBlockEntity>> TRANSPORT_DUCT_LINKING =
            BLOCK_ENTITIES.register("transport_duct_linking", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new TransportDuctBlockEntity(MNBlockEntities.TRANSPORT_DUCT_LINKING.get(), pos, state, TransportDuctBlockEntity.Tier.LINKING),
                    MNBlocks.TRANSPORT_DUCT_LINKING.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransportDuctBlockEntity>> TRANSPORT_DUCT_FRAME =
            BLOCK_ENTITIES.register("transport_duct_frame", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new TransportDuctBlockEntity(MNBlockEntities.TRANSPORT_DUCT_FRAME.get(), pos, state, TransportDuctBlockEntity.Tier.FRAME),
                    MNBlocks.TRANSPORT_DUCT_FRAME.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StructuralDuctBlockEntity>> STRUCTURAL_DUCT =
            BLOCK_ENTITIES.register("structural_duct", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new StructuralDuctBlockEntity(MNBlockEntities.STRUCTURAL_DUCT.get(), pos, state),
                    MNBlocks.STRUCTURAL_DUCT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StructuralDuctBlockEntity>> LUX_DUCT =
            BLOCK_ENTITIES.register("lux_duct", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new StructuralDuctBlockEntity(MNBlockEntities.LUX_DUCT.get(), pos, state),
                    MNBlocks.LUX_DUCT.get()).build(null));
}
