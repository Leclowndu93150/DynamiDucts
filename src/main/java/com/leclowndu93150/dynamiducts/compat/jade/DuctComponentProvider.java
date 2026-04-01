package com.leclowndu93150.dynamiducts.compat.jade;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.attachment.cover.Cover;
import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.init.DDItems;
import com.leclowndu93150.dynamiducts.item.CoverItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import org.jetbrains.annotations.Nullable;

public enum DuctComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "duct_info");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("DDIcon")) {
            ItemStack icon = ItemStack.parseOptional(accessor.getLevel().registryAccess(), data.getCompound("DDIcon"));
            if (!icon.isEmpty()) {
                return IElementHelper.get().item(icon);
            }
        }
        return null;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("DDAttType")) return;

        if (data.contains("DDIcon")) {
            ItemStack icon = ItemStack.parseOptional(accessor.getLevel().registryAccess(), data.getCompound("DDIcon"));
            if (!icon.isEmpty()) {
                ChatFormatting rarityColor = icon.getRarity().color();
                tooltip.replace(JadeIds.CORE_OBJECT_NAME, icon.getHoverName().copy().withStyle(rarityColor));
            }
        }

        String type = data.getString("DDAttType");

        if (data.contains("DDRedstone")) {
            boolean active = data.getBoolean("DDActive");
            Component status = active
                    ? Component.translatable("info.dynamiducts.jade.active").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("info.dynamiducts.jade.inactive").withStyle(ChatFormatting.RED);
            tooltip.add(Component.translatable("info.dynamiducts.jade.status").append(": ").append(status));
        }

        if (type.equals("Relay")) {
            int color = data.getInt("DDColor");
            String relayMode = data.getString("DDRelayMode");
            tooltip.add(Component.translatable("info.dynamiducts.jade.channel")
                    .append(": ").append(Component.translatable("info.dynamiducts.relay.color." + color)));
            tooltip.add(Component.translatable("info.dynamiducts.jade.mode")
                    .append(": ").append(relayMode));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof DuctBlockEntity ductBE)) return;

        BlockHitResult hitResult = accessor.getHitResult();
        BlockState state = accessor.getBlockState();
        DuctHitHelper.DuctHit hit = DuctHitHelper.resolve(state, ductBE, accessor.getPosition(), hitResult);

        if (hit.part() != DuctHitHelper.HitPart.COLLAR && hit.part() != DuctHitHelper.HitPart.CABLE) return;

        Direction side = hit.side();
        Attachment att = ductBE.getAttachment(side);
        if (att == null) return;

        ItemStack icon = getAttachmentItem(att);
        if (!icon.isEmpty()) {
            data.put("DDIcon", icon.saveOptional(accessor.getLevel().registryAccess()));
        }

        if (att instanceof ConnectionBase conn) {
            data.putString("DDAttType", conn.isServo() ? "Servo" : conn.isFilter() ? "Filter" : conn.isRetriever() ? "Retriever" : "Unknown");
            data.putString("DDRedstone", conn.getRedstoneMode().getSerializedName());
            data.putBoolean("DDActive", conn.isActive());
        } else if (att instanceof Relay relay) {
            data.putString("DDAttType", "Relay");
            String relayMode = switch (relay.getType()) {
                case Relay.TYPE_REDSTONE_INPUT -> "Redstone Input";
                case Relay.TYPE_REDSTONE_OUTPUT -> "Redstone Output";
                case Relay.TYPE_COMPARATOR_INPUT -> "Comparator Input";
                default -> "Unknown";
            };
            data.putString("DDRelayMode", relayMode);
            data.putInt("DDColor", relay.getColor());
        } else if (att instanceof Cover) {
            data.putString("DDAttType", "Cover");
        }
    }

    private static ItemStack getAttachmentItem(Attachment att) {
        if (att instanceof Cover cover) {
            return CoverItem.createForState(cover.getCoverState(), 1);
        }
        if (att instanceof Relay) {
            return new ItemStack(DDItems.RELAY.get());
        }
        if (att instanceof ConnectionBase conn) {
            int tier = conn.getTier().index();
            if (conn.isServo()) {
                return new ItemStack(switch (tier) {
                    case 0 -> DDItems.SERVO_BASIC.get();
                    case 1 -> DDItems.SERVO_HARDENED.get();
                    case 2 -> DDItems.SERVO_REINFORCED.get();
                    case 3 -> DDItems.SERVO_SIGNALUM.get();
                    default -> DDItems.SERVO_RESONANT.get();
                });
            }
            if (conn.isFilter()) {
                return new ItemStack(switch (tier) {
                    case 0 -> DDItems.FILTER_BASIC.get();
                    case 1 -> DDItems.FILTER_HARDENED.get();
                    case 2 -> DDItems.FILTER_REINFORCED.get();
                    case 3 -> DDItems.FILTER_SIGNALUM.get();
                    default -> DDItems.FILTER_RESONANT.get();
                });
            }
            if (conn.isRetriever()) {
                return new ItemStack(switch (tier) {
                    case 0 -> DDItems.RETRIEVER_BASIC.get();
                    case 1 -> DDItems.RETRIEVER_HARDENED.get();
                    case 2 -> DDItems.RETRIEVER_REINFORCED.get();
                    case 3 -> DDItems.RETRIEVER_SIGNALUM.get();
                    default -> DDItems.RETRIEVER_RESONANT.get();
                });
            }
        }
        return ItemStack.EMPTY;
    }
}
