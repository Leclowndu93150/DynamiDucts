package com.leclowndu93150.modular_networks.datagen;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MNLanguageProvider extends LanguageProvider {

    public MNLanguageProvider(PackOutput output) {
        super(output, ModularNetworks.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.modular_networks", "Modular Networks");

        addBlock(MNBlocks.ENERGY_DUCT_BASIC, "Leadstone Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_HARDENED, "Hardened Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_REINFORCED, "Redstone Energy Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_SIGNALUM, "Signalum Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_RESONANT, "Resonant Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR, "Cryo-Stabilized Fluxduct");
        addBlock(MNBlocks.ENERGY_DUCT_REINFORCED_EMPTY, "Redstone Energy Fluxduct (Empty)");
        addBlock(MNBlocks.ENERGY_DUCT_SIGNALUM_EMPTY, "Signalum Fluxduct (Empty)");
        addBlock(MNBlocks.ENERGY_DUCT_RESONANT_EMPTY, "Resonant Fluxduct (Empty)");
        addBlock(MNBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, "Cryo-Stabilized Fluxduct (Empty)");

        addBlock(MNBlocks.FLUID_DUCT_BASIC, "Fluiduct");
        addBlock(MNBlocks.FLUID_DUCT_BASIC_OPAQUE, "Fluiduct (Opaque)");
        addBlock(MNBlocks.FLUID_DUCT_HARDENED, "Hardened Fluiduct");
        addBlock(MNBlocks.FLUID_DUCT_HARDENED_OPAQUE, "Hardened Fluiduct (Opaque)");
        addBlock(MNBlocks.FLUID_DUCT_ENERGY, "Signalum-Plated Fluiduct");
        addBlock(MNBlocks.FLUID_DUCT_ENERGY_OPAQUE, "Signalum-Plated Fluiduct (Opaque)");
        addBlock(MNBlocks.FLUID_DUCT_SUPER, "Super-Laminar Fluiduct");
        addBlock(MNBlocks.FLUID_DUCT_SUPER_OPAQUE, "Super-Laminar Fluiduct (Opaque)");

        addBlock(MNBlocks.ITEM_DUCT_BASIC, "Itemduct");
        addBlock(MNBlocks.ITEM_DUCT_BASIC_OPAQUE, "Itemduct (Opaque)");
        addBlock(MNBlocks.ITEM_DUCT_FAST, "Impulse Itemduct");
        addBlock(MNBlocks.ITEM_DUCT_FAST_OPAQUE, "Impulse Itemduct (Opaque)");
        addBlock(MNBlocks.ITEM_DUCT_ENERGY, "Signalum-Plated Itemduct");
        addBlock(MNBlocks.ITEM_DUCT_ENERGY_OPAQUE, "Signalum-Plated Itemduct (Opaque)");
        addBlock(MNBlocks.ITEM_DUCT_ENERGY_FAST, "Signalum-Plated Impulse Itemduct");
        addBlock(MNBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE, "Signalum-Plated Impulse Itemduct (Opaque)");

        addBlock(MNBlocks.TRANSPORT_DUCT_BASIC, "Viaduct");
        addBlock(MNBlocks.TRANSPORT_DUCT_LONG_RANGE, "Long Range Viaduct");
        addBlock(MNBlocks.TRANSPORT_DUCT_LINKING, "Long Range Linking Viaduct");
        addBlock(MNBlocks.TRANSPORT_DUCT_FRAME, "Viaduct (Untreated)");

        addBlock(MNBlocks.STRUCTURAL_DUCT, "Structuralduct");
        addBlock(MNBlocks.LUX_DUCT, "Luxduct");

        addItem(MNItems.SERVO_BASIC, "Servo");
        addItem(MNItems.SERVO_HARDENED, "Hardened Servo");
        addItem(MNItems.SERVO_REINFORCED, "Reinforced Servo");
        addItem(MNItems.SERVO_SIGNALUM, "Signalum Servo");
        addItem(MNItems.SERVO_RESONANT, "Resonant Servo");

        addItem(MNItems.FILTER_BASIC, "Filter");
        addItem(MNItems.FILTER_HARDENED, "Hardened Filter");
        addItem(MNItems.FILTER_REINFORCED, "Reinforced Filter");
        addItem(MNItems.FILTER_SIGNALUM, "Signalum Filter");
        addItem(MNItems.FILTER_RESONANT, "Resonant Filter");

        addItem(MNItems.RETRIEVER_BASIC, "Retriever");
        addItem(MNItems.RETRIEVER_HARDENED, "Hardened Retriever");
        addItem(MNItems.RETRIEVER_REINFORCED, "Reinforced Retriever");
        addItem(MNItems.RETRIEVER_SIGNALUM, "Signalum Retriever");
        addItem(MNItems.RETRIEVER_RESONANT, "Resonant Retriever");

        addItem(MNItems.RELAY, "Redstone Relay");
        addItem(MNItems.COVER, "Cover");
        addItem(MNItems.WRENCH, "Crescent Hammer");

        add("info.modular_networks.duct.energy", "Transfers Redstone Flux (RF).");
        add("info.modular_networks.duct.fluid", "Transfers Fluids.");
        add("info.modular_networks.duct.fluidEnergy", "Transfers Fluids and Redstone Flux (RF).");
        add("info.modular_networks.duct.item", "Transfers Items.");
        add("info.modular_networks.duct.itemEnergy", "Transfers Items and Redstone Flux (RF).");
        add("info.modular_networks.duct.itemFast", "Items travel more rapidly.");
        add("info.modular_networks.duct.structure", "Provides Structure.");
        add("info.modular_networks.duct.crafting", "Crafting Item.");
        add("info.modular_networks.duct.cover", "Combine with a Block to create Covers.");
        add("info.modular_networks.duct.transport", "Transfers Players. Whoosh.");
        add("info.modular_networks.duct.transportLongRange", "Fast transport between two specific locations.");
        add("info.modular_networks.duct.transportCrossover", "End points for Long Range Viaducts. Required.");
        add("info.modular_networks.duct.light", "Emits light when given a Redstone signal.");

        add("info.modular_networks.duct.fluidBasic", "Will break if contents are extremely hot or cold.");
        add("info.modular_networks.duct.fluidHardened", "Contents may be any temperature.");
        add("info.modular_networks.duct.fluidSuper", "Limitless transfer rate when pressurized.");
        add("info.modular_networks.duct.energySuper", "Has no residual capacitance.");

        add("info.modular_networks.transfer", "Transfer");
        add("info.modular_networks.transferConnection", "Transfer amount is per connection.");
        add("info.modular_networks.transferFluid", "Transfer rate varies by fluid.");

        add("info.modular_networks.servo.info", "Extracts items/fluids from something.");
        add("info.modular_networks.filter.info", "Restricts what may pass through it.");
        add("info.modular_networks.retriever.info", "Pulls distant items/fluids to itself.");
        add("info.modular_networks.relay.info", "Transmits redstone signals.");

        add("info.modular_networks.filter.whiteList.off", "Whitelist");
        add("info.modular_networks.filter.whiteList.on", "Blacklist");
        add("info.modular_networks.filter.routeType.0", "Nearest-First");
        add("info.modular_networks.filter.routeType.1", "Furthest-First");
        add("info.modular_networks.filter.routeType.2", "Random");
        add("info.modular_networks.filter.routeType.3", "Round Robin");

        for (int i = 0; i < 16; i++) {
            String[] colors = {"Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Light Gray", "Gray",
                    "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White", "Black"};
            add("info.modular_networks.relay.color." + i, colors[i]);
        }

        add("info.modular_networks.relay.type.0", "Redstone Input");
        add("info.modular_networks.relay.type.1", "Redstone Output");
        add("info.modular_networks.relay.type.2", "Comparator Input");
    }
}
