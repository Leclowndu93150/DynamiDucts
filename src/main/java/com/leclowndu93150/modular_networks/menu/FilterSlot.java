package com.leclowndu93150.modular_networks.menu;

import com.leclowndu93150.modular_networks.core.attachment.FilterLogic;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends Slot {

    private static final Container DUMMY = new SimpleContainer(0);

    private final FilterLogic filter;
    private final int filterIndex;

    public FilterSlot(FilterLogic filter, int filterIndex, int x, int y) {
        super(DUMMY, filterIndex, x, y);
        this.filter = filter;
        this.filterIndex = filterIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getItem() {
        return filter.getFilterStack(filterIndex);
    }

    @Override
    public void set(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack = stack.copyWithCount(1);
        }
        filter.setFilterStack(filterIndex, stack);
        setChanged();
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
