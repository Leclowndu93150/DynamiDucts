package com.leclowndu93150.modular_networks.network.payload;

import com.leclowndu93150.modular_networks.ModularNetworks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DuctSyncPayload(BlockPos pos, byte[] connectionData) implements CustomPacketPayload {

    public static final Type<DuctSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "duct_sync"));

    public static final StreamCodec<FriendlyByteBuf, DuctSyncPayload> STREAM_CODEC = StreamCodec.of(
            DuctSyncPayload::write,
            DuctSyncPayload::read
    );

    private static void write(FriendlyByteBuf buf, DuctSyncPayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeByteArray(payload.connectionData);
    }

    private static DuctSyncPayload read(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        byte[] data = buf.readByteArray();
        return new DuctSyncPayload(pos, data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
