package com.leclowndu93150.modular_networks.menu;

import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.StructuralDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.init.MNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class RelayMenu extends AbstractContainerMenu {

    private final Relay relay;
    private final ContainerData data;

    public RelayMenu(int containerId, Inventory playerInv, Relay relay) {
        super(MNMenuTypes.RELAY_MENU.get(), containerId);
        this.relay = relay;

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> relay.getColor();
                    case 1 -> relay.getType();
                    case 2 -> relay.getOutputStrength();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> relay.setColor(value);
                    case 1 -> relay.setType(value);
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        addDataSlots(this.data);
    }

    public static RelayMenu fromNetwork(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int sideOrdinal = buf.readByte();
        Direction side = Direction.values()[sideOrdinal];

        if (playerInv.player.level().getBlockEntity(pos) instanceof StructuralDuctBlockEntity ductBE) {
            Attachment att = ductBE.getAttachment(side);
            if (att instanceof Relay relay) {
                return new RelayMenu(containerId, playerInv, relay);
            }
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public Relay getRelay() {
        return relay;
    }

    public int getRelayColor() {
        return data.get(0);
    }

    public int getRelayType() {
        return data.get(1);
    }

    public int getRelayPower() {
        return data.get(2);
    }
}
