package com.leclowndu93150.modular_networks.client.renderer;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import com.leclowndu93150.modular_networks.duct.item.ItemGrid;
import com.leclowndu93150.modular_networks.duct.item.TravelingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemDuctRenderer extends DuctBlockEntityRenderer {

    private static final float ITEM_SCALE = 0.35F;
    private static final int MAX_ITEMS_PER_DUCT = 16;
    private static float spinAngle = 0;

    public ItemDuctRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DuctBlockEntity be, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        if (be instanceof ItemDuctBlockEntity itemBE) {
            renderTravelingItems(itemBE, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderTravelingItems(ItemDuctBlockEntity be, float partialTick, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var unit = be.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit itemUnit)) return;
        if (itemUnit.getGrid() == null) return;

        ItemGrid grid = itemUnit.getGrid();
        spinAngle += partialTick * 1.5F;
        if (spinAngle > 360) spinAngle -= 360;

        int rendered = 0;
        for (TravelingItem tItem : grid.getTravelingItems()) {
            if (!tItem.currentPos.equals(be.getBlockPos())) continue;
            if (rendered >= MAX_ITEMS_PER_DUCT) break;

            float progress = (tItem.ticksInDuct + partialTick) / tItem.speed;
            Direction dir = tItem.getCurrentDirection();

            float offsetX = dir.getStepX() * progress * 0.3125F;
            float offsetY = dir.getStepY() * progress * 0.3125F;
            float offsetZ = dir.getStepZ() * progress * 0.3125F;

            poseStack.pushPose();
            poseStack.translate(0.5F + offsetX, 0.5F + offsetY - 0.05F, 0.5F + offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(spinAngle));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    tItem.stack, ItemDisplayContext.FIXED,
                    packedLight, packedOverlay,
                    poseStack, bufferSource,
                    be.getLevel(), 0);

            poseStack.popPose();
            rendered++;
        }
    }
}
