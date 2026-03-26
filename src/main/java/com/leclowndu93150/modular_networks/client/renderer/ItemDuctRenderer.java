package com.leclowndu93150.modular_networks.client.renderer;

import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.item.ItemDuctUnit;
import com.leclowndu93150.modular_networks.duct.item.TravelingItemSnapshot;
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
    private static final float SPIN_SPEED = 0.5F;

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
        if (be.getLevel() == null) return;
        float gameTime = (be.getLevel().getGameTime() + partialTick) * SPIN_SPEED;
        float spinAngle = gameTime % 360;

        int rendered = 0;
        for (TravelingItemSnapshot tItem : itemUnit.getClientTravelingItems()) {
            if (rendered >= MAX_ITEMS_PER_DUCT) break;

            float travel = tItem.getProgress(partialTick) - 0.5F;
            Direction direction = travel < 0.0F ? tItem.getOldDirection() : tItem.getDirection();
            float offsetX = direction.getStepX() * travel;
            float offsetY = direction.getStepY() * travel;
            float offsetZ = direction.getStepZ() * travel;

            poseStack.pushPose();
            poseStack.translate(0.5F + offsetX, 0.5F + offsetY - 0.05F, 0.5F + offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(spinAngle));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    tItem.getStack(), ItemDisplayContext.FIXED,
                    packedLight, packedOverlay,
                    poseStack, bufferSource,
                    be.getLevel(), 0);

            poseStack.popPose();
            rendered++;
        }
    }
}
