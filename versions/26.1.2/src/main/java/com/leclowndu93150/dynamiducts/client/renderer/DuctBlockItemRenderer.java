package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DuctBlockItemRenderer implements SpecialModelRenderer<BlockItem> {

    private final DuctBlockEntityRenderer renderer = new DuctBlockEntityRenderer();
    private final Map<BlockItem, BlockEntity> cache = new IdentityHashMap<>();

    @Override
    public void submit(BlockItem blockItem, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                       int overlayCoords, boolean hasFoil, int outlineColor) {
        if (blockItem == null || !(blockItem.getBlock() instanceof EntityBlock entityBlock)) {
            return;
        }

        BlockEntity blockEntity = cache.computeIfAbsent(blockItem, item ->
                entityBlock.newBlockEntity(BlockPos.ZERO, getItemRenderState(blockItem)));
        if (!(blockEntity instanceof DuctBlockEntity ductBE)) {
            return;
        }

        DuctRenderState renderState = renderer.createRenderState();
        renderer.extractRenderState(ductBE, renderState, 0.0F, Vec3.ZERO, null);
        renderState.lightCoords = lightCoords;
        renderState.overlay = overlayCoords;
        renderer.submitDuct(renderState, poseStack, collector);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        for (int i = 0; i < 8; i++) {
            output.accept(new Vector3f(i & 1, i >> 1 & 1, i >> 2 & 1));
        }
    }

    @Override
    public BlockItem extractArgument(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof EntityBlock ? blockItem : null;
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

    public record Unbaked() implements SpecialModelRenderer.Unbaked<BlockItem> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<BlockItem> bake(SpecialModelRenderer.BakingContext context) {
            return new DuctBlockItemRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
