package com.leclowndu93150.dynamiducts.compat.jade;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import org.jetbrains.annotations.Nullable;

public enum DuctComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    static final Identifier UID = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "duct_info");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, Element currentIcon) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("DDIcon")) {
            ItemStack icon = data.read("DDIcon", ItemStack.CODEC, DuctServerDataProvider.registryOps(accessor)).orElse(ItemStack.EMPTY);
            if (!icon.isEmpty()) {
                return JadeUI.item(icon);
            }
        }
        return null;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("DDAttType")) return;

        if (data.contains("DDIcon")) {
            ItemStack icon = data.read("DDIcon", ItemStack.CODEC, DuctServerDataProvider.registryOps(accessor)).orElse(ItemStack.EMPTY);
            if (!icon.isEmpty()) {
                ChatFormatting rarityColor = icon.getRarity().color();
                tooltip.replace(JadeIds.CORE_OBJECT_NAME, icon.getHoverName().copy().withStyle(rarityColor));
            }
        }

        String type = data.getStringOr("DDAttType", "");

        if (data.contains("DDRedstone")) {
            boolean active = data.getBooleanOr("DDActive", false);
            Component status = active
                    ? Component.translatable("info.dynamiducts.jade.active").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("info.dynamiducts.jade.inactive").withStyle(ChatFormatting.RED);
            tooltip.add(Component.translatable("info.dynamiducts.jade.status").append(": ").append(status));
        }

        if (type.equals("Relay")) {
            int color = data.getIntOr("DDColor", 0);
            String relayMode = data.getStringOr("DDRelayMode", "");
            tooltip.add(Component.translatable("info.dynamiducts.jade.channel")
                    .append(": ").append(Component.translatable("info.dynamiducts.relay.color." + color)));
            tooltip.add(Component.translatable("info.dynamiducts.jade.mode")
                    .append(": ").append(relayMode));
        }
    }
}
