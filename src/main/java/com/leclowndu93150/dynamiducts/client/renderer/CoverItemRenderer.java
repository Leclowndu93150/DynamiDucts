package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.item.CoverItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CoverItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static CoverItemRenderer instance;

    private CoverItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static CoverItemRenderer get() {
        if (instance == null) {
            instance = new CoverItemRenderer();
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState coverState = CoverItem.getCoverState(stack);
        if (coverState == null) {
            coverState = Blocks.BARRIER.defaultBlockState();
        }

        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, -0.5);
        DuctBlockEntityRenderer.renderCoverPreview(
                coverState,
                Minecraft.getInstance().level,
                BlockPos.ZERO,
                Direction.NORTH,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                1.0F
        );
        poseStack.popPose();
    }
}
