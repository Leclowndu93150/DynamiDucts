package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DDCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerEnergyCaps(event);
        registerFluidCaps(event);
        registerItemCaps(event);
    }

    private static void registerEnergyCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_BASIC.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_HARDENED.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_REINFORCED.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_SIGNALUM.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_RESONANT.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR.get(), (be, dir) -> be.getEnergyCapability(dir));
    }

    private static void registerFluidCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_BASIC.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_BASIC_OPAQUE.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_HARDENED.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_HARDENED_OPAQUE.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_ENERGY.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_SUPER.get(), (be, dir) -> be.getFluidCapability(dir));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DDBlockEntities.FLUID_DUCT_SUPER_OPAQUE.get(), (be, dir) -> be.getFluidCapability(dir));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.FLUID_DUCT_ENERGY.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get(), (be, dir) -> be.getEnergyCapability(dir));
    }

    private static void registerItemCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_BASIC.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_BASIC_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_DENSE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_DENSE_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_VACUUM.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_VACUUM_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_FAST.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_FAST_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_FAST.get(), (be, dir) -> be.getItemCapability(dir));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(), (be, dir) -> be.getItemCapability(dir));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_FAST.get(), (be, dir) -> be.getEnergyCapability(dir));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(), (be, dir) -> be.getEnergyCapability(dir));
    }
}
