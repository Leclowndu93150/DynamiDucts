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

    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_BASIC = BLOCKS.register("energy_duct_basic",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.BASIC));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_HARDENED = BLOCKS.register("energy_duct_hardened",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.HARDENED));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_REINFORCED = BLOCKS.register("energy_duct_reinforced",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.REINFORCED));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SIGNALUM = BLOCKS.register("energy_duct_signalum",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.SIGNALUM));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_RESONANT = BLOCKS.register("energy_duct_resonant",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.RESONANT));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SUPERCONDUCTOR = BLOCKS.register("energy_duct_superconductor",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR));

    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_REINFORCED_EMPTY = BLOCKS.register("energy_duct_reinforced_empty",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.REINFORCED_EMPTY));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SIGNALUM_EMPTY = BLOCKS.register("energy_duct_signalum_empty",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.SIGNALUM_EMPTY));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_RESONANT_EMPTY = BLOCKS.register("energy_duct_resonant_empty",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.RESONANT_EMPTY));
    public static final DeferredBlock<EnergyDuctBlock> ENERGY_DUCT_SUPERCONDUCTOR_EMPTY = BLOCKS.register("energy_duct_superconductor_empty",
            () -> new EnergyDuctBlock(ductProps(), EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR_EMPTY));

    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_BASIC = BLOCKS.register("fluid_duct_basic",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.BASIC, false));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_BASIC_OPAQUE = BLOCKS.register("fluid_duct_basic_opaque",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.BASIC, true));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_HARDENED = BLOCKS.register("fluid_duct_hardened",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.HARDENED, false));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_HARDENED_OPAQUE = BLOCKS.register("fluid_duct_hardened_opaque",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.HARDENED, true));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_ENERGY = BLOCKS.register("fluid_duct_energy",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.ENERGY, false));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_ENERGY_OPAQUE = BLOCKS.register("fluid_duct_energy_opaque",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.ENERGY, true));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_SUPER = BLOCKS.register("fluid_duct_super",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.SUPER, false));
    public static final DeferredBlock<FluidDuctBlock> FLUID_DUCT_SUPER_OPAQUE = BLOCKS.register("fluid_duct_super_opaque",
            () -> new FluidDuctBlock(ductProps(), FluidDuctBlockEntity.Tier.SUPER, true));

    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_BASIC = BLOCKS.register("item_duct_basic",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.BASIC, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_BASIC_OPAQUE = BLOCKS.register("item_duct_basic_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.BASIC, true));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_DENSE = BLOCKS.register("item_duct_dense",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.DENSE, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_DENSE_OPAQUE = BLOCKS.register("item_duct_dense_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.DENSE, true));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_VACUUM = BLOCKS.register("item_duct_vacuum",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.VACUUM, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_VACUUM_OPAQUE = BLOCKS.register("item_duct_vacuum_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.VACUUM, true));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_FAST = BLOCKS.register("item_duct_fast",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.FAST, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_FAST_OPAQUE = BLOCKS.register("item_duct_fast_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.FAST, true));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY = BLOCKS.register("item_duct_energy",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.ENERGY, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_OPAQUE = BLOCKS.register("item_duct_energy_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.ENERGY, true));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_FAST = BLOCKS.register("item_duct_energy_fast",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.ENERGY_FAST, false));
    public static final DeferredBlock<ItemDuctBlock> ITEM_DUCT_ENERGY_FAST_OPAQUE = BLOCKS.register("item_duct_energy_fast_opaque",
            () -> new ItemDuctBlock(ductProps(), ItemDuctBlockEntity.Tier.ENERGY_FAST, true));

    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_BASIC = BLOCKS.register("transport_duct_basic",
            () -> new TransportDuctBlock(ductProps(), TransportDuctBlockEntity.Tier.BASIC));
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_LONG_RANGE = BLOCKS.register("transport_duct_long_range",
            () -> new TransportDuctBlock(ductProps(), TransportDuctBlockEntity.Tier.LONG_RANGE));
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_LINKING = BLOCKS.register("transport_duct_linking",
            () -> new TransportDuctBlock(ductProps(), TransportDuctBlockEntity.Tier.LINKING));
    public static final DeferredBlock<TransportDuctBlock> TRANSPORT_DUCT_FRAME = BLOCKS.register("transport_duct_frame",
            () -> new TransportDuctBlock(ductProps(), TransportDuctBlockEntity.Tier.FRAME));

    public static final DeferredBlock<StructuralDuctBlock> STRUCTURAL_DUCT = BLOCKS.register("structural_duct",
            () -> new StructuralDuctBlock(ductProps()));
    public static final DeferredBlock<LuxDuctBlock> LUX_DUCT = BLOCKS.register("lux_duct",
            () -> new LuxDuctBlock(ductProps()));
}
