package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.blockentity.ItemDuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.item.ItemDuctUnit;
import com.leclowndu93150.dynamiducts.duct.item.TravelingItemSnapshot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class ItemDuctRenderer extends DuctBlockEntityRenderer {

    private static final float ITEM_SCALE = 0.35F;
    private static final int MAX_ITEMS_PER_DUCT = 16;
    private static final float SPIN_SPEED = 0.5F;

    private final ItemModelResolver itemModelResolver;

    public ItemDuctRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ItemDuctRenderState createRenderState() {
        return new ItemDuctRenderState();
    }

    @Override
    public void extractRenderState(DuctBlockEntity be, DuctRenderState renderState, float partialTick, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(be, renderState, partialTick, cameraPosition, breakProgress);

        if (be instanceof ItemDuctBlockEntity itemBE && renderState instanceof ItemDuctRenderState itemState) {
            extractTravelingItems(itemBE, itemState, partialTick);
        }
    }

    @Override
    public void submit(DuctRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(renderState, poseStack, collector, camera);

        if (renderState instanceof ItemDuctRenderState itemState) {
            submitTravelingItems(itemState, poseStack, collector);
        }
    }

    private void extractTravelingItems(ItemDuctBlockEntity be, ItemDuctRenderState renderState, float partialTick) {
        var unit = be.getDuctUnit(DuctToken.ITEM);
        if (!(unit instanceof ItemDuctUnit itemUnit)) return;
        if (be.getLevel() == null) return;
        if (!itemUnit.isTransparent()) return;
        float gameTime = (be.getLevel().getGameTime() + partialTick) * SPIN_SPEED;
        renderState.spinAngle = gameTime % 360;

        int rendered = 0;
        for (TravelingItemSnapshot tItem : itemUnit.getClientTravelingItems()) {
            if (rendered >= MAX_ITEMS_PER_DUCT) break;

            float travel = tItem.getProgress(partialTick) - 0.5F;
            Direction direction = travel < 0.0F ? tItem.getOldDirection() : tItem.getDirection();
            float offsetX = direction.getStepX() * travel;
            float offsetY = direction.getStepY() * travel;
            float offsetZ = direction.getStepZ() * travel;

            ItemStackRenderState itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, tItem.getStack(), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            renderState.travelingItems.add(new ItemDuctRenderState.TravelingItem(itemState, offsetX, offsetY, offsetZ));
            rendered++;
        }
    }

    private void submitTravelingItems(ItemDuctRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        for (ItemDuctRenderState.TravelingItem tItem : renderState.travelingItems) {
            poseStack.pushPose();
            poseStack.translate(0.5F + tItem.offsetX(), 0.5F + tItem.offsetY() - 0.05F, 0.5F + tItem.offsetZ());
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.spinAngle));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            tItem.item().submit(poseStack, collector, renderState.lightCoords, renderState.overlay, 0);

            poseStack.popPose();
        }
    }
}
