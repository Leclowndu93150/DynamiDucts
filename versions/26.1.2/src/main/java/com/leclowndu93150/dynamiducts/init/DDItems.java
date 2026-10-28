package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.attachment.filter.FilterFluid;
import com.leclowndu93150.dynamiducts.attachment.filter.FilterItem;
import com.leclowndu93150.dynamiducts.attachment.retriever.RetrieverFluid;
import com.leclowndu93150.dynamiducts.attachment.retriever.RetrieverItem;
import com.leclowndu93150.dynamiducts.attachment.servo.ServoFluid;
import com.leclowndu93150.dynamiducts.attachment.servo.ServoItem;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentPlacementHelper;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.item.AttachmentItem;
import com.leclowndu93150.dynamiducts.item.DuctBlockItem;
import com.leclowndu93150.dynamiducts.item.RelayItem;
import com.leclowndu93150.dynamiducts.item.DDTooltipHelper;
import com.leclowndu93150.dynamiducts.item.WrenchItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DynamiDucts.MODID);

    private static final String TIP_ENERGY = "info.dynamiducts.duct.energy";
    private static final String TIP_FLUID = "info.dynamiducts.duct.fluid";
    private static final String TIP_FLUID_ENERGY = "info.dynamiducts.duct.fluidEnergy";
    private static final String TIP_ITEM = "info.dynamiducts.duct.item";
    private static final String TIP_ITEM_ENERGY = "info.dynamiducts.duct.itemEnergy";
    private static final String TIP_ITEM_FAST = "info.dynamiducts.duct.itemFast";
    private static final String TIP_STRUCTURE = "info.dynamiducts.duct.structure";
    private static final String TIP_CRAFTING = "info.dynamiducts.duct.crafting";
    private static final String TIP_FLUID_BASIC = "info.dynamiducts.duct.fluidBasic";
    private static final String TIP_FLUID_HARDENED = "info.dynamiducts.duct.fluidHardened";
    private static final String TIP_FLUID_SUPER = "info.dynamiducts.duct.fluidSuper";
    private static final String TIP_ENERGY_SUPER = "info.dynamiducts.duct.energySuper";

    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_BASIC = ITEMS.registerItem("energy_duct_basic",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_BASIC.get(), props, TIP_ENERGY), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_HARDENED = ITEMS.registerItem("energy_duct_hardened",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_HARDENED.get(), props, TIP_ENERGY), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_REINFORCED = ITEMS.registerItem("energy_duct_reinforced",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_REINFORCED.get(), props, TIP_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SIGNALUM = ITEMS.registerItem("energy_duct_signalum",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_SIGNALUM.get(), props, TIP_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_RESONANT = ITEMS.registerItem("energy_duct_resonant",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_RESONANT.get(), props, TIP_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SUPERCONDUCTOR = ITEMS.registerItem("energy_duct_superconductor",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get(), props, TIP_ENERGY, TIP_ENERGY_SUPER), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));

    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_REINFORCED_EMPTY = ITEMS.registerItem("energy_duct_reinforced_empty",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY.get(), props, TIP_CRAFTING), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SIGNALUM_EMPTY = ITEMS.registerItem("energy_duct_signalum_empty",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY.get(), props, TIP_CRAFTING), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_RESONANT_EMPTY = ITEMS.registerItem("energy_duct_resonant_empty",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_RESONANT_EMPTY.get(), props, TIP_CRAFTING), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SUPERCONDUCTOR_EMPTY = ITEMS.registerItem("energy_duct_superconductor_empty",
            props -> new DuctBlockItem(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY.get(), props, TIP_CRAFTING), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));

    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_BASIC = ITEMS.registerItem("fluid_duct_basic",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_BASIC.get(), props, TIP_FLUID, TIP_FLUID_BASIC), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_BASIC_OPAQUE = ITEMS.registerItem("fluid_duct_basic_opaque",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_BASIC_OPAQUE.get(), props, TIP_FLUID, TIP_FLUID_BASIC), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_HARDENED = ITEMS.registerItem("fluid_duct_hardened",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_HARDENED.get(), props, TIP_FLUID, TIP_FLUID_HARDENED), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_HARDENED_OPAQUE = ITEMS.registerItem("fluid_duct_hardened_opaque",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_HARDENED_OPAQUE.get(), props, TIP_FLUID, TIP_FLUID_HARDENED), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_ENERGY = ITEMS.registerItem("fluid_duct_energy",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_ENERGY.get(), props, TIP_FLUID_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_ENERGY_OPAQUE = ITEMS.registerItem("fluid_duct_energy_opaque",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_ENERGY_OPAQUE.get(), props, TIP_FLUID_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_SUPER = ITEMS.registerItem("fluid_duct_super",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_SUPER.get(), props, TIP_FLUID, TIP_FLUID_SUPER), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_SUPER_OPAQUE = ITEMS.registerItem("fluid_duct_super_opaque",
            props -> new DuctBlockItem(DDBlocks.FLUID_DUCT_SUPER_OPAQUE.get(), props, TIP_FLUID, TIP_FLUID_SUPER), p -> p.useBlockDescriptionPrefix().rarity(Rarity.RARE));

    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_BASIC = ITEMS.registerItem("item_duct_basic",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_BASIC.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_BASIC_OPAQUE = ITEMS.registerItem("item_duct_basic_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_BASIC_OPAQUE.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_DENSE = ITEMS.registerItem("item_duct_dense",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_DENSE.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_DENSE_OPAQUE = ITEMS.registerItem("item_duct_dense_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_DENSE_OPAQUE.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_VACUUM = ITEMS.registerItem("item_duct_vacuum",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_VACUUM.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_VACUUM_OPAQUE = ITEMS.registerItem("item_duct_vacuum_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_VACUUM_OPAQUE.get(), props, TIP_ITEM), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_FAST = ITEMS.registerItem("item_duct_fast",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_FAST.get(), props, TIP_ITEM, TIP_ITEM_FAST), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_FAST_OPAQUE = ITEMS.registerItem("item_duct_fast_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_FAST_OPAQUE.get(), props, TIP_ITEM, TIP_ITEM_FAST), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY = ITEMS.registerItem("item_duct_energy",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_ENERGY.get(), props, TIP_ITEM_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_OPAQUE = ITEMS.registerItem("item_duct_energy_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_ENERGY_OPAQUE.get(), props, TIP_ITEM_ENERGY), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_FAST = ITEMS.registerItem("item_duct_energy_fast",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_ENERGY_FAST.get(), props, TIP_ITEM_ENERGY, TIP_ITEM_FAST), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_FAST_OPAQUE = ITEMS.registerItem("item_duct_energy_fast_opaque",
            props -> new DuctBlockItem(DDBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(), props, TIP_ITEM_ENERGY, TIP_ITEM_FAST), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_BASIC = ITEMS.registerItem("transport_duct_basic",
            props -> new DuctBlockItem(DDBlocks.TRANSPORT_DUCT_BASIC.get(), props, "info.dynamiducts.duct.transport"), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_LONG_RANGE = ITEMS.registerItem("transport_duct_long_range",
            props -> new DuctBlockItem(DDBlocks.TRANSPORT_DUCT_LONG_RANGE.get(), props, "info.dynamiducts.duct.transportLongRange"), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_LINKING = ITEMS.registerItem("transport_duct_linking",
            props -> new DuctBlockItem(DDBlocks.TRANSPORT_DUCT_LINKING.get(), props, "info.dynamiducts.duct.transportCrossover"), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_FRAME = ITEMS.registerItem("transport_duct_frame",
            props -> new DuctBlockItem(DDBlocks.TRANSPORT_DUCT_FRAME.get(), props, TIP_CRAFTING), p -> p.useBlockDescriptionPrefix().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<DuctBlockItem> STRUCTURAL_DUCT = ITEMS.registerItem("structural_duct",
            props -> new DuctBlockItem(DDBlocks.STRUCTURAL_DUCT.get(), props, TIP_STRUCTURE), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<DuctBlockItem> LUX_DUCT = ITEMS.registerItem("lux_duct",
            props -> new DuctBlockItem(DDBlocks.LUX_DUCT.get(), props, "info.dynamiducts.duct.light"), Item.Properties::useBlockDescriptionPrefix);

    public static final DeferredItem<AttachmentItem> SERVO_BASIC = ITEMS.registerItem("servo_basic",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.BASIC, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.BASIC)), "info.dynamiducts.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_HARDENED = ITEMS.registerItem("servo_hardened",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.HARDENED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.HARDENED)), "info.dynamiducts.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_REINFORCED = ITEMS.registerItem("servo_reinforced",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.REINFORCED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.REINFORCED)), "info.dynamiducts.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_SIGNALUM = ITEMS.registerItem("servo_signalum",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.SIGNALUM, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.SIGNALUM)), "info.dynamiducts.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_RESONANT = ITEMS.registerItem("servo_resonant",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.RESONANT, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.RESONANT)), "info.dynamiducts.servo.info"));

    public static final DeferredItem<AttachmentItem> FILTER_BASIC = ITEMS.registerItem("filter_basic",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.BASIC, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.BASIC)), "info.dynamiducts.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_HARDENED = ITEMS.registerItem("filter_hardened",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.HARDENED, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.HARDENED)), "info.dynamiducts.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_REINFORCED = ITEMS.registerItem("filter_reinforced",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.REINFORCED, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.REINFORCED)), "info.dynamiducts.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_SIGNALUM = ITEMS.registerItem("filter_signalum",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.SIGNALUM, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.SIGNALUM)), "info.dynamiducts.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_RESONANT = ITEMS.registerItem("filter_resonant",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.RESONANT, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.RESONANT)), "info.dynamiducts.filter.info"));

    public static final DeferredItem<AttachmentItem> RETRIEVER_BASIC = ITEMS.registerItem("retriever_basic",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.BASIC, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.BASIC)), "info.dynamiducts.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_HARDENED = ITEMS.registerItem("retriever_hardened",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.HARDENED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.HARDENED)), "info.dynamiducts.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_REINFORCED = ITEMS.registerItem("retriever_reinforced",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.REINFORCED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.REINFORCED)), "info.dynamiducts.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_SIGNALUM = ITEMS.registerItem("retriever_signalum",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.SIGNALUM, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.SIGNALUM)), "info.dynamiducts.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_RESONANT = ITEMS.registerItem("retriever_resonant",
            props -> new AttachmentItem(props, DDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.RESONANT, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.RESONANT)), "info.dynamiducts.retriever.info"));

    public static final DeferredItem<RelayItem> RELAY = ITEMS.registerItem("relay",
            RelayItem::new);

    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.registerItem("wrench",
            WrenchItem::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.registerSimpleItem("lead_nugget");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> TIN_NUGGET = ITEMS.registerSimpleItem("tin_nugget");
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> SILVER_NUGGET = ITEMS.registerSimpleItem("silver_nugget");
    public static final DeferredItem<Item> INVAR_INGOT = ITEMS.registerSimpleItem("invar_ingot");
    public static final DeferredItem<Item> INVAR_NUGGET = ITEMS.registerSimpleItem("invar_nugget");
    public static final DeferredItem<Item> ELECTRUM_INGOT = ITEMS.registerSimpleItem("electrum_ingot");
    public static final DeferredItem<Item> ELECTRUM_NUGGET = ITEMS.registerSimpleItem("electrum_nugget");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> BRONZE_NUGGET = ITEMS.registerSimpleItem("bronze_nugget");
    public static final DeferredItem<Item> SIGNALUM_INGOT = ITEMS.registerSimpleItem("signalum_ingot");
    public static final DeferredItem<Item> SIGNALUM_NUGGET = ITEMS.registerSimpleItem("signalum_nugget");
    public static final DeferredItem<Item> ENDERIUM_INGOT = ITEMS.registerSimpleItem("enderium_ingot");
    public static final DeferredItem<Item> ENDERIUM_NUGGET = ITEMS.registerSimpleItem("enderium_nugget");
    public static final DeferredItem<Item> LUMIUM_INGOT = ITEMS.registerSimpleItem("lumium_ingot");
    public static final DeferredItem<Item> LUMIUM_NUGGET = ITEMS.registerSimpleItem("lumium_nugget");
}
