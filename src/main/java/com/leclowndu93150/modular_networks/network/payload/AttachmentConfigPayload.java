package com.leclowndu93150.modular_networks.network.payload;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.attachment.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AttachmentConfigPayload(
        BlockPos pos,
        int side,
        int action,
        int value
) implements CustomPacketPayload {

    public static final int ACTION_TOGGLE_WHITELIST = 0;
    public static final int ACTION_TOGGLE_MATCH_COMPONENTS = 1;
    public static final int ACTION_TOGGLE_MATCH_MOD = 2;
    public static final int ACTION_SET_REDSTONE_MODE = 3;
    public static final int ACTION_SET_MAX_STOCK = 4;
    public static final int ACTION_SET_ROUTE_TYPE = 5;

    public static final Type<AttachmentConfigPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "attachment_config"));

    public static final StreamCodec<FriendlyByteBuf, AttachmentConfigPayload> STREAM_CODEC = StreamCodec.of(
            AttachmentConfigPayload::write,
            AttachmentConfigPayload::read
    );

    private static void write(FriendlyByteBuf buf, AttachmentConfigPayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeByte(payload.side);
        buf.writeByte(payload.action);
        buf.writeVarInt(payload.value);
    }

    private static AttachmentConfigPayload read(FriendlyByteBuf buf) {
        return new AttachmentConfigPayload(buf.readBlockPos(), buf.readByte(), buf.readByte(), buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AttachmentConfigPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (player.level().getBlockEntity(payload.pos) instanceof DuctBlockEntity ductBE) {
                Attachment att = ductBE.getAttachment(Direction.values()[payload.side]);
                if (att instanceof ConnectionBase conn) {
                    switch (payload.action) {
                        case ACTION_TOGGLE_WHITELIST -> conn.getFilter().setWhitelist(!conn.getFilter().isWhitelist());
                        case ACTION_TOGGLE_MATCH_COMPONENTS -> conn.getFilter().setMatchComponents(!conn.getFilter().isMatchComponents());
                        case ACTION_TOGGLE_MATCH_MOD -> conn.getFilter().setMatchModId(!conn.getFilter().isMatchModId());
                        case ACTION_SET_REDSTONE_MODE -> conn.setRedstoneMode(RedstoneMode.values()[payload.value]);
                        case ACTION_SET_MAX_STOCK -> conn.getFilter().setMaxStock(payload.value);
                        case ACTION_SET_ROUTE_TYPE -> conn.getFilter().setRouteType(payload.value);
                    }
                    ductBE.setChanged();
                    player.level().sendBlockUpdated(payload.pos, ductBE.getBlockState(), ductBE.getBlockState(), 3);
                }
            }
        });
    }
}
