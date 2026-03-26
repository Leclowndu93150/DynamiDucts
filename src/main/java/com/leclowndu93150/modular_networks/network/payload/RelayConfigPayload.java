package com.leclowndu93150.modular_networks.network.payload;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RelayConfigPayload(
        BlockPos pos,
        int side,
        int action,
        int value
) implements CustomPacketPayload {

    public static final int ACTION_SET_TYPE = 0;
    public static final int ACTION_SET_INVERT = 1;
    public static final int ACTION_SET_COLOR = 2;
    public static final int ACTION_SET_THRESHOLD = 3;

    public static final Type<RelayConfigPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "relay_config"));

    public static final StreamCodec<FriendlyByteBuf, RelayConfigPayload> STREAM_CODEC = StreamCodec.of(
            RelayConfigPayload::write,
            RelayConfigPayload::read
    );

    private static void write(FriendlyByteBuf buf, RelayConfigPayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeByte(payload.side);
        buf.writeByte(payload.action);
        buf.writeVarInt(payload.value);
    }

    private static RelayConfigPayload read(FriendlyByteBuf buf) {
        return new RelayConfigPayload(buf.readBlockPos(), buf.readByte(), buf.readByte(), buf.readVarInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RelayConfigPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (!(player.level().getBlockEntity(payload.pos) instanceof DuctBlockEntity ductBE)) {
                return;
            }

            Attachment attachment = ductBE.getAttachment(Direction.values()[payload.side]);
            if (!(attachment instanceof Relay relay)) {
                return;
            }

            switch (payload.action) {
                case ACTION_SET_TYPE -> relay.setType(payload.value);
                case ACTION_SET_INVERT -> relay.setInvertMode(payload.value);
                case ACTION_SET_COLOR -> relay.setColor(payload.value);
                case ACTION_SET_THRESHOLD -> relay.setThreshold(payload.value);
                default -> {
                    return;
                }
            }

            ductBE.setChanged();
            player.level().sendBlockUpdated(payload.pos, ductBE.getBlockState(), ductBE.getBlockState(), 3);
        });
    }
}
