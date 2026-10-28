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
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class DuctBlockEntityRenderer implements BlockEntityRenderer<DuctBlockEntity, DuctRenderState> {

    private static final Map<String, DuctTextures> DUCT_TEX = new HashMap<>();
    private static final String REDSTONE_STILL = "fluid/redstone_still";
    private static final String GLOWSTONE_STILL = "fluid/glowstone_still";
    private static final String CRYOTHEUM_STILL = "fluid/cryotheum_still";
    private static final Translation HALF_TRANSLATION = new Translation(0.5, 0.5, 0.5);
    private final Map<String, TextureAtlasSprite> spriteCache = new HashMap<>();
    private final Map<Block, String> ductNameCache = new HashMap<>();

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

    public DuctBlockEntityRenderer() {
        DuctModels.init();
    }

    @Override
    public DuctRenderState createRenderState() {
        return new DuctRenderState();
    }

    @Override
    public void extractRenderState(DuctBlockEntity be, DuctRenderState renderState, float partialTick, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, renderState, partialTick, cameraPosition, breakProgress);
        BlockState state = be.getBlockState();
        String ductName = ductNameCache.computeIfAbsent(state.getBlock(),
                b -> b.builtInRegistryHolder().key().identifier().getPath());
        renderState.textures = DUCT_TEX.getOrDefault(ductName, DUCT_TEX.get("structural_duct"));
        renderState.itemRender = isItemRender(be);
        renderState.overlay = OverlayTexture.NO_OVERLAY;
        renderState.connectionMask = getConnectionMask(be, state);
        for (Direction dir : Direction.values()) {
            renderState.connected[dir.ordinal()] = isConnected(be, state, dir);
            renderState.connectionDetail[dir.ordinal()] = shouldRenderConnectionDetail(be, state, dir);
        }
        extractAttachments(be, renderState);
        extractFluidContents(be, renderState);
    }

    @Override
    public void submit(DuctRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submitDuct(renderState, poseStack, collector);
    }

    public void submitDuct(DuctRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        DuctTextures tex = renderState.textures;

        if (tex == null) {
            return;
        }

        Translation trans = HALF_TRANSLATION;

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = renderState.lightCoords;
        ccrs.overlay = renderState.overlay;
        ccrs.baseColour = 0xFFFFFFFF;
        ccrs.crumblingOverlay = renderState.breakProgress;

        try {
            renderBase(ccrs, collector, poseStack, trans, tex, renderState);
            if (!tex.opaque) {
                renderDecorativeOverlays(ccrs, collector, poseStack, trans, tex, renderState);
                renderFluidContents(renderState, ccrs, collector, poseStack, trans);
            }
            renderAttachments(renderState, ccrs, collector, poseStack, trans);
        } catch (Exception e) {
            DynamiDucts.LOG.error("[DuctBESR] Exception during render at {}", renderState.blockPos, e);
        }
    }

    private void renderBase(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack,
                             Translation trans, DuctTextures tex, DuctRenderState renderState) {
        int connectionMask = renderState.connectionMask;
        boolean itemRender = renderState.itemRender;

        if (tex.base != null) {
            TextureAtlasSprite baseSprite = getSprite("block/duct/base/" + tex.base);
            IconTransformation baseIcon = new IconTransformation(baseSprite);
            ccrs.bind(getBaseRenderType(tex, itemRender), collector, poseStack);
            (tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask])
                    .render(ccrs, trans, baseIcon);
        }

        if (tex.fluid != null && tex.fluidAlpha == 255) {
            renderTinted(ccrs, collector, poseStack, DuctModels.modelFluidTubes[connectionMask], trans,
                    resolveSpritePath(tex.fluid), 255, itemRender);
        }

        if (tex.overlay != null) {
            TextureAtlasSprite overlaySprite = getSprite("block/duct/base/" + tex.overlay);
            IconTransformation overlayIcon = new IconTransformation(overlaySprite);
            CCModel overlayModel = tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask];
            ccrs.bind(getCutoutRenderType(itemRender), collector, poseStack);
            ccrs.baseColour = 0xFFFFFFFF;
            overlayModel.render(ccrs, trans, overlayIcon);
        }

        if (itemRender && tex.base != null) {
            TextureAtlasSprite endCapSprite = getSprite("block/duct/base/structure_trans");
            IconTransformation endCapIcon = new IconTransformation(endCapSprite);
            ccrs.bind(getCutoutRenderType(true), collector, poseStack);
            for (Direction dir : Direction.values()) {
                if ((connectionMask & (1 << dir.ordinal())) != 0) {
                    DuctModels.modelConnection[0][dir.ordinal()].render(ccrs, 4, 8, trans, endCapIcon);
                }
            }
        }

        if (tex.connection != null && !itemRender) {
            TextureAtlasSprite connSprite = getSprite("block/duct/connection/" + tex.connection);
            IconTransformation connIcon = new IconTransformation(connSprite);
            ccrs.bind(getCutoutRenderType(false), collector, poseStack);

            for (Direction dir : Direction.values()) {
                if (renderState.connectionDetail[dir.ordinal()]) {
                    DuctModels.modelConnection[1][dir.ordinal()].render(ccrs, trans, connIcon);
                }
            }
        }

        renderFrameBase(ccrs, collector, poseStack, trans, tex, renderState);
    }

    private void renderFrameBase(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack, Translation trans,
                                 DuctTextures tex, DuctRenderState renderState) {
        if (tex.frameType == FrameType.NONE || tex.frame == null) {
            return;
        }

        int connectionMask = renderState.connectionMask;
        boolean itemRender = renderState.itemRender;

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, collector, poseStack, trans, connectionMask, false, "block/duct/side_ducts", itemRender);
            case FRAME -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (!renderState.connected[dir.ordinal()]) {
                        continue;
                    }
                    if (renderState.connectionDetail[dir.ordinal()] && bandIcon != null) {
                        ccrs.bind(getCutoutRenderType(itemRender), collector, poseStack);
                        DuctModels.modelFrameConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                        ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), collector, poseStack);
                        DuctModels.modelFrame[70 + dir.ordinal()].render(ccrs, trans, frameIcon);
                    }
                }

                if (DuctModels.modelFrameConnection[connectionMask].verts.length != 0) {
                    ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), collector, poseStack);
                    DuctModels.modelFrameConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case LARGE -> {
                ccrs.bind(getRenderTypeForTexture(tex.frame, false, itemRender), collector, poseStack);
                DuctModels.modelLargeTubes[connectionMask].render(ccrs, trans, new IconTransformation(getSprite("block/duct/base/" + tex.frame)));
            }
            case TRANSPORT -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (renderState.connectionDetail[dir.ordinal()] && bandIcon != null) {
                        ccrs.bind(getCutoutRenderType(itemRender), collector, poseStack);
                        DuctModels.modelTransportConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                    }
                }

                if (DuctModels.modelTransportConnection[connectionMask].verts.length != 0) {
                    ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), collector, poseStack);
                    DuctModels.modelTransportConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case NONE -> {
            }
        }
    }

    private void renderDecorativeOverlays(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack,
                                          Translation trans, DuctTextures tex, DuctRenderState renderState) {
        int connectionMask = renderState.connectionMask;
        boolean itemRender = renderState.itemRender;

        if (tex.fluid != null && tex.fluidAlpha > 0 && tex.fluidAlpha < 255) {
            renderTinted(ccrs, collector, poseStack, DuctModels.modelFluidTubes[connectionMask], trans,
                    resolveSpritePath(tex.fluid), tex.fluidAlpha, itemRender);
        }

        if (tex.frameFluid == null || tex.frameFluidAlpha <= 0) {
            return;
        }

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, collector, poseStack, trans, connectionMask, true, "block/duct/base/" + tex.frameFluid, tex.frameFluidAlpha, itemRender);
            case FRAME -> {
                TextureAtlasSprite frameFluidSprite = getSprite(resolveSpritePath(tex.frameFluid));
                IconTransformation frameFluidIcon = new IconTransformation(frameFluidSprite);
                ccrs.bind(getRenderTypeForTexture(resolveSpritePath(tex.frameFluid), true, itemRender), collector, poseStack);
                ccrs.baseColour = rgba(255, 255, 255, tex.frameFluidAlpha);

                for (Direction dir : Direction.values()) {
                    if (renderState.connected[dir.ordinal()] && renderState.connectionDetail[dir.ordinal()]) {
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
                ccrs.bind(getRenderTypeForTexture(resolveSpritePath(tex.frameFluid), true, itemRender), collector, poseStack);
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

    private void extractFluidContents(DuctBlockEntity be, DuctRenderState renderState) {
        renderState.fluidSprite = null;

        var fluidUnit = be.getDuctUnit(DuctToken.FLUID);
        if (fluidUnit instanceof FluidDuctUnit fdu && fdu.isTransparent()) {
            FluidStack fluid = fdu.getVisualFluid();
            int level = fdu.getVisualFluidLevel();
            if (!fluid.isEmpty() && level > 0) {
                FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet()
                        .get(fluid.getFluid().defaultFluidState());
                FluidTintSource tintSource = fluidModel.fluidTintSource();
                renderState.fluidSprite = fluidModel.stillMaterial().sprite();
                renderState.fluidColor = argbToRgba(tintSource != null ? tintSource.colorAsStack(fluid) : 0xFFFFFFFF);
                renderState.fluidLevel = level;
            }
        }
    }

    private void renderFluidContents(DuctRenderState renderState, CCRenderState ccrs, SubmitNodeCollector collector,
                                     PoseStack poseStack, Translation trans) {
        if (renderState.fluidSprite == null) {
            return;
        }

        int connectionMask = renderState.connectionMask;
        int level = renderState.fluidLevel;
        IconTransformation icon = new IconTransformation(renderState.fluidSprite);

        ccrs.bind(getTranslucentRenderType(renderState.itemRender), collector, poseStack);
        ccrs.baseColour = renderState.fluidColor;
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

    private void renderSideTubes(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack,
                                 Translation trans, int connectionMask, boolean inner, String spritePath, boolean itemRender) {
        renderSideTubes(ccrs, collector, poseStack, trans, connectionMask, inner, spritePath, 255, itemRender);
    }

    private void renderSideTubes(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack,
                                 Translation trans, int connectionMask, boolean inner, String spritePath, int alpha, boolean itemRender) {
        CCModel[] models = inner ? DuctModels.modelSideTubesInner : DuctModels.modelSideTubes;
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);

        ccrs.bind(getRenderTypeForTexture(spritePath, inner || alpha < 255, itemRender), collector, poseStack);
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        if (models[connectionMask].verts.length != 0) {
            models[connectionMask].render(ccrs, trans, icon);
        }
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private void renderTinted(CCRenderState ccrs, SubmitNodeCollector collector, PoseStack poseStack, CCModel model,
                              Translation trans, String spritePath, int alpha, boolean itemRender) {
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);
        ccrs.bind(getRenderTypeForTexture(spritePath, alpha < 255, itemRender), collector, poseStack);
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        model.render(ccrs, trans, icon);
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private void extractAttachments(DuctBlockEntity be, DuctRenderState renderState) {
        Attachment[] attachments = be.getAttachments();

        for (int i = 0; i < 6; i++) {
            renderState.attachmentTextures[i] = null;
            renderState.attachmentModels[i] = null;
            Attachment att = attachments == null ? null : attachments[i];
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

            renderState.attachmentTextures[i] = texPath;
            renderState.attachmentModels[i] = model;
        }
    }

    private void renderAttachments(DuctRenderState renderState, CCRenderState ccrs, SubmitNodeCollector collector,
                                    PoseStack poseStack, Translation trans) {
        for (int i = 0; i < 6; i++) {
            String texPath = renderState.attachmentTextures[i];
            CCModel model = renderState.attachmentModels[i];
            if (texPath == null || model == null) continue;

            TextureAtlasSprite sprite = getSprite(texPath);
            IconTransformation icon = new IconTransformation(sprite);

            ccrs.bind(getCutoutRenderType(false), collector, poseStack);
            model.render(ccrs, trans, icon);
        }
    }

    protected int getConnectionMask(DuctBlockEntity be, BlockState state) {
        int mask = 0;
        for (Direction dir : Direction.values()) {
            if (isConnected(be, state, dir)) {
                mask |= (1 << dir.ordinal());
            }
        }
        return mask;
    }

    private boolean isConnected(DuctBlockEntity be, BlockState state, Direction dir) {
        return state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir)) || be.getAttachment(dir) != null;
    }

    private boolean isExternalConnection(DuctBlockEntity be, BlockState state, Direction dir) {
        if (!isConnected(be, state, dir)) {
            return false;
        }
        if (be.getLevel() == null) return true;
        return !(be.getLevel().getBlockState(be.getBlockPos().relative(dir)).getBlock() instanceof DuctBlock);
    }

    private boolean shouldRenderConnectionDetail(DuctBlockEntity be, BlockState state, Direction dir) {
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

    private TextureAtlasSprite getSprite(String path) {
        return spriteCache.computeIfAbsent(path, p ->
                Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
                        .getSprite(Identifier.fromNamespaceAndPath(DynamiDucts.MODID, p)));
    }

    private static String resolveSpritePath(String path) {
        return path.startsWith("fluid/") ? "block/" + path : "block/duct/base/" + path;
    }

    private static boolean isItemRender(DuctBlockEntity be) {
        return be.getLevel() == null;
    }

    private static RenderType getBaseRenderType(DuctTextures tex, boolean itemRender) {
        return tex.opaque ? getCutoutRenderType(itemRender) : getTranslucentRenderType(itemRender);
    }

    private static RenderType getRenderTypeForTexture(String texture, boolean translucent, boolean itemRender) {
        if (translucent || texture.contains("trans") || texture.contains("glass") || texture.contains("flux") || texture.contains("/fluid/")) {
            return getTranslucentRenderType(itemRender);
        }
        return getCutoutRenderType(itemRender);
    }

    private static RenderType getCutoutRenderType(boolean itemRender) {
        return itemRender ? RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS) : Sheets.cutoutBlockSheet();
    }

    private static RenderType getTranslucentRenderType(boolean itemRender) {
        return itemRender ? RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS) : Sheets.translucentBlockSheet();
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
