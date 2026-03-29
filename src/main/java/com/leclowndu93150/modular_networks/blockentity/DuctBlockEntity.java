package com.leclowndu93150.modular_networks.blockentity;

import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentRegistry;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import com.leclowndu93150.modular_networks.core.network.ConnectionType;
import com.leclowndu93150.modular_networks.core.network.NetworkManager;
import com.leclowndu93150.modular_networks.duct.fluid.FluidDuctUnit;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.Map;

public abstract class DuctBlockEntity extends BlockEntity {

    protected final Map<DuctToken, DuctUnit<?, ?, ?>> ductUnits = new EnumMap<>(DuctToken.class);
    protected final ConnectionType[] connectionTypes = new ConnectionType[6];
    protected final Attachment[] attachments = new Attachment[6];

    protected DuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (int i = 0; i < 6; i++) {
            connectionTypes[i] = ConnectionType.NORMAL;
        }
    }

    protected abstract void initDuctUnits();

    public static void serverTick(Level level, BlockPos pos, BlockState state, DuctBlockEntity be) {
        for (Attachment att : be.attachments) {
            if (att != null) {
                att.tick();
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, DuctBlockEntity be) {
        for (DuctUnit<?, ?, ?> unit : be.ductUnits.values()) {
            if (unit instanceof ItemDuctUnit itemUnit) {
                itemUnit.tickClientTravelingItems();
            }
        }
    }

    protected void addDuctUnit(DuctUnit<?, ?, ?> unit) {
        ductUnits.put(unit.getToken(), unit);
    }

    public DuctUnit<?, ?, ?> getDuctUnit(DuctToken token) {
        return ductUnits.get(token);
    }

    public Map<DuctToken, DuctUnit<?, ?, ?>> getDuctUnits() {
        return ductUnits;
    }

    public ConnectionType getConnectionType(Direction side) {
        return connectionTypes[side.ordinal()];
    }

    public void setConnectionType(Direction side, ConnectionType type) {
        connectionTypes[side.ordinal()] = type;
        onNeighborChanged();
        setChanged();
        if (level != null && !level.isClientSide) {
            refreshVisualState();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    public Attachment getAttachment(Direction side) {
        return attachments[side.ordinal()];
    }

    public void setAttachment(Direction side, Attachment attachment) {
        attachments[side.ordinal()] = attachment;
        setChanged();
        if (level != null && !level.isClientSide) {
            onNeighborChanged();
            refreshVisualState();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void removeAttachment(Direction side) {
        attachments[side.ordinal()] = null;
        setChanged();
        if (level != null && !level.isClientSide) {
            onNeighborChanged();
            refreshVisualState();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
            unit.updateCaches();
        }
        if (level instanceof ServerLevel serverLevel) {
            for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
                NetworkManager.get(serverLevel).scheduleFormation(unit);
            }
        }
    }

    public void onPlaced() {
        if (level instanceof ServerLevel serverLevel) {
            for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
                unit.updateCaches();
                NetworkManager.get(serverLevel).scheduleFormation(unit);
            }
        }
    }

    public void onBroken() {
        for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
            if (unit instanceof ItemDuctUnit itemUnit) {
                itemUnit.dropAllItems();
            }
            if (unit instanceof FluidDuctUnit fluidUnit) {
                fluidUnit.markBeingDestroyed();
            }
            unit.invalidate();
        }
    }

    public void onNeighborChanged() {
        if (level instanceof ServerLevel serverLevel) {
            boolean connectionTypesChanged = normalizeDisconnectedBlockedSides();
            for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
                unit.invalidate();
                unit.updateCaches();
                NetworkManager.get(serverLevel).scheduleFormation(unit);
            }
            boolean powered = level.hasNeighborSignal(worldPosition);
            for (Attachment att : attachments) {
                if (att instanceof ConnectionBase conn) {
                    conn.updatePowerState(powered);
                }
            }
            level.invalidateCapabilities(worldPosition);
            if (connectionTypesChanged) {
                setChanged();
                refreshVisualState();
            }
        }
    }

    private boolean normalizeDisconnectedBlockedSides() {
        if (level == null) {
            return false;
        }

        boolean changed = false;
        for (Direction side : Direction.values()) {
            if (connectionTypes[side.ordinal()] != ConnectionType.BLOCKED) {
                continue;
            }

            BlockPos neighborPos = worldPosition.relative(side);
            if (!level.isLoaded(neighborPos)) {
                continue;
            }

            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) {
                connectionTypes[side.ordinal()] = ConnectionType.NORMAL;
                changed = true;
            }
        }
        return changed;
    }

    private void refreshVisualState() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof DuctBlock ductBlock)) {
            return;
        }
        BlockState updatedState = ductBlock.updateVisualConnections(level, worldPosition, state);
        if (updatedState != state) {
            level.setBlock(worldPosition, updatedState, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void setRemoved() {
        for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
            unit.invalidate();
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
            unit.invalidate();
        }
        super.onChunkUnloaded();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        byte[] connections = new byte[6];
        for (int i = 0; i < 6; i++) {
            connections[i] = (byte) connectionTypes[i].ordinal();
        }
        tag.putByteArray("Connections", connections);

        for (var entry : ductUnits.entrySet()) {
            CompoundTag unitTag = new CompoundTag();
            entry.getValue().saveAdditional(unitTag, registries);
            if (!unitTag.isEmpty()) {
                tag.put("Unit_" + entry.getKey().name(), unitTag);
            }
        }

        ListTag attachmentList = new ListTag();
        for (int i = 0; i < 6; i++) {
            if (attachments[i] != null) {
                attachmentList.add(attachments[i].save(registries));
            } else {
                attachmentList.add(new CompoundTag());
            }
        }
        tag.put("Attachments", attachmentList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("Connections")) {
            byte[] connections = tag.getByteArray("Connections");
            for (int i = 0; i < Math.min(6, connections.length); i++) {
                connectionTypes[i] = ConnectionType.values()[connections[i]];
            }
        }

        for (var entry : ductUnits.entrySet()) {
            String key = "Unit_" + entry.getKey().name();
            if (tag.contains(key)) {
                entry.getValue().loadAdditional(tag.getCompound(key), registries);
            }
        }

        if (tag.contains("Attachments")) {
            ListTag attachmentList = tag.getList("Attachments", Tag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(6, attachmentList.size()); i++) {
                CompoundTag attTag = attachmentList.getCompound(i);
                if (attTag.isEmpty() || !attTag.contains("Id")) {
                    attachments[i] = null;
                } else {
                    ResourceLocation id = ResourceLocation.parse(attTag.getString("Id"));
                    attachments[i] = AttachmentRegistry.create(id, this, Direction.values()[i], attTag);
                    if (attachments[i] != null) {
                        attachments[i].load(attTag, registries);
                    }
                }
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (level != null && level.isClientSide) {
            for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
                unit.updateCaches();
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        super.onDataPacket(net, pkt, registries);
        if (level != null && level.isClientSide) {
            for (DuctUnit<?, ?, ?> unit : ductUnits.values()) {
                unit.updateCaches();
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
