package com.leclowndu93150.modular_networks.attachment.retriever;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.AttachmentTier;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.core.network.Route;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import com.leclowndu93150.modular_networks.duct.item.ItemGrid;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class RetrieverItem extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "retriever_item");

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

        List<Route> routes = grid.getRouteCache().getRoutes(localUnit, grid.getNodeSet());
        int maxStack = tier.stackSize();

        for (Route route : routes) {
            for (ItemDuctUnit node : grid.getNodeSet()) {
                if (!node.getPos().equals(route.destination)) continue;

                IItemHandler source = level.getCapability(
                        Capabilities.ItemHandler.BLOCK,
                        node.getPos().relative(route.insertionSide),
                        route.insertionSide.getOpposite()
                );
                if (source == null) continue;

                for (int slot = 0; slot < source.getSlots(); slot++) {
                    ItemStack extracted = source.extractItem(slot, maxStack, true);
                    if (extracted.isEmpty()) continue;

                    if (!filter.isEmpty() && !filter.matchesItem(extracted)) continue;

                    ItemStack toSend = source.extractItem(slot, maxStack, false);
                    if (!toSend.isEmpty()) {
                        if (!node.insertItem(toSend)) {
                            source.insertItem(slot, toSend, false);
                        }
                        return;
                    }
                }
            }
        }
    }
}
