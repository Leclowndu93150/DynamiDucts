package com.leclowndu93150.dynamiducts.datagen;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import com.leclowndu93150.dynamiducts.init.DDItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class DDLanguageProvider extends LanguageProvider {

    public DDLanguageProvider(PackOutput output) {
        super(output, DynamiDucts.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.dynamiducts", "DynamiDucts");

        addBlock(DDBlocks.ENERGY_DUCT_BASIC, "Leadstone Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_HARDENED, "Hardened Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_REINFORCED, "Redstone Energy Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_SIGNALUM, "Signalum Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_RESONANT, "Resonant Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR, "Cryo-Stabilized Fluxduct");
        addBlock(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY, "Redstone Energy Fluxduct (Empty)");
        addBlock(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY, "Signalum Fluxduct (Empty)");
        addBlock(DDBlocks.ENERGY_DUCT_RESONANT_EMPTY, "Resonant Fluxduct (Empty)");
        addBlock(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, "Cryo-Stabilized Fluxduct (Empty)");

        addBlock(DDBlocks.FLUID_DUCT_BASIC, "Fluiduct");
        addBlock(DDBlocks.FLUID_DUCT_BASIC_OPAQUE, "Fluiduct (Opaque)");
        addBlock(DDBlocks.FLUID_DUCT_HARDENED, "Hardened Fluiduct");
        addBlock(DDBlocks.FLUID_DUCT_HARDENED_OPAQUE, "Hardened Fluiduct (Opaque)");
        addBlock(DDBlocks.FLUID_DUCT_ENERGY, "Signalum-Plated Fluiduct");
        addBlock(DDBlocks.FLUID_DUCT_ENERGY_OPAQUE, "Signalum-Plated Fluiduct (Opaque)");
        addBlock(DDBlocks.FLUID_DUCT_SUPER, "Super-Laminar Fluiduct");
        addBlock(DDBlocks.FLUID_DUCT_SUPER_OPAQUE, "Super-Laminar Fluiduct (Opaque)");

        addBlock(DDBlocks.ITEM_DUCT_BASIC, "Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_BASIC_OPAQUE, "Itemduct (Opaque)");
        addBlock(DDBlocks.ITEM_DUCT_DENSE, "Dense Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_DENSE_OPAQUE, "Dense Itemduct (Opaque)");
        addBlock(DDBlocks.ITEM_DUCT_VACUUM, "Vacuum Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_VACUUM_OPAQUE, "Vacuum Itemduct (Opaque)");
        addBlock(DDBlocks.ITEM_DUCT_FAST, "Impulse Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_FAST_OPAQUE, "Impulse Itemduct (Opaque)");
        addBlock(DDBlocks.ITEM_DUCT_ENERGY, "Signalum-Plated Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_ENERGY_OPAQUE, "Signalum-Plated Itemduct (Opaque)");
        addBlock(DDBlocks.ITEM_DUCT_ENERGY_FAST, "Signalum-Plated Impulse Itemduct");
        addBlock(DDBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE, "Signalum-Plated Impulse Itemduct (Opaque)");

        addBlock(DDBlocks.TRANSPORT_DUCT_BASIC, "Viaduct");
        addBlock(DDBlocks.TRANSPORT_DUCT_LONG_RANGE, "Long Range Viaduct");
        addBlock(DDBlocks.TRANSPORT_DUCT_LINKING, "Long Range Linking Viaduct");
        addBlock(DDBlocks.TRANSPORT_DUCT_FRAME, "Viaduct (Untreated)");

        addBlock(DDBlocks.STRUCTURAL_DUCT, "Structuralduct");
        addBlock(DDBlocks.LUX_DUCT, "Luxduct");

        addItem(DDItems.SERVO_BASIC, "Servo");
        addItem(DDItems.SERVO_HARDENED, "Hardened Servo");
        addItem(DDItems.SERVO_REINFORCED, "Reinforced Servo");
        addItem(DDItems.SERVO_SIGNALUM, "Signalum Servo");
        addItem(DDItems.SERVO_RESONANT, "Resonant Servo");

        addItem(DDItems.FILTER_BASIC, "Filter");
        addItem(DDItems.FILTER_HARDENED, "Hardened Filter");
        addItem(DDItems.FILTER_REINFORCED, "Reinforced Filter");
        addItem(DDItems.FILTER_SIGNALUM, "Signalum Filter");
        addItem(DDItems.FILTER_RESONANT, "Resonant Filter");

        addItem(DDItems.RETRIEVER_BASIC, "Retriever");
        addItem(DDItems.RETRIEVER_HARDENED, "Hardened Retriever");
        addItem(DDItems.RETRIEVER_REINFORCED, "Reinforced Retriever");
        addItem(DDItems.RETRIEVER_SIGNALUM, "Signalum Retriever");
        addItem(DDItems.RETRIEVER_RESONANT, "Resonant Retriever");

        addItem(DDItems.RELAY, "Redstone Relay");
        addItem(DDItems.WRENCH, "Crescent Hammer");

        addItem(DDItems.LEAD_INGOT, "Lead Ingot");
        addItem(DDItems.LEAD_NUGGET, "Lead Nugget");
        addItem(DDItems.TIN_INGOT, "Tin Ingot");
        addItem(DDItems.TIN_NUGGET, "Tin Nugget");
        addItem(DDItems.SILVER_INGOT, "Silver Ingot");
        addItem(DDItems.SILVER_NUGGET, "Silver Nugget");
        addItem(DDItems.INVAR_INGOT, "Invar Ingot");
        addItem(DDItems.INVAR_NUGGET, "Invar Nugget");
        addItem(DDItems.ELECTRUM_INGOT, "Electrum Ingot");
        addItem(DDItems.ELECTRUM_NUGGET, "Electrum Nugget");
        addItem(DDItems.BRONZE_INGOT, "Bronze Ingot");
        addItem(DDItems.BRONZE_NUGGET, "Bronze Nugget");
        addItem(DDItems.SIGNALUM_INGOT, "Signalum Ingot");
        addItem(DDItems.SIGNALUM_NUGGET, "Signalum Nugget");
        addItem(DDItems.ENDERIUM_INGOT, "Enderium Ingot");
        addItem(DDItems.ENDERIUM_NUGGET, "Enderium Nugget");
        addItem(DDItems.LUMIUM_INGOT, "Lumium Ingot");
        addItem(DDItems.LUMIUM_NUGGET, "Lumium Nugget");

        add("info.dynamiducts.duct.energy", "Transfers Redstone Flux (RF).");
        add("info.dynamiducts.duct.fluid", "Transfers Fluids.");
        add("info.dynamiducts.duct.fluidEnergy", "Transfers Fluids and Redstone Flux (RF).");
        add("info.dynamiducts.duct.item", "Transfers Items.");
        add("info.dynamiducts.duct.itemEnergy", "Transfers Items and Redstone Flux (RF).");
        add("info.dynamiducts.duct.itemFast", "Items travel more rapidly.");
        add("info.dynamiducts.duct.structure", "Provides Structure.");
        add("info.dynamiducts.duct.crafting", "Crafting Item.");
        add("info.dynamiducts.duct.transport", "Transfers Players. Whoosh.");
        add("info.dynamiducts.duct.transportLongRange", "Fast transport between two specific locations.");
        add("info.dynamiducts.duct.transportCrossover", "End points for Long Range Viaducts. Required.");
        add("info.dynamiducts.duct.light", "Emits light when given a Redstone signal.");
        add("info.dynamiducts.duct.dense", "Increases path length dramatically.");
        add("info.dynamiducts.duct.vacuum", "Decreases path length dramatically.");
        add("info.dynamiducts.information", "Information");
        add("info.dynamiducts.redstoneControl", "Redstone Control");
        add("info.dynamiducts.controlStatus", "Control Status");
        add("info.dynamiducts.signalRequired", "Signal Required");
        add("info.dynamiducts.ignored", "Ignored");
        add("info.dynamiducts.enabled", "Enabled");
        add("info.dynamiducts.disabled", "Disabled");
        add("info.dynamiducts.low", "Low");
        add("info.dynamiducts.high", "High");
        add("info.dynamiducts.info.redstone", "Redstone");
        add("tab.dynamiducts.conChange", "Hold Shift and use either mouse button to fine tune quantities.");

        add("info.dynamiducts.duct.fluidBasic", "Will break if contents are extremely hot or cold.");
        add("info.dynamiducts.duct.fluidHardened", "Contents may be any temperature.");
        add("info.dynamiducts.duct.fluidSuper", "Limitless transfer rate when pressurized.");
        add("info.dynamiducts.duct.energySuper", "Has no residual capacitance.");

        add("info.dynamiducts.transfer", "Transfer");
        add("info.dynamiducts.transferConnection", "Transfer amount is per connection.");
        add("info.dynamiducts.transferFluid", "Transfer rate varies by fluid.");

        add("info.dynamiducts.servo.info", "Extracts items/fluids from something.");
        add("info.dynamiducts.filter.info", "Restricts what may pass through it.");
        add("info.dynamiducts.retriever.info", "Pulls distant items/fluids to itself.");
        add("info.dynamiducts.relay.info", "Transmits redstone signals.");
        add("info.dynamiducts.toggle", "Right click to toggle mode.");

        add("info.dynamiducts.cofh.items", "Items");
        add("info.dynamiducts.cofh.fluids", "Fluids");
        add("info.dynamiducts.cofh.infinite", "Infinite");

        add("info.dynamiducts.filter.whiteList.off", "Whitelist");
        add("info.dynamiducts.filter.whiteList.on", "Blacklist");
        add("info.dynamiducts.filter.components.off", "Match Components");
        add("info.dynamiducts.filter.components.on", "Ignore Components");
        add("info.dynamiducts.filter.metadata", "Metadata");
        add("info.dynamiducts.filter.modSorting.off", "Match Mod Owner");
        add("info.dynamiducts.filter.modSorting.on", "Ignore Mod Owner");
        add("info.dynamiducts.filter.nbt", "NBT");
        add("info.dynamiducts.filter.oreDict", "Ore Dictionary");
        add("info.dynamiducts.filter.modSorting", "Mod Owner");
        add("info.dynamiducts.filter.options", "Filter Options");
        add("info.dynamiducts.filter.routeType.0", "Nearest-First");
        add("info.dynamiducts.filter.routeType.1", "Furthest-First");
        add("info.dynamiducts.filter.routeType.2", "Random");
        add("info.dynamiducts.filter.routeType.3", "Round Robin");
        add("info.dynamiducts.filter.decRetainSize", "Dec. Max Total Items in Inventory");
        add("info.dynamiducts.filter.incRetainSize", "Inc. Max Total Items in Inventory");
        add("info.dynamiducts.filter.zeroRetainSize", "Infinite");

        add("info.dynamiducts.servo.extractRate", "Extraction Rate");
        add("info.dynamiducts.servo.maxStackSize", "Max Stack Size");
        add("info.dynamiducts.servo.decStackSize", "Decrease Max Stack Size by %s");
        add("info.dynamiducts.servo.incStackSize", "Increase Max Stack Size by %s");
        add("info.dynamiducts.servo.redstoneExt", "Requires Redstone Signal");
        add("info.dynamiducts.servo.redstoneInt", "Internal Redstone Control");
        add("info.dynamiducts.servo.slotMulti", "Extracts from multiple slots.");
        add("info.dynamiducts.servo.slotSingle", "Extracts from a single slot.");
        add("info.dynamiducts.servo.speedBoost", "Speed Boost");
        add("info.dynamiducts.redstoneMode.0", "Ignored");
        add("info.dynamiducts.redstoneMode.1", "Active without Signal");
        add("info.dynamiducts.redstoneMode.2", "Active with Signal");

        for (int i = 0; i < 16; i++) {
            String[] colors = {"Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Light Gray", "Gray",
                    "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White", "Black"};
            add("info.dynamiducts.relay.color." + i, colors[i]);
        }

        add("info.dynamiducts.relay.type.0", "Redstone Input");
        add("info.dynamiducts.relay.type.1", "Redstone Output");
        add("info.dynamiducts.relay.type.2", "Comparator Input");
        add("info.dynamiducts.relay.invert.0", "Scaled");
        add("info.dynamiducts.relay.invert.1", "Inverted Scaled");
        add("info.dynamiducts.relay.invert.2", "Threshold");
        add("info.dynamiducts.relay.invert.3", "Inverted Threshold");
        add("info.dynamiducts.relay.threshold", "Threshold: %s");
        add("info.dynamiducts.relay.relayRS", "Relay Power: %s");
        add("info.dynamiducts.relay.gridRS", "Duct Power: %s");

        add("gui.dynamiducts.transport.title", "Select Destination");
        add("gui.dynamiducts.transport.config", "Configure");
        add("gui.dynamiducts.transport.noDestinations", "No destinations available.");
        add("gui.dynamiducts.transport.unnamed", "Unnamed");
        add("entity.dynamiducts.transport", "Transport");

        add("info.dynamiducts.jade.active", "Active");
        add("info.dynamiducts.jade.inactive", "Inactive");
        add("info.dynamiducts.jade.status", "Status");
        add("info.dynamiducts.jade.channel", "Channel");
        add("info.dynamiducts.jade.mode", "Mode");
    }
}
