package com.leclowndu93150.modular_networks.duct.item;

import com.leclowndu93150.modular_networks.MNConfig;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemDuctUnit extends DuctUnit<ItemDuctUnit, ItemGrid, IItemHandler> {

    private final int speed;
    private final boolean transparent;
    private final int pathWeightValue;

    public ItemDuctUnit(DuctBlockEntity parent, int speed, boolean transparent) {
        this(parent, speed, transparent, 1);
    }

    public ItemDuctUnit(DuctBlockEntity parent, int speed, boolean transparent, int pathWeight) {
        super(parent);
        this.speed = speed;
        this.transparent = transparent;
        this.pathWeightValue = pathWeight;
    }

    @Override
    protected IItemHandler[] createTileCacheArray() {
        return new IItemHandler[6];
    }

    @Override
    protected ItemDuctUnit[] createDuctCacheArray() {
        return new ItemDuctUnit[6];
    }

    @Override
    public DuctToken getToken() {
        return DuctToken.ITEM;
    }

    @Override
    public ItemGrid createGrid(ServerLevel level) {
        return new ItemGrid(level);
    }

    @Override
    public int getPathWeight() {
        return pathWeightValue;
    }

    @Override
    public IItemHandler cacheTile(Direction side) {
        if (parent.getLevel() == null) return null;
        return parent.getLevel().getCapability(
                Capabilities.ItemHandler.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
    }

    public boolean insertItem(ItemStack stack) {
        if (grid == null || stack.isEmpty()) return false;
        return grid.insertNewItem(stack, this, speed);
    }

    public IItemHandler createCapability(Direction side) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (grid == null || stack.isEmpty()) return stack;
                if (simulate) return ItemStack.EMPTY;
                if (ItemDuctUnit.this.insertItem(stack.copy())) {
                    return ItemStack.EMPTY;
                }
                return stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
            }
        };
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isTransparent() {
        return transparent;
    }
}
