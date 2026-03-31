package com.leclowndu93150.dynamiducts.menu;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDirectoryEntry;
import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import com.leclowndu93150.dynamiducts.duct.transport.TransportGrid;
import com.leclowndu93150.dynamiducts.duct.transport.TransportRoute;
import com.leclowndu93150.dynamiducts.init.DDMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransportMenu extends AbstractContainerMenu {

    private final BlockPos ductPos;
    private final DuctBlockEntity blockEntity;
    private final TransportDuctUnit transportUnit;
    private TransportDirectoryEntry currentEntry;
    private List<TransportDirectoryEntry> destinations = new ArrayList<>();

    public TransportMenu(int containerId, Inventory playerInv, DuctBlockEntity blockEntity, TransportDuctUnit unit) {
        super(DDMenuTypes.TRANSPORT_MENU.get(), containerId);
        this.ductPos = blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
        this.blockEntity = blockEntity;
        this.transportUnit = unit;
        if (transportUnit != null) {
            currentEntry = createEntry(transportUnit);
            refreshDestinations();
        }
    }

    public static TransportMenu fromNetwork(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        TransportDirectoryEntry currentEntry = readEntry(buf);
        int destCount = buf.readVarInt();
        List<TransportDirectoryEntry> dests = new ArrayList<>();
        for (int i = 0; i < destCount; i++) {
            dests.add(readEntry(buf));
        }

        DuctBlockEntity ductBE = null;
        TransportDuctUnit unit = null;
        if (playerInv.player.level().getBlockEntity(pos) instanceof DuctBlockEntity be) {
            ductBE = be;
            if (be.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit tdu) {
                unit = tdu;
            }
        }

        TransportMenu menu = new TransportMenu(containerId, playerInv, ductBE, unit);
        menu.currentEntry = currentEntry;
        menu.destinations = dests;
        return menu;
    }

    public void refreshDestinations() {
        destinations.clear();
        if (transportUnit == null) return;
        for (TransportRoute route : transportUnit.getAvailableDestinations()) {
            if (route.destination.equals(ductPos)) continue;
            if (transportUnit.getGrid() instanceof TransportGrid tg) {
                for (TransportDuctUnit endpoint : tg.getEndpoints()) {
                    if (endpoint.getPos().equals(route.destination)) {
                        destinations.add(createEntry(endpoint));
                        break;
                    }
                }
            }
        }
        destinations.sort(Comparator.comparing(TransportDirectoryEntry::name));
    }

    public List<TransportDirectoryEntry> getDestinations() {
        return destinations;
    }

    public TransportDirectoryEntry getCurrentEntry() {
        return currentEntry;
    }

    public BlockPos getDuctPos() {
        return ductPos;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != 0 || !(player instanceof ServerPlayer serverPlayer) || blockEntity == null || transportUnit == null || !transportUnit.isEndpoint()) {
            return false;
        }

        serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new TransportConfigMenu(id, inv, blockEntity, transportUnit),
                Component.translatable("gui.dynamiducts.transport.config")
        ), buf -> buf.writeBlockPos(ductPos));
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && !blockEntity.isRemoved() &&
                player.distanceToSqr(ductPos.getX() + 0.5, ductPos.getY() + 0.5, ductPos.getZ() + 0.5) <= 64.0;
    }

    public static void writeScreenData(FriendlyByteBuf buf, TransportDuctUnit unit, BlockPos origin) {
        List<TransportDirectoryEntry> entries = buildDirectory(unit, origin);
        buf.writeBlockPos(origin);
        writeEntry(buf, createEntry(unit));
        buf.writeVarInt(entries.size());
        for (TransportDirectoryEntry entry : entries) {
            writeEntry(buf, entry);
        }
    }

    private static List<TransportDirectoryEntry> buildDirectory(TransportDuctUnit unit, BlockPos origin) {
        List<TransportDirectoryEntry> entries = new ArrayList<>();
        for (TransportRoute route : unit.getAvailableDestinations()) {
            if (route.destination.equals(origin)) {
                continue;
            }
            if (unit.getGrid() instanceof TransportGrid tg) {
                for (TransportDuctUnit endpoint : tg.getEndpoints()) {
                    if (endpoint.getPos().equals(route.destination)) {
                        entries.add(createEntry(endpoint));
                        break;
                    }
                }
            }
        }
        entries.sort(Comparator.comparing(TransportDirectoryEntry::name));
        return entries;
    }

    private static TransportDirectoryEntry createEntry(TransportDuctUnit unit) {
        return new TransportDirectoryEntry(unit.getPos(), unit.getRawEndpointName(), unit.getEndpointIcon().copy());
    }

    private static void writeEntry(FriendlyByteBuf buf, TransportDirectoryEntry entry) {
        buf.writeBlockPos(entry.pos());
        buf.writeUtf(entry.name());
        buf.writeBoolean(!entry.icon().isEmpty());
        if (!entry.icon().isEmpty()) {
            buf.writeUtf(BuiltInRegistries.ITEM.getKey(entry.icon().getItem()).toString());
        }
    }

    private static TransportDirectoryEntry readEntry(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        String name = buf.readUtf();
        ItemStack icon = ItemStack.EMPTY;
        if (buf.readBoolean()) {
            ResourceLocation itemId = ResourceLocation.parse(buf.readUtf());
            icon = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
        }
        return new TransportDirectoryEntry(pos, name, icon);
    }
}
