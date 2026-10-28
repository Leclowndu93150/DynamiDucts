package com.leclowndu93150.dynamiducts.core.attachment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.HashSet;
import java.util.Set;

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

    private Set<String> cachedModIds;
    private Set<String> cachedFluidModIds;
    private boolean needsRecalc = true;

    public FilterLogic(int slotCount) {
        this.slotCount = Math.max(1, slotCount);
        this.filterStacks = new ItemStack[this.slotCount];
        for (int i = 0; i < filterStacks.length; i++) {
            filterStacks[i] = ItemStack.EMPTY;
        }
    }

    public boolean matchesItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (matchModId) {
            recalcIfNeeded();
            if (!cachedModIds.isEmpty()) {
                String mod = stack.getItem().builtInRegistryHolder().key().identifier().getNamespace();
                if (cachedModIds.contains(mod)) return whitelist;
            }
        }

        boolean found = false;
        for (ItemStack filter : filterStacks) {
            if (filter.isEmpty()) continue;
            if (matchesFilterEntry(stack, filter)) {
                found = true;
                break;
            }
        }
        return whitelist == found;
    }

    private boolean matchesFilterEntry(ItemStack stack, ItemStack filter) {
        if (matchComponents) {
            return ItemStack.isSameItemSameComponents(stack, filter);
        }
        return ItemStack.isSameItem(stack, filter);
    }

    private void recalcIfNeeded() {
        if (!needsRecalc) return;
        needsRecalc = false;
        cachedModIds = new HashSet<>();
        cachedFluidModIds = new HashSet<>();
        for (ItemStack filter : filterStacks) {
            if (!filter.isEmpty()) {
                cachedModIds.add(filter.getItem().builtInRegistryHolder().key().identifier().getNamespace());
                FluidStack fluid = getFilterFluid(filter);
                if (!fluid.isEmpty()) {
                    cachedFluidModIds.add(fluid.getFluid().builtInRegistryHolder().key().identifier().getNamespace());
                }
            }
        }
    }

    public void markDirty() {
        needsRecalc = true;
    }

    public boolean matchesFluid(FluidStack stack) {
        if (stack.isEmpty()) return false;

        if (matchModId) {
            recalcIfNeeded();
            if (!cachedFluidModIds.isEmpty()) {
                String mod = stack.getFluid().builtInRegistryHolder().key().identifier().getNamespace();
                if (cachedFluidModIds.contains(mod)) return whitelist;
            }
        }

        boolean found = false;
        for (ItemStack filter : filterStacks) {
            if (filter.isEmpty()) continue;
            FluidStack filterFluid = getFilterFluid(filter);
            if (!filterFluid.isEmpty() && matchesFluidEntry(stack, filterFluid)) {
                found = true;
                break;
            }
        }
        return whitelist == found;
    }

    private boolean matchesFluidEntry(FluidStack stack, FluidStack filter) {
        if (matchComponents) {
            return FluidStack.isSameFluidSameComponents(stack, filter);
        }
        return FluidStack.isSameFluid(stack, filter);
    }

    private static FluidStack getFilterFluid(ItemStack filter) {
        return FluidUtil.getFirstStackContained(filter);
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
            markDirty();
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

    public void save(ValueOutput output) {
        output.putBoolean("Whitelist", whitelist);
        output.putBoolean("MatchComponents", matchComponents);
        output.putBoolean("MatchModId", matchModId);
        output.putInt("RouteType", routeType);
        output.putInt("MaxStock", maxStock);

        ValueOutput.TypedOutputList<ItemStack> items = output.list("Filters", ItemStack.OPTIONAL_CODEC);
        for (ItemStack stack : filterStacks) {
            items.add(stack);
        }
    }

    public void load(ValueInput input) {
        whitelist = input.getBooleanOr("Whitelist", false);
        matchComponents = input.getBooleanOr("MatchComponents", false);
        matchModId = input.getBooleanOr("MatchModId", false);
        routeType = input.getInt("RouteType").map(type -> Math.floorMod(type, ROUTE_TYPE_COUNT)).orElse(ROUTE_TYPE_NEAREST);
        maxStock = input.getIntOr("MaxStock", 0);

        int i = 0;
        for (ItemStack stack : input.listOrEmpty("Filters", ItemStack.OPTIONAL_CODEC)) {
            if (i >= filterStacks.length) break;
            filterStacks[i++] = stack;
        }
        markDirty();
    }
}
