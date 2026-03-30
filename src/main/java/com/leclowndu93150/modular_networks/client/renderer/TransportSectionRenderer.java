package com.leclowndu93150.modular_networks.client.renderer;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.uv.IconTransformation;
import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.attachment.cover.Cover;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.block.TransportDuctBlock;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.blockentity.TransportDuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ModularNetworks.MODID, value = Dist.CLIENT)
public class TransportSectionRenderer {

    private static final Map<String, TextureAtlasSprite> SPRITE_CACHE = new ConcurrentHashMap<>();

    private static final Map<TransportDuctBlockEntity.Tier, TubeTextures> TIER_TEXTURES = Map.of(
            TransportDuctBlockEntity.Tier.BASIC, new TubeTextures("copper_trans", "copper_band", "green_glass", 96),
            TransportDuctBlockEntity.Tier.LONG_RANGE, new TubeTextures("lead_trans", "lead_band", "green_glass", 80),
            TransportDuctBlockEntity.Tier.LINKING, new TubeTextures("enderium_trans", "enderium_band", "green_glass", 128),
            TransportDuctBlockEntity.Tier.FRAME, new TubeTextures("copper_trans", "copper_band", null, 0)
    );

    @SubscribeEvent
    public static void onAddSectionGeometry(AddSectionGeometryEvent event) {
        Level level = event.getLevel();
        BlockPos origin = event.getSectionOrigin();

        List<TransportRenderData> renderData = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (!(level.getBlockEntity(pos) instanceof TransportDuctBlockEntity tbe)) continue;
                    if (!(tbe.getBlockState().getBlock() instanceof TransportDuctBlock)) continue;

                    TransportDuctBlockEntity.Tier tier = tbe.getTier();
                    if (tier == TransportDuctBlockEntity.Tier.FRAME) continue;

                    TubeTextures tex = TIER_TEXTURES.get(tier);
                    if (tex == null) continue;

                    int connectionMask = getConnectionMask(tbe, tbe.getBlockState());
                    boolean[] externalSides = new boolean[6];
                    CoverSnapshot[] covers = new CoverSnapshot[6];
                    for (Direction dir : Direction.values()) {
                        externalSides[dir.ordinal()] = isExternalConnection(tbe, tbe.getBlockState(), dir);
                        Attachment att = tbe.getAttachment(dir);
                        if (att instanceof Cover cover && com.leclowndu93150.modular_networks.item.CoverItem.isValidCoverState(cover.getCoverState())) {
                            covers[dir.ordinal()] = new CoverSnapshot(cover.getCoverState(), dir);
                        }
                    }

                    renderData.add(new TransportRenderData(pos, connectionMask, externalSides, tex, covers));
                }
            }
        }

        if (renderData.isEmpty()) return;

        event.addRenderer(context -> {
            DuctModels.init();
            CCRenderState ccrs = CCRenderState.instance();

            for (TransportRenderData data : renderData) {
                renderTransportDuct(ccrs, context, data);
                for (CoverSnapshot cover : data.covers) {
                    if (cover != null) {
                        renderCover(context, data.pos, cover);
                    }
                }
            }
        });
    }

    private static void renderTransportDuct(CCRenderState ccrs, AddSectionGeometryEvent.SectionRenderingContext context, TransportRenderData data) {
        int lx = data.pos.getX() & 15;
        int ly = data.pos.getY() & 15;
        int lz = data.pos.getZ() & 15;
        Translation trans = new Translation(lx + 0.5, ly + 0.5, lz + 0.5);
        int light = LevelRenderer.getLightColor(context.getRegion(), data.pos);

        VertexConsumer translucentConsumer = context.getOrCreateChunkBuffer(RenderType.translucent());
        VertexConsumer cutoutConsumer = context.getOrCreateChunkBuffer(RenderType.cutout());

        TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + data.tex.frame);
        TextureAtlasSprite bandSprite = data.tex.band != null ? getSprite("block/duct/base/" + data.tex.band) : null;
        IconTransformation frameIcon = new IconTransformation(frameSprite);
        IconTransformation bandIcon = bandSprite != null ? new IconTransformation(bandSprite) : null;

        for (Direction dir : Direction.values()) {
            if (data.externalSides[dir.ordinal()] && bandIcon != null) {
                ccrs.reset();
                ccrs.computeLighting = false;
                ccrs.bind(cutoutConsumer, RenderType.cutout().format());
                ccrs.brightness = light;
                ccrs.baseColour = 0xFFFFFFFF;
                DuctModels.modelTransportConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
            }
        }

        if (DuctModels.modelTransportConnection[data.connectionMask].verts.length != 0) {
            ccrs.reset();
            ccrs.computeLighting = false;
            ccrs.bind(translucentConsumer, RenderType.translucent().format());
            ccrs.brightness = light;
            ccrs.baseColour = 0xFFFFFFFF;
            DuctModels.modelTransportConnection[data.connectionMask].render(ccrs, trans, frameIcon);
        }

        if (data.tex.fill != null && data.tex.fillAlpha > 0) {
            TextureAtlasSprite fillSprite = getSprite("block/duct/base/" + data.tex.fill);
            IconTransformation fillIcon = new IconTransformation(fillSprite);
            ccrs.reset();
            ccrs.computeLighting = false;
            ccrs.bind(translucentConsumer, RenderType.translucent().format());
            ccrs.brightness = light;
            ccrs.baseColour = rgba(255, 255, 255, data.tex.fillAlpha);
            if (DuctModels.modelTransport[data.connectionMask].verts.length != 0) {
                DuctModels.modelTransport[data.connectionMask].render(ccrs, trans, fillIcon);
            }
            ccrs.baseColour = 0xFFFFFFFF;
        }
    }

    private static int getConnectionMask(DuctBlockEntity be, BlockState state) {
        int mask = 0;
        for (Direction dir : Direction.values()) {
            boolean connected = state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir));
            Attachment attachment = be.getAttachment(dir);
            if (connected || (attachment != null && !(attachment instanceof Cover))) {
                mask |= (1 << dir.ordinal());
            }
        }
        return mask;
    }

    private static boolean isExternalConnection(DuctBlockEntity be, BlockState state, Direction dir) {
        boolean connected = state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir));
        Attachment attachment = be.getAttachment(dir);
        if (!connected && (attachment == null || attachment instanceof Cover)) return false;
        if (be.getLevel() == null) return true;
        return !(be.getLevel().getBlockState(be.getBlockPos().relative(dir)).getBlock() instanceof DuctBlock);
    }

    private static TextureAtlasSprite getSprite(String path) {
        return SPRITE_CACHE.computeIfAbsent(path, p ->
                Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, p)));
    }

    public static void clearSpriteCache() {
        SPRITE_CACHE.clear();
    }

    private static int rgba(int r, int g, int b, int a) {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | a & 0xFF;
    }

    private static void renderCover(AddSectionGeometryEvent.SectionRenderingContext context, BlockPos pos, CoverSnapshot cover) {
        BlockState coverState = cover.state;
        Direction side = cover.side;

        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(coverState);
        AABB bounds = DuctHitHelper.coverBox(side);
        TextureAtlasSprite sideSprite = getSprite("block/duct/attachment/cover/cover_side");

        RenderType coverType = ItemBlockRenderTypes.getRenderType(coverState, true);
        if (coverType.getChunkLayerId() == -1) {
            coverType = RenderType.cutout();
        }
        VertexConsumer consumer = context.getOrCreateChunkBuffer(coverType);

        float nudge = 1.0F / 512.0F;
        PoseStack poseStack = context.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(
                (pos.getX() & 15) + side.getStepX() * nudge,
                (pos.getY() & 15) + side.getStepY() * nudge,
                (pos.getZ() & 15) + side.getStepZ() * nudge
        );
        PoseStack.Pose pose = poseStack.last();
        int light = LevelRenderer.getLightColor(context.getRegion(), pos);

        for (BakedQuad quad : DuctBlockEntityRenderer.sliceCoverQuads(coverState, pos, model, side, bounds, sideSprite)) {
            int tint = quad.isTinted()
                    ? Minecraft.getInstance().getBlockColors().getColor(coverState, context.getRegion(), pos, quad.getTintIndex())
                    : -1;
            float r = tint == -1 ? 1.0F : FastColor.ARGB32.red(tint) / 255.0F;
            float g = tint == -1 ? 1.0F : FastColor.ARGB32.green(tint) / 255.0F;
            float b = tint == -1 ? 1.0F : FastColor.ARGB32.blue(tint) / 255.0F;
            consumer.putBulkData(pose, quad, r, g, b, 1.0F, light, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
    }

    private record TubeTextures(String frame, String band, String fill, int fillAlpha) {}

    private record CoverSnapshot(BlockState state, Direction side) {}

    private record TransportRenderData(BlockPos pos, int connectionMask, boolean[] externalSides, TubeTextures tex, CoverSnapshot[] covers) {}
}
