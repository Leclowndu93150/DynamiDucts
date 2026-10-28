package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.block.EnergyDuctBlock;
import com.leclowndu93150.dynamiducts.block.FluidDuctBlock;
import com.leclowndu93150.dynamiducts.block.ItemDuctBlock;
import com.leclowndu93150.dynamiducts.block.LuxDuctBlock;
import com.leclowndu93150.dynamiducts.block.StructuralDuctBlock;
import com.leclowndu93150.dynamiducts.block.TransportDuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.EnergyDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.FluidDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.TransportDuctBlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DynamiDucts.MODID);

    private static BlockBehaviour.Properties ductProps() {
        return BlockBehaviour.Properties.of()
                .strength(1.0F, 6.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .dynamicShape();
    }

    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_BASIC = BLOCKS.registerBlock("energy_duct_basic",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.BASIC), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_HARDENED = BLOCKS.registerBlock("energy_duct_hardened",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.HARDENED), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_REINFORCED = BLOCKS.registerBlock("energy_duct_reinforced",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.REINFORCED), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SIGNALUM = BLOCKS.registerBlock("energy_duct_signalum",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.SIGNALUM), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_RESONANT = BLOCKS.registerBlock("energy_duct_resonant",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.RESONANT), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SUPERCONDUCTOR = BLOCKS.registerBlock("energy_duct_superconductor",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR), DDBlocks::ductProps);

    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_REINFORCED_EMPTY = BLOCKS.registerBlock("energy_duct_reinforced_empty",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.REINFORCED_EMPTY), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SIGNALUM_EMPTY = BLOCKS.registerBlock("energy_duct_signalum_empty",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.SIGNALUM_EMPTY), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_RESONANT_EMPTY = BLOCKS.registerBlock("energy_duct_resonant_empty",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.RESONANT_EMPTY), DDBlocks::ductProps);
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SUPERCONDUCTOR_EMPTY = BLOCKS.registerBlock("energy_duct_superconductor_empty",
            props -> new EnergyDuctBlock(props, EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR_EMPTY), DDBlocks::ductProps);

    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_BASIC = BLOCKS.registerBlock("fluid_duct_basic",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.BASIC, false), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_BASIC_OPAQUE = BLOCKS.registerBlock("fluid_duct_basic_opaque",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.BASIC, true), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_HARDENED = BLOCKS.registerBlock("fluid_duct_hardened",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.HARDENED, false), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_HARDENED_OPAQUE = BLOCKS.registerBlock("fluid_duct_hardened_opaque",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.HARDENED, true), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_ENERGY = BLOCKS.registerBlock("fluid_duct_energy",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.ENERGY, false), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_ENERGY_OPAQUE = BLOCKS.registerBlock("fluid_duct_energy_opaque",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.ENERGY, true), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_SUPER = BLOCKS.registerBlock("fluid_duct_super",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.SUPER, false), DDBlocks::ductProps);
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_SUPER_OPAQUE = BLOCKS.registerBlock("fluid_duct_super_opaque",
            props -> new FluidDuctBlock(props, FluidDuctBlockEntity.Tier.SUPER, true), DDBlocks::ductProps);

    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_BASIC = BLOCKS.registerBlock("item_duct_basic",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.BASIC, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_BASIC_OPAQUE = BLOCKS.registerBlock("item_duct_basic_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.BASIC, true), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_DENSE = BLOCKS.registerBlock("item_duct_dense",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.DENSE, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_DENSE_OPAQUE = BLOCKS.registerBlock("item_duct_dense_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.DENSE, true), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_VACUUM = BLOCKS.registerBlock("item_duct_vacuum",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.VACUUM, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_VACUUM_OPAQUE = BLOCKS.registerBlock("item_duct_vacuum_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.VACUUM, true), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_FAST = BLOCKS.registerBlock("item_duct_fast",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.FAST, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_FAST_OPAQUE = BLOCKS.registerBlock("item_duct_fast_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.FAST, true), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY = BLOCKS.registerBlock("item_duct_energy",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.ENERGY, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_OPAQUE = BLOCKS.registerBlock("item_duct_energy_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.ENERGY, true), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_FAST = BLOCKS.registerBlock("item_duct_energy_fast",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.ENERGY_FAST, false), DDBlocks::ductProps);
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_FAST_OPAQUE = BLOCKS.registerBlock("item_duct_energy_fast_opaque",
            props -> new ItemDuctBlock(props, ItemDuctBlockEntity.Tier.ENERGY_FAST, true), DDBlocks::ductProps);

    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_BASIC = BLOCKS.registerBlock("transport_duct_basic",
            props -> new TransportDuctBlock(props, TransportDuctBlockEntity.Tier.BASIC), DDBlocks::ductProps);
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_LONG_RANGE = BLOCKS.registerBlock("transport_duct_long_range",
            props -> new TransportDuctBlock(props, TransportDuctBlockEntity.Tier.LONG_RANGE), DDBlocks::ductProps);
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_LINKING = BLOCKS.registerBlock("transport_duct_linking",
            props -> new TransportDuctBlock(props, TransportDuctBlockEntity.Tier.LINKING), DDBlocks::ductProps);
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_FRAME = BLOCKS.registerBlock("transport_duct_frame",
            props -> new TransportDuctBlock(props, TransportDuctBlockEntity.Tier.FRAME), DDBlocks::ductProps);

    public static final DeferredBlock<StructuralDuctBlock> STRUCTURAL_DUCT = BLOCKS.registerBlock("structural_duct",
            props -> new StructuralDuctBlock(props), DDBlocks::ductProps);
    public static final DeferredBlock<LuxDuctBlock> LUX_DUCT = BLOCKS.registerBlock("lux_duct",
            props -> new LuxDuctBlock(props), DDBlocks::ductProps);
}
