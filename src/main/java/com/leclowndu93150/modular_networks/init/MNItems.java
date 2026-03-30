package com.leclowndu93150.modular_networks.init;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.attachment.filter.FilterFluid;
import com.leclowndu93150.modular_networks.attachment.filter.FilterItem;
import com.leclowndu93150.modular_networks.attachment.retriever.RetrieverFluid;
import com.leclowndu93150.modular_networks.attachment.retriever.RetrieverItem;
import com.leclowndu93150.modular_networks.attachment.servo.ServoFluid;
import com.leclowndu93150.modular_networks.attachment.servo.ServoItem;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentPlacementHelper;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.item.AttachmentItem;
import com.leclowndu93150.modular_networks.item.CoverItem;
import com.leclowndu93150.modular_networks.item.DuctBlockItem;
import com.leclowndu93150.modular_networks.item.RelayItem;
import com.leclowndu93150.modular_networks.item.TDTooltipHelper;
import com.leclowndu93150.modular_networks.item.WrenchItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MNItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModularNetworks.MODID);

    private static final String TIP_ENERGY = "info.modular_networks.duct.energy";
    private static final String TIP_FLUID = "info.modular_networks.duct.fluid";
    private static final String TIP_FLUID_ENERGY = "info.modular_networks.duct.fluidEnergy";
    private static final String TIP_ITEM = "info.modular_networks.duct.item";
    private static final String TIP_ITEM_ENERGY = "info.modular_networks.duct.itemEnergy";
    private static final String TIP_ITEM_FAST = "info.modular_networks.duct.itemFast";
    private static final String TIP_STRUCTURE = "info.modular_networks.duct.structure";
    private static final String TIP_CRAFTING = "info.modular_networks.duct.crafting";
    private static final String TIP_FLUID_BASIC = "info.modular_networks.duct.fluidBasic";
    private static final String TIP_FLUID_HARDENED = "info.modular_networks.duct.fluidHardened";
    private static final String TIP_FLUID_SUPER = "info.modular_networks.duct.fluidSuper";
    private static final String TIP_ENERGY_SUPER = "info.modular_networks.duct.energySuper";

    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_BASIC = ITEMS.register("energy_duct_basic",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_BASIC.get(), new Item.Properties(), TIP_ENERGY));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_HARDENED = ITEMS.register("energy_duct_hardened",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_HARDENED.get(), new Item.Properties(), TIP_ENERGY));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_REINFORCED = ITEMS.register("energy_duct_reinforced",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_REINFORCED.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ENERGY));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SIGNALUM = ITEMS.register("energy_duct_signalum",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_SIGNALUM.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ENERGY));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_RESONANT = ITEMS.register("energy_duct_resonant",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_RESONANT.get(), new Item.Properties().rarity(Rarity.RARE), TIP_ENERGY));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SUPERCONDUCTOR = ITEMS.register("energy_duct_superconductor",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get(), new Item.Properties().rarity(Rarity.RARE), TIP_ENERGY, TIP_ENERGY_SUPER));

    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_REINFORCED_EMPTY = ITEMS.register("energy_duct_reinforced_empty",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_CRAFTING));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SIGNALUM_EMPTY = ITEMS.register("energy_duct_signalum_empty",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_CRAFTING));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_RESONANT_EMPTY = ITEMS.register("energy_duct_resonant_empty",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY.get(), new Item.Properties().rarity(Rarity.RARE), TIP_CRAFTING));
    public static final DeferredItem<DuctBlockItem> ENERGY_DUCT_SUPERCONDUCTOR_EMPTY = ITEMS.register("energy_duct_superconductor_empty",
            () -> new DuctBlockItem(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY.get(), new Item.Properties().rarity(Rarity.RARE), TIP_CRAFTING));

    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_BASIC = ITEMS.register("fluid_duct_basic",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_BASIC.get(), new Item.Properties(), TIP_FLUID, TIP_FLUID_BASIC));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_BASIC_OPAQUE = ITEMS.register("fluid_duct_basic_opaque",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_BASIC_OPAQUE.get(), new Item.Properties(), TIP_FLUID, TIP_FLUID_BASIC));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_HARDENED = ITEMS.register("fluid_duct_hardened",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_HARDENED.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_FLUID, TIP_FLUID_HARDENED));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_HARDENED_OPAQUE = ITEMS.register("fluid_duct_hardened_opaque",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_HARDENED_OPAQUE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_FLUID, TIP_FLUID_HARDENED));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_ENERGY = ITEMS.register("fluid_duct_energy",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_ENERGY.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_FLUID_ENERGY));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_ENERGY_OPAQUE = ITEMS.register("fluid_duct_energy_opaque",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_ENERGY_OPAQUE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_FLUID_ENERGY));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_SUPER = ITEMS.register("fluid_duct_super",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_SUPER.get(), new Item.Properties().rarity(Rarity.RARE), TIP_FLUID, TIP_FLUID_SUPER));
    public static final DeferredItem<DuctBlockItem> FLUID_DUCT_SUPER_OPAQUE = ITEMS.register("fluid_duct_super_opaque",
            () -> new DuctBlockItem(MNBlocks.FLUID_DUCT_SUPER_OPAQUE.get(), new Item.Properties().rarity(Rarity.RARE), TIP_FLUID, TIP_FLUID_SUPER));

    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_BASIC = ITEMS.register("item_duct_basic",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_BASIC.get(), new Item.Properties(), TIP_ITEM));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_BASIC_OPAQUE = ITEMS.register("item_duct_basic_opaque",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_BASIC_OPAQUE.get(), new Item.Properties(), TIP_ITEM));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_FAST = ITEMS.register("item_duct_fast",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_FAST.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM, TIP_ITEM_FAST));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_FAST_OPAQUE = ITEMS.register("item_duct_fast_opaque",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_FAST_OPAQUE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM, TIP_ITEM_FAST));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY = ITEMS.register("item_duct_energy",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_ENERGY.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM_ENERGY));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_OPAQUE = ITEMS.register("item_duct_energy_opaque",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_ENERGY_OPAQUE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM_ENERGY));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_FAST = ITEMS.register("item_duct_energy_fast",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_ENERGY_FAST.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM_ENERGY, TIP_ITEM_FAST));
    public static final DeferredItem<DuctBlockItem> ITEM_DUCT_ENERGY_FAST_OPAQUE = ITEMS.register("item_duct_energy_fast_opaque",
            () -> new DuctBlockItem(MNBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_ITEM_ENERGY, TIP_ITEM_FAST));

    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_BASIC = ITEMS.register("transport_duct_basic",
            () -> new DuctBlockItem(MNBlocks.TRANSPORT_DUCT_BASIC.get(), new Item.Properties().rarity(Rarity.UNCOMMON), "info.modular_networks.duct.transport"));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_LONG_RANGE = ITEMS.register("transport_duct_long_range",
            () -> new DuctBlockItem(MNBlocks.TRANSPORT_DUCT_LONG_RANGE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), "info.modular_networks.duct.transportLongRange"));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_LINKING = ITEMS.register("transport_duct_linking",
            () -> new DuctBlockItem(MNBlocks.TRANSPORT_DUCT_LINKING.get(), new Item.Properties().rarity(Rarity.UNCOMMON), "info.modular_networks.duct.transportCrossover"));
    public static final DeferredItem<DuctBlockItem> TRANSPORT_DUCT_FRAME = ITEMS.register("transport_duct_frame",
            () -> new DuctBlockItem(MNBlocks.TRANSPORT_DUCT_FRAME.get(), new Item.Properties().rarity(Rarity.UNCOMMON), TIP_CRAFTING));

    public static final DeferredItem<DuctBlockItem> STRUCTURAL_DUCT = ITEMS.register("structural_duct",
            () -> new DuctBlockItem(MNBlocks.STRUCTURAL_DUCT.get(), new Item.Properties(), TIP_STRUCTURE));
    public static final DeferredItem<DuctBlockItem> LUX_DUCT = ITEMS.register("lux_duct",
            () -> new DuctBlockItem(MNBlocks.LUX_DUCT.get(), new Item.Properties(), "info.modular_networks.duct.light"));

    public static final DeferredItem<AttachmentItem> SERVO_BASIC = ITEMS.register("servo_basic",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.BASIC, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.BASIC)), "info.modular_networks.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_HARDENED = ITEMS.register("servo_hardened",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.HARDENED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.HARDENED)), "info.modular_networks.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_REINFORCED = ITEMS.register("servo_reinforced",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.REINFORCED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.REINFORCED)), "info.modular_networks.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_SIGNALUM = ITEMS.register("servo_signalum",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.SIGNALUM, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.SIGNALUM)), "info.modular_networks.servo.info"));
    public static final DeferredItem<AttachmentItem> SERVO_RESONANT = ITEMS.register("servo_resonant",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.SERVO, AttachmentTier.RESONANT, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new ServoFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new ServoItem(duct, dir, AttachmentTier.RESONANT)), "info.modular_networks.servo.info"));

    public static final DeferredItem<AttachmentItem> FILTER_BASIC = ITEMS.register("filter_basic",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.BASIC, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.BASIC)), "info.modular_networks.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_HARDENED = ITEMS.register("filter_hardened",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.HARDENED, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.HARDENED)), "info.modular_networks.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_REINFORCED = ITEMS.register("filter_reinforced",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.REINFORCED, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.REINFORCED)), "info.modular_networks.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_SIGNALUM = ITEMS.register("filter_signalum",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.SIGNALUM, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.SIGNALUM)), "info.modular_networks.filter.info"));
    public static final DeferredItem<AttachmentItem> FILTER_RESONANT = ITEMS.register("filter_resonant",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.FILTER, AttachmentTier.RESONANT, false, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new FilterFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new FilterItem(duct, dir, AttachmentTier.RESONANT)), "info.modular_networks.filter.info"));

    public static final DeferredItem<AttachmentItem> RETRIEVER_BASIC = ITEMS.register("retriever_basic",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.BASIC, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.BASIC), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.BASIC)), "info.modular_networks.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_HARDENED = ITEMS.register("retriever_hardened",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.HARDENED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.HARDENED), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.HARDENED)), "info.modular_networks.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_REINFORCED = ITEMS.register("retriever_reinforced",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.REINFORCED, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.REINFORCED), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.REINFORCED)), "info.modular_networks.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_SIGNALUM = ITEMS.register("retriever_signalum",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.SIGNALUM, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.SIGNALUM), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.SIGNALUM)), "info.modular_networks.retriever.info"));
    public static final DeferredItem<AttachmentItem> RETRIEVER_RESONANT = ITEMS.register("retriever_resonant",
            () -> new AttachmentItem(new Item.Properties(), TDTooltipHelper.AttachmentTooltipType.RETRIEVER, AttachmentTier.RESONANT, (be, side) -> AttachmentPlacementHelper.createTransferAttachment(be, side, (duct, dir) -> new RetrieverFluid(duct, dir, AttachmentTier.RESONANT), (duct, dir) -> new RetrieverItem(duct, dir, AttachmentTier.RESONANT)), "info.modular_networks.retriever.info"));

    public static final DeferredItem<RelayItem> RELAY = ITEMS.register("relay",
            () -> new RelayItem(new Item.Properties()));
    public static final DeferredItem<CoverItem> COVER = ITEMS.register("cover",
            () -> new CoverItem(new Item.Properties()));

    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties().stacksTo(1)));

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
