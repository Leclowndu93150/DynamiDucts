package com.leclowndu93150.dynamiducts.core.attachment;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class Attachment {

    protected final DuctBlockEntity parent;
    protected final Direction side;

    protected Attachment(DuctBlockEntity parent, Direction side) {
        this.parent = parent;
        this.side = side;
    }

    public abstract Identifier getId();

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

    public ItemStack getDrop() {
        return ItemStack.EMPTY;
    }

    public DuctBlockEntity getParent() {
        return parent;
    }

    public Direction getSide() {
        return side;
    }

    public void save(ValueOutput output) {
        output.putString("Id", getId().toString());
        output.putInt("Side", side.ordinal());
        saveAdditional(output);
    }

    public void load(ValueInput input) {
        loadAdditional(input);
    }

    protected void saveAdditional(ValueOutput output) {
    }

    protected void loadAdditional(ValueInput input) {
    }
}
