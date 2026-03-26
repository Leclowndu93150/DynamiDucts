package com.leclowndu93150.modular_networks.duct.item;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.duct.DuctUnit;
import com.leclowndu93150.modular_networks.core.network.Route;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDuctUnit extends DuctUnit<ItemDuctUnit, ItemGrid, IItemHandler> {

    private final int speed;
    private final boolean transparent;
    private final int pathWeightValue;
    private final List<TravelingItem> myItems = new ArrayList<>();
    private final List<TravelingItem> itemsToAdd = new ArrayList<>();
    private final List<TravelingItemSnapshot> clientTravelingItems = new ArrayList<>();

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

    public boolean insertItem(ItemStack stack, Direction entrySide) {
        if (grid == null || stack.isEmpty()) return false;
        return grid.insertNewItem(stack, this, entrySide, speed);
    }

    public void insertItemWithRoute(ItemStack stack, Direction entrySide, Route route, int speedBoost) {
        TravelingItem item = new TravelingItem(stack, route, getPos(), entrySide, Math.max(1, speed / speedBoost));
        addTravelingItem(item);
    }

    public void addTravelingItem(TravelingItem item) {
        itemsToAdd.add(item);
    }

    public void transferItem(TravelingItem item) {
        item.currentPos = getPos();
        itemsToAdd.add(item);
    }

    public List<TravelingItem> getMyItems() {
        return myItems;
    }

    public List<TravelingItem> getItemsToAdd() {
        return itemsToAdd;
    }

    public void flushItemsToAdd() {
        if (!itemsToAdd.isEmpty()) {
            myItems.addAll(itemsToAdd);
            itemsToAdd.clear();
        }
    }

    public void dropAllItems() {
        if (parent.getLevel() == null || parent.getLevel().isClientSide) return;
        for (TravelingItem item : myItems) {
            dropStack(item.stack);
        }
        myItems.clear();
        for (TravelingItem item : itemsToAdd) {
            dropStack(item.stack);
        }
        itemsToAdd.clear();
    }

    private void dropStack(ItemStack stack) {
        if (!stack.isEmpty() && parent.getLevel() instanceof ServerLevel serverLevel) {
            ItemEntity entity = new ItemEntity(
                    serverLevel,
                    parent.getBlockPos().getX() + 0.5,
                    parent.getBlockPos().getY() + 0.5,
                    parent.getBlockPos().getZ() + 0.5,
                    stack);
            serverLevel.addFreshEntity(entity);
        }
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
                if (ItemDuctUnit.this.insertItem(stack.copy(), side)) {
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

    public List<TravelingItemSnapshot> getClientTravelingItems() {
        return clientTravelingItems;
    }

    public void setClientTravelingItems(List<TravelingItemSnapshot> items) {
        clientTravelingItems.clear();
        clientTravelingItems.addAll(items);
    }

    public void setServerTravelingItems(List<TravelingItemSnapshot> items) {
        if (parent.getLevel() != null && !parent.getLevel().isClientSide) {
            clientTravelingItems.clear();
            clientTravelingItems.addAll(items);
        }
    }

    public void tickClientTravelingItems() {
        if (parent.getLevel() == null || !parent.getLevel().isClientSide || clientTravelingItems.isEmpty()) {
            return;
        }

        Iterator<TravelingItemSnapshot> iterator = clientTravelingItems.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().tickClient()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        List<TravelingItem> allItems = new ArrayList<>(myItems);
        allItems.addAll(itemsToAdd);

        if (allItems.isEmpty() && clientTravelingItems.isEmpty()) {
            return;
        }

        ListTag list = new ListTag();
        if (!allItems.isEmpty()) {
            for (TravelingItem item : allItems) {
                list.add(TravelingItemSnapshot.fromTravelingItem(item).save(provider));
            }
        } else {
            for (TravelingItemSnapshot item : clientTravelingItems) {
                list.add(item.save(provider));
            }
        }
        if (!list.isEmpty()) {
            tag.put("TravelingItems", list);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        clientTravelingItems.clear();
        if (!tag.contains("TravelingItems", Tag.TAG_LIST)) {
            return;
        }

        ListTag list = tag.getList("TravelingItems", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            TravelingItemSnapshot item = TravelingItemSnapshot.load(list.getCompound(i), provider);
            if (item != null) {
                clientTravelingItems.add(item);
            }
        }
    }
}
