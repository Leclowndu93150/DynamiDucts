package com.leclowndu93150.dynamiducts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;

public class DuctBlockItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static DuctBlockItemRenderer instance;
    private final java.util.Map<BlockItem, BlockEntity> cache = new IdentityHashMap<>();

    private DuctBlockItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static DuctBlockItemRenderer get() {
        if (instance == null) {
            instance = new DuctBlockItemRenderer();
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof EntityBlock entityBlock)) {
            return;
        }

        BlockEntity blockEntity = cache.computeIfAbsent(blockItem, item ->
                entityBlock.newBlockEntity(BlockPos.ZERO, getItemRenderState(blockItem)));
        if (blockEntity == null) {
            return;
        }

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private static BlockState getItemRenderState(BlockItem blockItem) {
        BlockState state = blockItem.getBlock().defaultBlockState();
        if (blockItem.getBlock() instanceof DuctBlock) {
            state = state
                    .setValue(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.DOWN), true)
                    .setValue(DuctBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), true);
        }
        return state;
    }
}
