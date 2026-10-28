package com.leclowndu93150.dynamiducts.duct.item;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.duct.DuctUnit;
import com.leclowndu93150.dynamiducts.core.network.Route;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDuctUnit extends DuctUnit<ItemDuctUnit, ItemGrid, ResourceHandler<ItemResource>> {

    private final int speed;
    private final boolean transparent;
    private final int pathWeightValue;
    private final List<TravelingItem> myItems = new ArrayList<>();
    private final List<TravelingItem> itemsToAdd = new ArrayList<>();
    private final List<TravelingItemSnapshot> clientTravelingItems = new ArrayList<>();
    private final InsertJournal insertJournal = new InsertJournal();

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
    @SuppressWarnings("unchecked")
    protected ResourceHandler<ItemResource>[] createTileCacheArray() {
        return new ResourceHandler[6];
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
    protected BlockCapability<ResourceHandler<ItemResource>, Direction> getTileCapability() {
        return Capabilities.Item.BLOCK;
    }

    public boolean insertItem(ItemStack stack, Direction entrySide, TransactionContext transaction) {
        if (grid == null || stack.isEmpty()) return false;
        return grid.insertNewItem(stack, this, entrySide, speed, transaction);
    }

    public void insertItemWithRoute(ItemStack stack, Direction entrySide, Route route, int speedBoost) {
        TravelingItem item = new TravelingItem(stack, route, getPos(), entrySide, Math.max(1, speed / speedBoost));
        addTravelingItem(item);
        if (grid != null) {
            grid.syncTravelersAt(this);
        }
    }

    public void addTravelingItem(TravelingItem item) {
        itemsToAdd.add(item);
    }

    public void addTravelingItem(TravelingItem item, TransactionContext transaction) {
        insertJournal.updateSnapshots(transaction);
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
        if (parent.getLevel() == null || parent.getLevel().isClientSide()) return;
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

    public ResourceHandler<ItemResource> createCapability(Direction side) {
        return new ResourceHandler<>() {
            @Override
            public int size() {
                return 1;
            }

            @Override
            public ItemResource getResource(int index) {
                return ItemResource.EMPTY;
            }

            @Override
            public long getAmountAsLong(int index) {
                return 0;
            }

            @Override
            public long getCapacityAsLong(int index, ItemResource resource) {
                return 64;
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return true;
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
                if (grid == null || amount == 0) return 0;
                ItemStack remaining = resource.toStack(amount);
                ItemDuctUnit.this.insertItem(remaining, side, transaction);
                return amount - remaining.getCount();
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
                TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
                return 0;
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
        if (parent.getLevel() != null && !parent.getLevel().isClientSide()) {
            clientTravelingItems.clear();
            clientTravelingItems.addAll(items);
        }
    }

    public void tickClientTravelingItems() {
        if (parent.getLevel() == null || !parent.getLevel().isClientSide() || clientTravelingItems.isEmpty()) {
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
    public void saveAdditional(ValueOutput output) {
        List<TravelingItem> allItems = new ArrayList<>(myItems);
        allItems.addAll(itemsToAdd);

        if (allItems.isEmpty() && clientTravelingItems.isEmpty()) {
            return;
        }

        ValueOutput.ValueOutputList list = output.childrenList("TravelingItems");
        if (!allItems.isEmpty()) {
            for (TravelingItem item : allItems) {
                item.save(list.addChild());
            }
        } else {
            for (TravelingItemSnapshot item : clientTravelingItems) {
                item.save(list.addChild());
            }
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        clientTravelingItems.clear();
        boolean restoreTravelers = parent.getLevel() == null || !parent.getLevel().isClientSide();
        if (restoreTravelers) {
            myItems.clear();
            itemsToAdd.clear();
        }
        for (ValueInput itemInput : input.childrenListOrEmpty("TravelingItems")) {
            TravelingItemSnapshot item = TravelingItemSnapshot.load(itemInput);
            if (item != null) {
                clientTravelingItems.add(item);
            }
            if (restoreTravelers) {
                TravelingItem traveler = TravelingItem.load(itemInput, getPos());
                if (traveler != null) {
                    myItems.add(traveler);
                }
            }
        }
    }

    private class InsertJournal extends SnapshotJournal<List<TravelingItem>> {
        @Override
        protected List<TravelingItem> createSnapshot() {
            return new ArrayList<>(itemsToAdd);
        }

        @Override
        protected void revertToSnapshot(List<TravelingItem> snapshot) {
            itemsToAdd.clear();
            itemsToAdd.addAll(snapshot);
        }

        @Override
        protected void onRootCommit(List<TravelingItem> originalState) {
            if (grid != null) {
                grid.syncTravelersAt(ItemDuctUnit.this);
            }
        }
    }
}
