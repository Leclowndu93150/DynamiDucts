package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.CCModel;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.CCRenderState;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.IconTransformation;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.Translation;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnit;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

public class DuctBlockEntityRenderer implements BlockEntityRenderer<DuctBlockEntity> {

    private static final Map<String, DuctTextures> DUCT_TEX = new HashMap<>();
    private static final Map<String, TextureAtlasSprite> SPRITE_CACHE = new ConcurrentHashMap<>();
    private static final Map<Block, String> DUCT_NAME_CACHE = new HashMap<>();
    private static final String REDSTONE_STILL = "fluid/redstone_still";
    private static final String GLOWSTONE_STILL = "fluid/glowstone_still";
    private static final String CRYOTHEUM_STILL = "fluid/cryotheum_still";
    private static final Translation HALF_TRANSLATION = new Translation(0.5, 0.5, 0.5);

    static {
        reg("energy_duct_basic", "lead_trans", "lead", "redstone_background", 255);
        reg("energy_duct_hardened", "invar_trans", "invar", "redstone_background", 255);
        reg("energy_duct_reinforced", "electrum_trans", "electrum", REDSTONE_STILL, 192);
        reg("energy_duct_signalum", "signalum_trans", "signalum", REDSTONE_STILL, 192);
        reg("energy_duct_resonant", "enderium_trans", "enderium", REDSTONE_STILL, 192);
        reg("energy_duct_superconductor", "enderium_trans", "enderium", REDSTONE_STILL, 255, FrameType.FRAME, "electrum_trans", "electrum_band", CRYOTHEUM_STILL, 96);
        reg("energy_duct_reinforced_empty", "electrum_trans", "electrum", null, 0);
        reg("energy_duct_signalum_empty", "signalum_trans", "signalum", null, 0);
        reg("energy_duct_resonant_empty", "enderium_trans", "enderium", null, 0);
        reg("energy_duct_superconductor_empty", "enderium_trans", "enderium", REDSTONE_STILL, 192, FrameType.FRAME, "electrum_trans", "electrum_band", null, 0);

        reg("fluid_duct_basic", "copper_trans", "copper", null, 0);
        reg("fluid_duct_basic_opaque", "copper", "copper", null, 0);
        reg("fluid_duct_hardened", "invar_trans", "invar", null, 0);
        reg("fluid_duct_hardened_opaque", "invar", "invar", null, 0);
        reg("fluid_duct_energy", "invar_signalum_trans", "invar", null, 0);
        reg("fluid_duct_energy_opaque", "invar_signalum", "invar", null, 0);
        reg("fluid_duct_super", "invar_trans", "invar", null, 0, FrameType.LARGE, "bronze_large", null, null, 0);
        reg("fluid_duct_super_opaque", "invar", "invar", null, 0, FrameType.LARGE, "bronze_large", null, null, 0);

        reg("item_duct_basic", "tin_trans", "tin", null, 0);
        reg("item_duct_basic_opaque", "tin", "tin", null, 0);
        regWithOverlay("item_duct_dense", "tin_trans", "tin", "dense");
        regWithOverlay("item_duct_dense_opaque", "tin", "tin", "dense");
        regWithOverlay("item_duct_vacuum", "tin_trans", "tin", "vacuum");
        regWithOverlay("item_duct_vacuum_opaque", "tin", "tin", "vacuum");
        reg("item_duct_fast", "tin_trans", "tin", GLOWSTONE_STILL, 80);
        reg("item_duct_fast_opaque", "tin_alt", "tin", null, 0);
        reg("item_duct_energy", "tin_signalum_trans", "tin", null, 0);
        reg("item_duct_energy_opaque", "tin_signalum", "tin", null, 0);
        reg("item_duct_energy_fast", "tin_signalum_trans", "tin", GLOWSTONE_STILL, 80);
        reg("item_duct_energy_fast_opaque", "tin_alt_signalum", "tin", null, 0);

        reg("transport_duct_basic", null, null, null, 0, FrameType.TRANSPORT, "copper_trans", "copper_band", "green_glass", 96);
        reg("transport_duct_long_range", null, null, null, 0, FrameType.TRANSPORT, "lead_trans", "lead_band", "green_glass", 80);
        reg("transport_duct_linking", null, null, null, 0, FrameType.TRANSPORT, "enderium_trans", "enderium_band", "green_glass", 128);
        reg("transport_duct_frame", null, null, null, 0, FrameType.TRANSPORT, "copper_trans", "copper_band", null, 0);

        reg("structural_duct", "structure", null, null, 0);
        reg("lux_duct", "lumium", null, null, 0);
    }

    private static void reg(String name, String base, String connection, String fluid, int fluidAlpha) {
        reg(name, base, connection, fluid, fluidAlpha, FrameType.NONE, null, null, null, 0);
    }

    private static void regWithOverlay(String name, String base, String connection, String overlay) {
        boolean opaque = base != null && !base.contains("trans");
        DUCT_TEX.put(name, new DuctTextures(base, connection, null, 0, opaque, FrameType.NONE, null, null, null, 0, overlay));
    }

    private static void reg(String name, String base, String connection, String fluid, int fluidAlpha,
                            FrameType frameType, String frame, String frameBand, String frameFluid, int frameFluidAlpha) {
        boolean opaque = base != null && !base.contains("trans");
        DUCT_TEX.put(name, new DuctTextures(base, connection, fluid, fluidAlpha, opaque, frameType, frame, frameBand, frameFluid, frameFluidAlpha, null));
    }

    public DuctBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        DuctModels.init();
    }

    @Override
    public boolean shouldRender(DuctBlockEntity be, Vec3 cameraPos) {
        return (!DuctSectionRenderer.isBaked(be) || hasDynamicContent(be) || getDestroyStage(be) >= 0)
                && BlockEntityRenderer.super.shouldRender(be, cameraPos);
    }

    protected boolean hasDynamicContent(DuctBlockEntity be) {
        return false;
    }

    @Override
    public void render(DuctBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        if (DuctSectionRenderer.isBaked(be)) {
            renderBreakingOverlay(be, poseStack);
            return;
        }

        DuctRenderData data = extract(be);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.baseColour = 0xFFFFFFFF;

        try {
            renderDuct(ccrs, new EntityBuffers(bufferSource, poseStack, data.itemRender), HALF_TRANSLATION, data);
        } catch (Exception e) {
            DynamiDucts.LOG.error("[DuctBESR] Exception during render at {}", be.getBlockPos(), e);
        }
    }

    private static int getDestroyStage(DuctBlockEntity be) {
        SortedSet<BlockDestructionProgress> progress = Minecraft.getInstance().levelRenderer.destructionProgress.get(be.getBlockPos().asLong());
        return progress == null || progress.isEmpty() ? -1 : progress.last().getProgress();
    }

    private static void renderBreakingOverlay(DuctBlockEntity be, PoseStack poseStack) {
        int stage = getDestroyStage(be);
        if (stage < 0) {
            return;
        }

        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(stage)),
                poseStack.last(), 1.0F);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        renderDuct(ccrs, (state, translucent) -> state.bind(consumer, DefaultVertexFormat.BLOCK, poseStack), HALF_TRANSLATION, extract(be));
    }

    public static DuctRenderData extract(DuctBlockEntity be) {
        DuctRenderData data = new DuctRenderData();
        BlockState state = be.getBlockState();
        String ductName = DUCT_NAME_CACHE.computeIfAbsent(state.getBlock(),
                b -> b.builtInRegistryHolder().key().location().getPath());
        data.textures = DUCT_TEX.getOrDefault(ductName, DUCT_TEX.get("structural_duct"));
        data.itemRender = isItemRender(be);
        for (Direction dir : Direction.values()) {
            data.connected[dir.ordinal()] = isConnected(be, state, dir);
            data.connectionDetail[dir.ordinal()] = shouldRenderConnectionDetail(be, state, dir);
            if (data.connected[dir.ordinal()]) {
                data.connectionMask |= 1 << dir.ordinal();
            }
        }
        extractAttachments(be, data);
        extractFluidContents(be, data);
        return data;
    }

    public static void renderDuct(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctRenderData data) {
        DuctTextures tex = data.textures;
        if (tex == null) {
            return;
        }

        renderBase(ccrs, buffers, trans, tex, data);
        if (!tex.opaque) {
            renderDecorativeOverlays(ccrs, buffers, trans, tex, data);
            renderFluidContents(ccrs, buffers, trans, data);
        }
        renderAttachments(ccrs, buffers, trans, data);
    }

    private static void renderBase(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctTextures tex, DuctRenderData data) {
        int connectionMask = data.connectionMask;
        boolean itemRender = data.itemRender;

        if (tex.base != null) {
            TextureAtlasSprite baseSprite = getSprite("block/duct/base/" + tex.base);
            IconTransformation baseIcon = new IconTransformation(baseSprite);
            buffers.bind(ccrs, !tex.opaque);
            (tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask])
                    .render(ccrs, trans, baseIcon);
        }

        if (tex.fluid != null && tex.fluidAlpha == 255) {
            renderTinted(ccrs, buffers, DuctModels.modelFluidTubes[connectionMask], trans, resolveSpritePath(tex.fluid), 255);
        }

        if (tex.overlay != null) {
            TextureAtlasSprite overlaySprite = getSprite("block/duct/base/" + tex.overlay);
            IconTransformation overlayIcon = new IconTransformation(overlaySprite);
            CCModel overlayModel = tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask];
            buffers.bind(ccrs, false);
            ccrs.baseColour = 0xFFFFFFFF;
            overlayModel.render(ccrs, trans, overlayIcon);
        }

        if (itemRender && tex.base != null) {
            TextureAtlasSprite endCapSprite = getSprite("block/duct/base/structure_trans");
            IconTransformation endCapIcon = new IconTransformation(endCapSprite);
            buffers.bind(ccrs, false);
            for (Direction dir : Direction.values()) {
                if ((connectionMask & (1 << dir.ordinal())) != 0) {
                    DuctModels.modelConnection[0][dir.ordinal()].render(ccrs, 4, 8, trans, endCapIcon);
                }
            }
        }

        if (tex.connection != null && !itemRender) {
            TextureAtlasSprite connSprite = getSprite("block/duct/connection/" + tex.connection);
            IconTransformation connIcon = new IconTransformation(connSprite);
            buffers.bind(ccrs, false);

            for (Direction dir : Direction.values()) {
                if (data.connectionDetail[dir.ordinal()]) {
                    DuctModels.modelConnection[1][dir.ordinal()].render(ccrs, trans, connIcon);
                }
            }
        }

        renderFrameBase(ccrs, buffers, trans, tex, data);
    }

    private static void renderFrameBase(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctTextures tex, DuctRenderData data) {
        if (tex.frameType == FrameType.NONE || tex.frame == null) {
            return;
        }
        int connectionMask = data.connectionMask;

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, buffers, trans, connectionMask, false, "block/duct/side_ducts", 255);
            case FRAME -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (!data.connected[dir.ordinal()]) {
                        continue;
                    }
                    if (data.connectionDetail[dir.ordinal()] && bandIcon != null) {
                        buffers.bind(ccrs, false);
                        DuctModels.modelFrameConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                        buffers.bind(ccrs, isTranslucentTexture(tex.frame, true));
                        DuctModels.modelFrame[70 + dir.ordinal()].render(ccrs, trans, frameIcon);
                    }
                }

                if (DuctModels.modelFrameConnection[connectionMask].verts.length != 0) {
                    buffers.bind(ccrs, isTranslucentTexture(tex.frame, true));
                    DuctModels.modelFrameConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case LARGE -> {
                buffers.bind(ccrs, isTranslucentTexture(tex.frame, false));
                DuctModels.modelLargeTubes[connectionMask].render(ccrs, trans, new IconTransformation(getSprite("block/duct/base/" + tex.frame)));
            }
            case TRANSPORT -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (data.connectionDetail[dir.ordinal()] && bandIcon != null) {
                        buffers.bind(ccrs, false);
                        DuctModels.modelTransportConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                    }
                }

                if (DuctModels.modelTransportConnection[connectionMask].verts.length != 0) {
                    buffers.bind(ccrs, isTranslucentTexture(tex.frame, true));
                    DuctModels.modelTransportConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case NONE -> {
            }
        }
    }

    private static void renderDecorativeOverlays(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctTextures tex, DuctRenderData data) {
        int connectionMask = data.connectionMask;
        if (tex.fluid != null && tex.fluidAlpha > 0 && tex.fluidAlpha < 255) {
            renderTinted(ccrs, buffers, DuctModels.modelFluidTubes[connectionMask], trans, resolveSpritePath(tex.fluid), tex.fluidAlpha);
        }

        if (tex.frameFluid == null || tex.frameFluidAlpha <= 0) {
            return;
        }

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, buffers, trans, connectionMask, true, "block/duct/base/" + tex.frameFluid, tex.frameFluidAlpha);
            case FRAME -> {
                TextureAtlasSprite frameFluidSprite = getSprite(resolveSpritePath(tex.frameFluid));
                IconTransformation frameFluidIcon = new IconTransformation(frameFluidSprite);
                buffers.bind(ccrs, isTranslucentTexture(resolveSpritePath(tex.frameFluid), true));
                ccrs.baseColour = rgba(255, 255, 255, tex.frameFluidAlpha);

                for (Direction dir : Direction.values()) {
                    if (data.connected[dir.ordinal()] && data.connectionDetail[dir.ordinal()]) {
                        DuctModels.modelFrame[70 + dir.ordinal()].render(ccrs, trans, frameFluidIcon);
                    }
                }

                if (DuctModels.modelFrame[connectionMask].verts.length != 0) {
                    DuctModels.modelFrame[connectionMask].render(ccrs, trans, frameFluidIcon);
                }
                ccrs.baseColour = 0xFFFFFFFF;
            }
            case TRANSPORT -> {
                TextureAtlasSprite frameFluidSprite = getSprite(resolveSpritePath(tex.frameFluid));
                IconTransformation frameFluidIcon = new IconTransformation(frameFluidSprite);
                buffers.bind(ccrs, isTranslucentTexture(resolveSpritePath(tex.frameFluid), true));
                ccrs.baseColour = rgba(255, 255, 255, tex.frameFluidAlpha);
                if (DuctModels.modelTransport[connectionMask].verts.length != 0) {
                    DuctModels.modelTransport[connectionMask].render(ccrs, trans, frameFluidIcon);
                }
                ccrs.baseColour = 0xFFFFFFFF;
            }
            case LARGE, NONE -> {
            }
        }
    }

    private static void renderFluidContents(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctRenderData data) {
        if (data.fluidSprite == null) {
            return;
        }

        int connectionMask = data.connectionMask;
        int level = data.fluidLevel;
        IconTransformation icon = new IconTransformation(data.fluidSprite);

        buffers.bind(ccrs, true);
        ccrs.baseColour = data.fluidColor;
        if (level < 6) {
            CCModel[] models = DuctModels.modelFluid[level - 1];
            for (Direction dir : Direction.values()) {
                if ((connectionMask & (1 << dir.ordinal())) != 0) {
                    models[dir.ordinal()].render(ccrs, trans, icon);
                }
            }
            models[6].render(ccrs, trans, icon);
        } else {
            DuctModels.modelFluidTubes[connectionMask].render(ccrs, trans, icon);
        }
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private static void renderSideTubes(CCRenderState ccrs, DuctBuffers buffers, Translation trans, int connectionMask,
                                        boolean inner, String spritePath, int alpha) {
        CCModel[] models = inner ? DuctModels.modelSideTubesInner : DuctModels.modelSideTubes;
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);

        buffers.bind(ccrs, isTranslucentTexture(spritePath, inner || alpha < 255));
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        if (models[connectionMask].verts.length != 0) {
            models[connectionMask].render(ccrs, trans, icon);
        }
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private static void renderTinted(CCRenderState ccrs, DuctBuffers buffers, CCModel model, Translation trans, String spritePath, int alpha) {
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);
        buffers.bind(ccrs, isTranslucentTexture(spritePath, alpha < 255));
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        model.render(ccrs, trans, icon);
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private static void renderAttachments(CCRenderState ccrs, DuctBuffers buffers, Translation trans, DuctRenderData data) {
        for (int i = 0; i < 6; i++) {
            String texPath = data.attachmentTextures[i];
            CCModel model = data.attachmentModels[i];
            if (texPath == null || model == null) continue;

            TextureAtlasSprite sprite = getSprite(texPath);
            IconTransformation icon = new IconTransformation(sprite);

            buffers.bind(ccrs, false);
            model.render(ccrs, trans, icon);
        }
    }

    private static void extractAttachments(DuctBlockEntity be, DuctRenderData data) {
        Attachment[] attachments = be.getAttachments();
        if (attachments == null) return;

        for (int i = 0; i < 6; i++) {
            Attachment att = attachments[i];
            if (att == null) continue;
            String texPath;
            CCModel model;

            if (att instanceof ConnectionBase conn) {
                int tierIndex = conn.getTier().index();
                if (conn.isServo()) {
                    texPath = "block/duct/attachment/servo/servo_base_0_" + tierIndex;
                    model = DuctModels.modelConnection[getAttachmentModelIndex(conn)][i];
                } else if (conn.isFilter()) {
                    texPath = "block/duct/attachment/filter/filter_" + tierIndex;
                    model = DuctModels.modelConnection[1][i];
                } else if (conn.isRetriever()) {
                    texPath = "block/duct/attachment/retriever/retriever_base_0_" + tierIndex;
                    model = DuctModels.modelConnection[getAttachmentModelIndex(conn)][i];
                } else {
                    continue;
                }
            } else if (att instanceof Relay relay) {
                texPath = "block/duct/attachment/signallers/signaller";
                model = DuctModels.modelConnection[1 + (relay.getType() & 1)][i];
            } else {
                continue;
            }

            data.attachmentTextures[i] = texPath;
            data.attachmentModels[i] = model;
        }
    }

    private static void extractFluidContents(DuctBlockEntity be, DuctRenderData data) {
        if (!(be.getDuctUnit(DuctToken.FLUID) instanceof FluidDuctUnit fdu) || !fdu.isTransparent()) {
            return;
        }
        FluidStack fluid = fdu.getVisualFluid();
        int level = fdu.getVisualFluidLevel();
        if (fluid.isEmpty() || level <= 0) {
            return;
        }
        IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTex = fluidExt.getStillTexture(fluid);
        if (stillTex == null) {
            return;
        }
        data.fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTex);
        data.fluidColor = argbToRgba(fluidExt.getTintColor(fluid));
        data.fluidLevel = level;
    }

    private static boolean isConnected(DuctBlockEntity be, BlockState state, Direction dir) {
        return state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir)) || be.getAttachment(dir) != null;
    }

    private static boolean shouldRenderConnectionDetail(DuctBlockEntity be, BlockState state, Direction dir) {
        if (!isConnected(be, state, dir)) {
            return false;
        }
        if (be.getLevel() == null) {
            return true;
        }

        BlockState neighborState = be.getLevel().getBlockState(be.getBlockPos().relative(dir));
        if (!(neighborState.getBlock() instanceof DuctBlock neighborDuct)) {
            return true;
        }

        return !(state.getBlock() instanceof DuctBlock self) || !self.connectsSeamlesslyTo(neighborDuct);
    }

    private static TextureAtlasSprite getSprite(String path) {
        return SPRITE_CACHE.computeIfAbsent(path, p ->
                Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, p)));
    }

    public static void clearSpriteCache() {
        SPRITE_CACHE.clear();
    }

    private static String resolveSpritePath(String path) {
        return path.startsWith("fluid/") ? "block/" + path : "block/duct/base/" + path;
    }

    private static boolean isItemRender(DuctBlockEntity be) {
        return be.getLevel() == null;
    }

    private static boolean isTranslucentTexture(String texture, boolean translucent) {
        return translucent || texture.contains("trans") || texture.contains("glass") || texture.contains("flux") || texture.contains("/fluid/");
    }

    private static RenderType getCutoutRenderType(boolean itemRender) {
        return itemRender ? RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS) : Sheets.cutoutBlockSheet();
    }

    private static RenderType getTranslucentRenderType(boolean itemRender) {
        return itemRender ? RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS) : Sheets.translucentCullBlockSheet();
    }

    private static int getAttachmentModelIndex(ConnectionBase connection) {
        return connection.isActive() ? 1 : 2;
    }

    private static int argbToRgba(int color) {
        int a = color >>> 24;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        return rgba(r, g, b, a);
    }

    private static int rgba(int r, int g, int b, int a) {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | a & 0xFF;
    }

    public interface DuctBuffers {
        void bind(CCRenderState ccrs, boolean translucent);
    }

    private record EntityBuffers(MultiBufferSource bufferSource, PoseStack poseStack, boolean itemRender) implements DuctBuffers {
        @Override
        public void bind(CCRenderState ccrs, boolean translucent) {
            ccrs.bind(translucent ? getTranslucentRenderType(itemRender) : getCutoutRenderType(itemRender), bufferSource, poseStack);
        }
    }

    private enum FrameType {
        NONE,
        SIDE,
        FRAME,
        LARGE,
        TRANSPORT
    }

    public record DuctTextures(String base, String connection, String fluid, int fluidAlpha, boolean opaque,
                                FrameType frameType, String frame, String frameBand, String frameFluid, int frameFluidAlpha,
                                String overlay) {
    }
}
