package com.leclowndu93150.modular_networks.duct.transport;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import com.leclowndu93150.modular_networks.core.network.ConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TransportDuctUnit extends DuctUnit<TransportDuctUnit, TransportGrid, Void> {

    private final boolean longRange;
    private String endpointName = "";
    private ItemStack endpointIcon = ItemStack.EMPTY;
    private byte endpointSide = -1;

    public TransportDuctUnit(DuctBlockEntity parent, boolean longRange) {
        super(parent);
        this.longRange = longRange;
    }

    @Override
    protected Void[] createTileCacheArray() {
        return new Void[6];
    }

    @Override
    protected TransportDuctUnit[] createDuctCacheArray() {
        return new TransportDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.TRANSPORT;
    }

    @Override
    public TransportGrid createGrid(ServerLevel level) {
        return new TransportGrid(level);
    }

    @Override
    public int getPathWeight() {
        return longRange ? 0 : 1;
    }

    public boolean isLongRange() {
        return longRange;
    }

    public boolean isEndpoint() {
        return endpointSide >= 0;
    }

    public byte getEndpointSide() {
        return endpointSide;
    }

    public String getEndpointName() {
        return endpointName.isEmpty() ? "Unnamed" : endpointName;
    }

    public String getRawEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String name) {
        String updatedName = name != null ? name : "";
        if (endpointName.equals(updatedName)) {
            return;
        }
        endpointName = updatedName;
        parent.setChanged();
    }

    public ItemStack getEndpointIcon() {
        return endpointIcon;
    }

    public void setEndpointIcon(ItemStack stack) {
        ItemStack updatedIcon = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        if (ItemStack.isSameItemSameComponents(endpointIcon, updatedIcon)) {
            return;
        }
        endpointIcon = updatedIcon;
        parent.setChanged();
    }

    public boolean trySetEndpoint(Direction side) {
        if (getDuctNeighbor(side) != null) return false;

        if (endpointSide == side.ordinal()) {
            endpointSide = -1;
            parent.setConnectionType(side, ConnectionType.NORMAL);
        } else {
            if (endpointSide >= 0) {
                parent.setConnectionType(Direction.values()[endpointSide], ConnectionType.NORMAL);
            }
            endpointSide = (byte) side.ordinal();
            parent.setConnectionType(side, ConnectionType.FORCED);
        }
        parent.setChanged();
        if (grid instanceof TransportGrid tg) {
            tg.onMajorGridChange();
        }
        return true;
    }

    public List<TransportRoute> getAvailableDestinations() {
        if (!(grid instanceof TransportGrid tg)) return List.of();
        return tg.getRoutesFrom(this);
    }

    public TransportRoute getRouteTo(BlockPos dest) {
        for (TransportRoute route : getAvailableDestinations()) {
            if (route.destination.equals(dest)) return route;
        }
        return null;
    }

    public boolean sendPlayerToDest(Player player, BlockPos dest) {
        TransportRoute route = getRouteTo(dest);
        if (route == null) return false;

        if (!(parent.getLevel() instanceof ServerLevel serverLevel)) return false;

        TransportEntity entity = new TransportEntity(serverLevel, this, route);
        entity.start(player);
        return true;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putByte("EndpointSide", endpointSide);
        if (!endpointName.isEmpty()) {
            tag.putString("EndpointName", endpointName);
        }
        if (!endpointIcon.isEmpty()) {
            tag.put("EndpointIcon", endpointIcon.saveOptional(provider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        endpointSide = tag.contains("EndpointSide") ? tag.getByte("EndpointSide") : -1;
        endpointName = tag.getString("EndpointName");
        endpointIcon = tag.contains("EndpointIcon", CompoundTag.TAG_COMPOUND)
                ? ItemStack.parseOptional(provider, tag.getCompound("EndpointIcon"))
                : ItemStack.EMPTY;
    }

    @Override
    public boolean isNode() {
        return isEndpoint() || super.isNode();
    }
}
