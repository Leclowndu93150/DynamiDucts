package com.leclowndu93150.dynamiducts.compat.jade;

import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.init.DDItems;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum DuctServerDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return DuctComponentProvider.UID;
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
            data.store("DDIcon", ItemStack.CODEC, registryOps(accessor), icon);
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
        }
    }

    static RegistryOps<Tag> registryOps(BlockAccessor accessor) {
        return accessor.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
    }

    private static ItemStack getAttachmentItem(Attachment att) {
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
