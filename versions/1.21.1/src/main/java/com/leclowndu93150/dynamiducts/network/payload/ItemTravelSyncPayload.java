package com.leclowndu93150.dynamiducts.network.payload;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.TravelingItemSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ItemTravelSyncPayload(BlockPos pos, List<TravelingItemSnapshot> items) implements CustomPacketPayload {

    public static final Type<ItemTravelSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "item_travel_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTravelSyncPayload> STREAM_CODEC = StreamCodec.of(
            ItemTravelSyncPayload::write,
            ItemTravelSyncPayload::read
    );

    private static void write(RegistryFriendlyByteBuf buf, ItemTravelSyncPayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeVarInt(payload.items.size());
        for (TravelingItemSnapshot item : payload.items) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, item.getStack());
            buf.writeByte(item.getDirection().get3DDataValue());
            buf.writeByte(item.getOldDirection().get3DDataValue());
            buf.writeVarInt(item.getRawProgress());
            buf.writeVarInt(item.getSpeed());
        }
    }

    private static ItemTravelSyncPayload read(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readVarInt();
        List<TravelingItemSnapshot> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            Direction direction = Direction.from3DDataValue(buf.readByte());
            Direction oldDirection = Direction.from3DDataValue(buf.readByte());
            int progress = buf.readVarInt();
            int speed = buf.readVarInt();
            if (!stack.isEmpty()) {
                items.add(new TravelingItemSnapshot(stack, direction, oldDirection, progress, speed));
            }
        }
        return new ItemTravelSyncPayload(pos, items);
    }

    public static void handle(ItemTravelSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().level().getBlockEntity(payload.pos) instanceof DuctBlockEntity ductBE) {
                if (ductBE.getDuctUnit(DuctToken.ITEM) instanceof ItemDuctUnit itemUnit) {
                    itemUnit.setClientTravelingItems(payload.items);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
