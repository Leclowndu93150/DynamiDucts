package com.leclowndu93150.modular_networks.network.payload;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.transport.TransportDuctUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TransportRenamePayload(BlockPos pos, String name) implements CustomPacketPayload {

    public static final Type<TransportRenamePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "transport_rename"));

    public static final StreamCodec<FriendlyByteBuf, TransportRenamePayload> STREAM_CODEC = StreamCodec.of(
            TransportRenamePayload::write,
            TransportRenamePayload::read
    );

    private static void write(FriendlyByteBuf buf, TransportRenamePayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeUtf(payload.name);
    }

    private static TransportRenamePayload read(FriendlyByteBuf buf) {
        return new TransportRenamePayload(buf.readBlockPos(), buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TransportRenamePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().level().getBlockEntity(payload.pos) instanceof DuctBlockEntity ductBE
                    && ductBE.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit transportUnit
                    && transportUnit.isEndpoint()) {
                transportUnit.setEndpointName(payload.name);
            }
        });
    }
}
