package com.leclowndu93150.dynamiducts.item;

import com.leclowndu93150.dynamiducts.MNConfig;
import com.leclowndu93150.dynamiducts.block.EnergyDuctBlock;
import com.leclowndu93150.dynamiducts.block.FluidDuctBlock;
import com.leclowndu93150.dynamiducts.block.ItemDuctBlock;
import com.leclowndu93150.dynamiducts.block.StructuralDuctBlock;
import com.leclowndu93150.dynamiducts.block.TransportDuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.EnergyDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.FluidDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.TransportDuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import com.leclowndu93150.dynamiducts.init.DDDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class DDTooltipHelper {

    public enum AttachmentTooltipType {
        SERVO("info.dynamiducts.servo.info"),
        FILTER("info.dynamiducts.filter.info"),
        RETRIEVER("info.dynamiducts.retriever.info");

        private final String summaryKey;

        AttachmentTooltipType(String summaryKey) {
            this.summaryKey = summaryKey;
        }
    }

    private static final int DEFAULT_ENERGY_BASE_TRANSFER = 1000;
    private static final byte PATHWEIGHT_DENSE = 1;
    private static final byte PATHWEIGHT_VACUUM = 2;
    private static final int[] SERVO_FLUID_THROTTLE = {50, 75, 100, 150, 200};
    private static final String[] FILTER_FLAG_KEYS = {
            "info.dynamiducts.filter.whiteList.off",
            "info.dynamiducts.filter.metadata",
            "info.dynamiducts.filter.nbt",
            "info.dynamiducts.filter.oreDict",
            "info.dynamiducts.filter.modSorting"
    };
    private static final int[] ITEM_FILTER_MAX_FLAG = {0, 1, 4, 4, 4};

    private DDTooltipHelper() {
    }

    public static void appendDuctTooltip(ItemStack stack, Block block, List<Component> tooltip) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(shiftForDetails());
            if (block == DDBlocks.STRUCTURAL_DUCT.get()) {
                tooltip.add(info("info.dynamiducts.duct.cover"));
            }
            return;
        }
        if (block instanceof EnergyDuctBlock energyBlock) {
            addEnergyDuctTooltip(energyBlock.getTier(), tooltip);
            return;
        }
        if (block instanceof FluidDuctBlock fluidBlock) {
            addFluidDuctTooltip(fluidBlock, tooltip);
            return;
        }
        if (block instanceof ItemDuctBlock itemBlock) {
            addItemDuctTooltip(stack, itemBlock, tooltip);
            return;
        }
        if (block instanceof TransportDuctBlock transportBlock) {
            addTransportDuctTooltip(transportBlock, tooltip);
            return;
        }
        if (block instanceof StructuralDuctBlock) {
            addStructuralDuctTooltip(block, tooltip);
        }
    }

    public static void appendAttachmentTooltip(AttachmentTooltipType type, AttachmentTier tier, List<Component> tooltip) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(info(type.summaryKey));
            tooltip.add(shiftForDetails());
            return;
        }
        int tierIndex = tier.index();
        if (type == AttachmentTooltipType.FILTER) {
            tooltip.add(heading("info.dynamiducts.cofh.items"));
            addFilterOptions(tooltip, true, tierIndex);
            tooltip.add(heading("info.dynamiducts.cofh.fluids"));
            addFilterOptions(tooltip, false, tierIndex);
            return;
        }

        tooltip.add(info("info.dynamiducts.servo.redstoneInt"));
        tooltip.add(heading("info.dynamiducts.cofh.items"));
        tooltip.add(detail("info.dynamiducts.servo.extractRate", formatSeconds(tier.tickRate())));
        tooltip.add(detail("info.dynamiducts.servo.maxStackSize", Integer.toString(tier.stackSize())));
        addFilterOptions(tooltip, true, tierIndex);
        tooltip.add(info(tier.multiStack() ? "info.dynamiducts.servo.slotMulti" : "info.dynamiducts.servo.slotSingle"));
        if (tier.speedBoost() != 1) {
            tooltip.add(detail("info.dynamiducts.servo.speedBoost", tier.speedBoost() + "x"));
        }
        tooltip.add(heading("info.dynamiducts.cofh.fluids"));
        tooltip.add(detail("info.dynamiducts.servo.extractRate", SERVO_FLUID_THROTTLE[tierIndex] + "%"));
        addFilterOptions(tooltip, false, tierIndex);
    }

    public static void appendRelayTooltip(List<Component> tooltip) {
        tooltip.add(info("info.dynamiducts.relay.info"));
        tooltip.add(notice("info.dynamiducts.toggle"));
    }

    private static void addEnergyDuctTooltip(EnergyDuctBlockEntity.Tier tier, List<Component> tooltip) {
        if (tier.isCraftingItem()) {
            tooltip.add(info("info.dynamiducts.duct.crafting"));
            return;
        }
        tooltip.add(info("info.dynamiducts.duct.energy"));
        if (tier == EnergyDuctBlockEntity.Tier.SUPERCONDUCTOR) {
            tooltip.add(transferLine(Component.translatable("info.dynamiducts.cofh.infinite").withStyle(ChatFormatting.AQUA)));
            tooltip.add(info("info.dynamiducts.duct.energySuper"));
        } else {
            tooltip.add(transferLine(Component.literal(Integer.toString(getEnergyRate(tier.index))).withStyle(ChatFormatting.YELLOW)));
        }
        tooltip.add(notice("info.dynamiducts.transferConnection"));
    }

    private static void addFluidDuctTooltip(FluidDuctBlock block, List<Component> tooltip, FluidDuctBlockEntity.Tier tier) {
        switch (tier) {
            case BASIC -> {
                tooltip.add(info("info.dynamiducts.duct.fluid"));
                tooltip.add(info("info.dynamiducts.duct.fluidBasic"));
            }
            case HARDENED -> {
                tooltip.add(info("info.dynamiducts.duct.fluid"));
                tooltip.add(info("info.dynamiducts.duct.fluidHardened"));
            }
            case ENERGY -> {
                tooltip.add(info("info.dynamiducts.duct.fluidEnergy"));
                tooltip.add(transferLine(Component.literal(Integer.toString(getHybridEnergyRate())).withStyle(ChatFormatting.YELLOW)));
                tooltip.add(info("info.dynamiducts.duct.fluidHardened"));
            }
            case SUPER -> {
                tooltip.add(info("info.dynamiducts.duct.fluid"));
                tooltip.add(info("info.dynamiducts.duct.fluidSuper"));
            }
        }
        if (tier != FluidDuctBlockEntity.Tier.SUPER) {
            tooltip.add(notice("info.dynamiducts.transferFluid"));
        }
    }

    private static void addItemDuctTooltip(ItemStack stack, ItemDuctBlock block, List<Component> tooltip) {
        ItemDuctBlockEntity.Tier tier = block.getTier();
        switch (tier) {
            case BASIC -> tooltip.add(info("info.dynamiducts.duct.item"));
            case FAST -> {
                tooltip.add(info("info.dynamiducts.duct.item"));
                tooltip.add(info("info.dynamiducts.duct.itemFast"));
            }
            case ENERGY -> {
                tooltip.add(info("info.dynamiducts.duct.itemEnergy"));
                tooltip.add(transferLine(Component.literal(Integer.toString(getHybridEnergyRate())).withStyle(ChatFormatting.YELLOW)));
            }
            case ENERGY_FAST -> {
                tooltip.add(info("info.dynamiducts.duct.itemEnergy"));
                tooltip.add(transferLine(Component.literal(Integer.toString(getHybridEnergyRate())).withStyle(ChatFormatting.YELLOW)));
                tooltip.add(info("info.dynamiducts.duct.itemFast"));
            }
        }
        Byte pathWeight = stack.get(DDDataComponents.DUCT_PATH_WEIGHT.get());
        if (pathWeight != null) {
            if (pathWeight == PATHWEIGHT_DENSE) {
                tooltip.add(info("info.dynamiducts.duct.dense"));
            } else if (pathWeight == PATHWEIGHT_VACUUM) {
                tooltip.add(info("info.dynamiducts.duct.vacuum"));
            }
        }
    }

    private static void addTransportDuctTooltip(TransportDuctBlock block, List<Component> tooltip) {
        TransportDuctBlockEntity.Tier tier = block.getTier();
        if (tier == TransportDuctBlockEntity.Tier.FRAME) {
            tooltip.add(info("info.dynamiducts.duct.crafting"));
            return;
        }
        tooltip.add(info("info.dynamiducts.duct.transport"));
        if (tier == TransportDuctBlockEntity.Tier.LONG_RANGE) {
            tooltip.add(info("info.dynamiducts.duct.transportLongRange"));
        } else if (tier == TransportDuctBlockEntity.Tier.LINKING) {
            tooltip.add(info("info.dynamiducts.duct.transportCrossover"));
        }
    }

    private static void addStructuralDuctTooltip(Block block, List<Component> tooltip) {
        tooltip.add(info("info.dynamiducts.duct.structure"));
        if (block == DDBlocks.STRUCTURAL_DUCT.get()) {
            tooltip.add(info("info.dynamiducts.duct.cover"));
        } else if (block == DDBlocks.LUX_DUCT.get()) {
            tooltip.add(info("info.dynamiducts.duct.light"));
        }
    }

    private static void addFluidDuctTooltip(FluidDuctBlock block, List<Component> tooltip) {
        addFluidDuctTooltip(block, tooltip, block.getTier());
    }

    private static void addFilterOptions(List<Component> tooltip, boolean itemTransfer, int tierIndex) {
        List<String> optionKeys = new ArrayList<>();
        if (itemTransfer) {
            int maxFlag = ITEM_FILTER_MAX_FLAG[Math.max(0, Math.min(tierIndex, ITEM_FILTER_MAX_FLAG.length - 1))];
            for (int i = 0; i <= maxFlag; i++) {
                optionKeys.add(FILTER_FLAG_KEYS[i]);
            }
        } else {
            optionKeys.add(FILTER_FLAG_KEYS[0]);
            optionKeys.add(FILTER_FLAG_KEYS[2]);
        }

        List<String> wrapped = wrapOptions(optionKeys, 32);
        for (int i = 0; i < wrapped.size(); i++) {
            MutableComponent line = Component.literal("  ");
            if (i == 0) {
                line.append(Component.translatable("info.dynamiducts.filter.options").withStyle(ChatFormatting.GRAY));
                line.append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
            }
            line.append(Component.literal(wrapped.get(i)).withStyle(ChatFormatting.WHITE));
            tooltip.add(line);
        }
    }

    private static List<String> wrapOptions(List<String> optionKeys, int maxChars) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String optionKey : optionKeys) {
            String option = Component.translatable(optionKey).getString();
            String candidate = current.isEmpty() ? option : current + ", " + option;
            if (!current.isEmpty() && candidate.length() > maxChars) {
                lines.add(current.toString());
                current = new StringBuilder(option);
            } else {
                current.setLength(0);
                current.append(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static MutableComponent shiftForDetails() {
        return Component.literal("Hold ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("Shift").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC))
                .append(Component.literal(" for Details").withStyle(ChatFormatting.GRAY));
    }

    private static MutableComponent transferLine(Component amount) {
        return Component.translatable("info.dynamiducts.transfer").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(amount)
                .append(Component.literal(" RF/t.").withStyle(ChatFormatting.GRAY));
    }

    private static MutableComponent detail(String key, String value) {
        return Component.literal("  ").append(Component.translatable(key).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(value).withStyle(ChatFormatting.WHITE));
    }

    private static MutableComponent info(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
    }

    private static MutableComponent heading(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.YELLOW);
    }

    private static MutableComponent notice(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.GOLD);
    }

    private static String formatSeconds(int ticks) {
        return ticks % 20 == 0 ? (ticks / 20) + "s" : (ticks / 20.0F) + "s";
    }

    private static int getEnergyBaseTransfer() {
        return MNConfig.energyBaseTransfer > 0 ? MNConfig.energyBaseTransfer : DEFAULT_ENERGY_BASE_TRANSFER;
    }

    private static int getEnergyRate(int tierIndex) {
        return switch (tierIndex) {
            case 0 -> getEnergyBaseTransfer();
            case 1 -> getEnergyBaseTransfer() * 4;
            case 2 -> getEnergyBaseTransfer() * 9;
            case 3 -> getEnergyBaseTransfer() * 16;
            case 4 -> getEnergyBaseTransfer() * 25;
            default -> 0;
        };
    }

    private static int getHybridEnergyRate() {
        return getEnergyBaseTransfer() * 4;
    }
}
