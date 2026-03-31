package com.leclowndu93150.dynamiducts;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = DynamiDucts.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MNConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue ENERGY_BASE_TRANSFER = BUILDER
            .comment("Base energy transfer rate (RF/t) for Leadstone Fluxduct. Higher tiers scale from this.")
            .defineInRange("energy.baseTransfer", 1000, 100, 10000);

    private static final ModConfigSpec.IntValue FLUID_BASE_CAPACITY = BUILDER
            .comment("Base fluid capacity (mB) per duct in a fluid network.")
            .defineInRange("fluid.baseCapacity", 3000, 500, 24000);

    private static final ModConfigSpec.IntValue FLUID_BASE_THROUGHPUT = BUILDER
            .comment("Base fluid throughput (mB/t) per duct.")
            .defineInRange("fluid.baseThroughput", 600, 100, 6000);

    private static final ModConfigSpec.IntValue ITEM_BASE_SPEED = BUILDER
            .comment("Base item travel speed in ticks per duct.")
            .defineInRange("item.baseSpeed", 40, 10, 200);

    private static final ModConfigSpec.IntValue ITEM_FAST_SPEED = BUILDER
            .comment("Fast item travel speed in ticks per duct (Impulse Itemducts).")
            .defineInRange("item.fastSpeed", 10, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int energyBaseTransfer;
    public static int fluidBaseCapacity;
    public static int fluidBaseThroughput;
    public static int itemBaseSpeed;
    public static int itemFastSpeed;

    public static int[] energyTransferRates() {
        return new int[]{
                energyBaseTransfer,
                energyBaseTransfer * 4,
                energyBaseTransfer * 9,
                energyBaseTransfer * 16,
                energyBaseTransfer * 25,
                0
        };
    }

    public static int[] energyCapacities() {
        return new int[]{
                energyBaseTransfer * 5,
                energyBaseTransfer * 20,
                energyBaseTransfer * 45,
                energyBaseTransfer * 80,
                energyBaseTransfer * 125,
                0
        };
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        energyBaseTransfer = ENERGY_BASE_TRANSFER.get();
        fluidBaseCapacity = FLUID_BASE_CAPACITY.get();
        fluidBaseThroughput = FLUID_BASE_THROUGHPUT.get();
        itemBaseSpeed = ITEM_BASE_SPEED.get();
        itemFastSpeed = ITEM_FAST_SPEED.get();
    }
}
