package com.leclowndu93150.modular_networks.core.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class FilterLogic {

    public static final int ROUTE_TYPE_NEAREST = 0;
    public static final int ROUTE_TYPE_FURTHEST = 1;
    public static final int ROUTE_TYPE_RANDOM = 2;
    public static final int ROUTE_TYPE_ROUND_ROBIN = 3;
    public static final int ROUTE_TYPE_COUNT = 4;

    private final int slotCount;
    private final ItemStack[] filterStacks;
    private boolean whitelist = false;
    private boolean matchComponents = false;
    private boolean matchModId = false;
    private int routeType = ROUTE_TYPE_NEAREST;
    private int maxStock = -1;

    public FilterLogic(int slotCount) {
        this.slotCount = Math.max(1, slotCount);
        this.filterStacks = new ItemStack[this.slotCount];
        for (int i = 0; i < filterStacks.length; i++) {
            filterStacks[i] = ItemStack.EMPTY;
        }
    }

    public boolean matchesItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        boolean found = false;
        for (ItemStack filter : filterStacks) {
            if (filter.isEmpty()) continue;
            if (matchesFilter(stack, filter)) {
                found = true;
                break;
            }
        }
        return whitelist == found;
    }

    private boolean matchesFilter(ItemStack stack, ItemStack filter) {
        if (matchComponents) {
            return ItemStack.isSameItemSameComponents(stack, filter);
        }
        return ItemStack.isSameItem(stack, filter);
    }

    public boolean matchesFluid(FluidStack stack) {
        if (stack.isEmpty()) return whitelist && hasNoFilters();

        for (ItemStack filter : filterStacks) {
            if (filter.isEmpty()) continue;
        }
        return whitelist;
    }

    private boolean hasNoFilters() {
        for (ItemStack filter : filterStacks) {
            if (!filter.isEmpty()) return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return hasNoFilters();
    }

    public ItemStack getFilterStack(int slot) {
        if (slot < 0 || slot >= filterStacks.length) return ItemStack.EMPTY;
        return filterStacks[slot];
    }

    public void setFilterStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < filterStacks.length) {
            filterStacks[slot] = stack.copy();
        }
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isMatchComponents() {
        return matchComponents;
    }

    public void setMatchComponents(boolean matchComponents) {
        this.matchComponents = matchComponents;
    }

    public boolean isMatchModId() {
        return matchModId;
    }

    public void setMatchModId(boolean matchModId) {
        this.matchModId = matchModId;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public int getMaxStockOrDefault(int fallback) {
        return maxStock > 0 ? maxStock : fallback;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = Math.floorMod(routeType, ROUTE_TYPE_COUNT);
    }

    public int getSlotCount() {
        return slotCount;
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Whitelist", whitelist);
        tag.putBoolean("MatchComponents", matchComponents);
        tag.putBoolean("MatchModId", matchModId);
        tag.putInt("RouteType", routeType);
        tag.putInt("MaxStock", maxStock);

        ListTag items = new ListTag();
        for (ItemStack stack : filterStacks) {
            if (stack.isEmpty()) {
                items.add(new CompoundTag());
            } else {
                items.add((Tag) stack.save(provider));
            }
        }
        tag.put("Filters", items);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider provider) {
        whitelist = tag.getBoolean("Whitelist");
        matchComponents = tag.getBoolean("MatchComponents");
        matchModId = tag.getBoolean("MatchModId");
        routeType = tag.contains("RouteType") ? Math.floorMod(tag.getInt("RouteType"), ROUTE_TYPE_COUNT) : ROUTE_TYPE_NEAREST;
        maxStock = tag.getInt("MaxStock");

        ListTag items = tag.getList("Filters", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(items.size(), filterStacks.length); i++) {
            CompoundTag itemTag = items.getCompound(i);
            if (itemTag.isEmpty()) {
                filterStacks[i] = ItemStack.EMPTY;
            } else {
                filterStacks[i] = ItemStack.parseOptional(provider, itemTag);
            }
        }
    }
}
