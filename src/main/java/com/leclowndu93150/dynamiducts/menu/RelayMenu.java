package com.leclowndu93150.dynamiducts.menu;

import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.blockentity.StructuralDuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.structural.StructuralDuctUnit;
import com.leclowndu93150.dynamiducts.init.DDMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class RelayMenu extends AbstractContainerMenu {

    private final Relay relay;
    private final ContainerData data;
    private final int[] values = new int[6];

    public RelayMenu(int containerId, Inventory playerInv, Relay relay) {
        super(DDMenuTypes.RELAY_MENU.get(), containerId);
        this.relay = relay;
        syncValues();

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return index >= 0 && index < values.length ? values[index] : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index < 0 || index >= values.length) {
                    return;
                }
                values[index] = value;
                switch (index) {
                    case 0 -> relay.setColor(value);
                    case 1 -> relay.setType(value);
                    case 3 -> relay.setInvertMode(value);
                    case 4 -> relay.setThreshold(value);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
        addDataSlots(this.data);
    }

    @Override
    public void broadcastChanges() {
        syncValues();
        super.broadcastChanges();
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

    public int getInvertMode() {
        return data.get(3);
    }

    public int getThreshold() {
        return data.get(4);
    }

    public int getGridPower() {
        return data.get(5);
    }

    private void syncValues() {
        values[0] = relay.getColor();
        values[1] = relay.getType();
        values[2] = relay.isInput() ? relay.readInputSignal() : relay.getOutputStrength();
        values[3] = relay.getInvertMode();
        values[4] = relay.getThreshold();
        values[5] = getGridPower(relay);
    }

    private static int getGridPower(Relay relay) {
        var unit = relay.getParent().getDuctUnit(DuctToken.STRUCTURAL);
        if (unit instanceof StructuralDuctUnit structuralUnit && structuralUnit.getGrid() != null) {
            return structuralUnit.getGrid().getSignal(relay.getColor());
        }
        return 0;
    }
}
