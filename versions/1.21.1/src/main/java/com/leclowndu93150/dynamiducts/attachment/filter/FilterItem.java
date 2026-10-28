package com.leclowndu93150.dynamiducts.attachment.filter;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class FilterItem extends ConnectionBase {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, "filter_item");

    public FilterItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
        super(parent, side, tier);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isFilter() {
        return true;
    }

    @Override
    public boolean canSend() {
        return true;
    }

    @Override
    protected void performAction() {
    }
}
