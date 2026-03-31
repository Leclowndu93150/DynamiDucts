package com.leclowndu93150.dynamiducts.core.attachment;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class Attachment {

    protected final DuctBlockEntity parent;
    protected final Direction side;

    protected Attachment(DuctBlockEntity parent, Direction side) {
        this.parent = parent;
        this.side = side;
    }

    public abstract ResourceLocation getId();

    public boolean isServo() {
        return false;
    }

    public boolean isFilter() {
        return false;
    }

    public boolean isRetriever() {
        return false;
    }

    public boolean canSend() {
        return false;
    }

    public boolean isNode() {
        return true;
    }

    public void tick() {
    }

    public void onNeighborChange() {
    }

    public DuctBlockEntity getParent() {
        return parent;
    }

    public Direction getSide() {
        return side;
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", getId().toString());
        tag.putInt("Side", side.ordinal());
        saveAdditional(tag, provider);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider provider) {
        loadAdditional(tag, provider);
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    }
}
