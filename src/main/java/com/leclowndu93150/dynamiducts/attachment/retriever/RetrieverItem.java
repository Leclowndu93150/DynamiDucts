package com.leclowndu93150.dynamiducts.attachment.retriever;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.core.network.Route;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.ItemGrid;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class RetrieverItem extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "retriever_item");

    public RetrieverItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isRetriever() {
        return true;
    }

    @Override
    protected void performAction() {
        var level = parent.getLevel();
        if (level == null) return;

        var unit = parent.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit localUnit)) return;
        if (!(localUnit.getGrid() instanceof ItemGrid grid)) return;

        IItemHandler localInv = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                parent.getBlockPos().relative(side),
                side.getOpposite()
        );
        if (localInv == null) return;

        int maxStack = filter.getMaxStockOrDefault(tier.stackSize());
        List<Route> routes = grid.getSortedRoutes(localUnit, filter.getRouteType());

        for (Route route : routes) {
            ItemDuctUnit remoteNode = findNode(route.destination, grid);
            if (remoteNode == null) continue;

            IItemHandler remoteInv = level.getCapability(
                    Capabilities.ItemHandler.BLOCK,
                    remoteNode.getPos().relative(route.insertionSide),
                    route.insertionSide.getOpposite()
            );
            if (remoteInv == null) continue;

            for (int slot = 0; slot < remoteInv.getSlots(); slot++) {
                ItemStack peek = remoteInv.extractItem(slot, maxStack, true);
                if (peek.isEmpty()) continue;
                if (!filter.matchesItem(peek)) continue;
                if (!canInsertInto(localInv, peek)) continue;

                ItemStack extracted = remoteInv.extractItem(slot, maxStack, false);
                if (extracted.isEmpty()) continue;

                if (tier.multiStack() && extracted.getCount() < maxStack) {
                    for (int s = slot + 1; s < remoteInv.getSlots() && extracted.getCount() < maxStack; s++) {
                        ItemStack other = remoteInv.extractItem(s, maxStack - extracted.getCount(), true);
                        if (other.isEmpty() || !ItemStack.isSameItemSameComponents(extracted, other)) continue;
                        ItemStack extra = remoteInv.extractItem(s, maxStack - extracted.getCount(), false);
                        if (!extra.isEmpty()) {
                            extracted.grow(extra.getCount());
                        }
                    }
                }

                Route returnRoute = grid.getRouteCache().getRouteBetween(remoteNode, localUnit, side, grid.getNodeSet());
                if (returnRoute == null) {
                    remoteInv.insertItem(slot, extracted, false);
                    continue;
                }

                remoteNode.insertItemWithRoute(extracted, route.insertionSide, returnRoute, tier.speedBoost());
                return;
            }
        }
    }

    private boolean canInsertInto(IItemHandler handler, ItemStack stack) {
        ItemStack simulated = stack.copy();
        for (int i = 0; i < handler.getSlots() && !simulated.isEmpty(); i++) {
            simulated = handler.insertItem(i, simulated, true);
        }
        return simulated.getCount() < stack.getCount();
    }

    private ItemDuctUnit findNode(net.minecraft.core.BlockPos pos, ItemGrid grid) {
        for (ItemDuctUnit node : grid.getNodeSet()) {
            if (node.getPos().equals(pos)) return node;
        }
        return null;
    }
}
