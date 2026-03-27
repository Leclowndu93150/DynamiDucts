package com.leclowndu93150.modular_networks.menu;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.transport.TransportDuctUnit;
import com.leclowndu93150.modular_networks.init.MNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransportConfigMenu extends AbstractContainerMenu {

    private static final int PLAYER_SLOT_COUNT = 36;
    private static final int ICON_SLOT_INDEX = PLAYER_SLOT_COUNT;

    private final BlockPos ductPos;
    private final DuctBlockEntity blockEntity;
    private final TransportDuctUnit transportUnit;

    public TransportConfigMenu(int containerId, Inventory playerInv, DuctBlockEntity blockEntity, TransportDuctUnit transportUnit) {
        super(MNMenuTypes.TRANSPORT_CONFIG_MENU.get(), containerId);
        this.ductPos = blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
        this.blockEntity = blockEntity;
        this.transportUnit = transportUnit;

        addPlayerInventory(playerInv);
        addSlot(new TransportIconSlot(transportUnit, 8, 15));
    }

    public static TransportConfigMenu fromNetwork(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        if (playerInv.player.level().getBlockEntity(pos) instanceof DuctBlockEntity ductBE
                && ductBE.getDuctUnit(DuctToken.TRANSPORT) instanceof TransportDuctUnit transportUnit) {
            return new TransportConfigMenu(containerId, playerInv, ductBE, transportUnit);
        }
        return null;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 53 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 111));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == ICON_SLOT_INDEX) {
            ItemStack carried = getCarried();
            transportUnit.setEndpointIcon(carried.isEmpty() ? ItemStack.EMPTY : carried.copyWithCount(1));
            broadcastChanges();
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index >= 0 && index < PLAYER_SLOT_COUNT) {
            ItemStack stack = slots.get(index).getItem();
            if (!stack.isEmpty()) {
                transportUnit.setEndpointIcon(stack.copyWithCount(1));
                broadcastChanges();
            }
        } else if (index == ICON_SLOT_INDEX) {
            transportUnit.setEndpointIcon(ItemStack.EMPTY);
            broadcastChanges();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && !blockEntity.isRemoved()
                && player.distanceToSqr(ductPos.getX() + 0.5, ductPos.getY() + 0.5, ductPos.getZ() + 0.5) <= 64.0;
    }

    public BlockPos getDuctPos() {
        return ductPos;
    }

    public String getEndpointName() {
        return transportUnit.getRawEndpointName();
    }
}
