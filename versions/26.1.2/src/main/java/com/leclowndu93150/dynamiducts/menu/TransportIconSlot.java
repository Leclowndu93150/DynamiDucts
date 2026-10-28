package com.leclowndu93150.dynamiducts.menu;

import com.leclowndu93150.dynamiducts.duct.transport.TransportDuctUnit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransportIconSlot extends Slot {

    private static final Container DUMMY = new SimpleContainer(0);

    private final TransportDuctUnit transportUnit;

    public TransportIconSlot(TransportDuctUnit transportUnit, int x, int y) {
        super(DUMMY, 0, x, y);
        this.transportUnit = transportUnit;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getItem() {
        return transportUnit.getEndpointIcon();
    }

    @Override
    public void set(ItemStack stack) {
        transportUnit.setEndpointIcon(stack);
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
