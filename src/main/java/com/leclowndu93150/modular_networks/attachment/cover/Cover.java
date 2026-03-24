package com.leclowndu93150.modular_networks.attachment.cover;

import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Cover extends Attachment {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, "cover");

    private BlockState coverState;

    public Cover(DuctBlockEntity parent, Direction side) {
        super(parent, side);
        this.coverState = Blocks.STONE.defaultBlockState();
    }

    public Cover(DuctBlockEntity parent, Direction side, BlockState state) {
        super(parent, side);
        this.coverState = state;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isNode() {
        return false;
    }

    @Override
    public boolean canSend() {
        return false;
    }

    public BlockState getCoverState() {
        return coverState;
    }

    public void setCoverState(BlockState state) {
        this.coverState = state;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("CoverState", NbtUtils.writeBlockState(coverState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("CoverState")) {
            coverState = NbtUtils.readBlockState(parent.getLevel() != null ?
                    parent.getLevel().holderLookup(Registries.BLOCK) :
                    BuiltInRegistries.BLOCK.asLookup(),
                    tag.getCompound("CoverState"));
        }
    }
}
