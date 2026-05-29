package com.leclowndu93150.dynamiducts.client.renderer;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.uv.IconTransformation;
import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.core.attachment.Attachment;
import com.leclowndu93150.dynamiducts.core.attachment.ConnectionBase;
import com.leclowndu93150.dynamiducts.core.duct.DuctToken;
import com.leclowndu93150.dynamiducts.duct.fluid.FluidDuctUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class DuctBlockEntityRenderer implements BlockEntityRenderer<DuctBlockEntity> {

    private static final Map<String, DuctTextures> DUCT_TEX = new HashMap<>();
    private static final String REDSTONE_STILL = "fluid/redstone_still";
    private static final String GLOWSTONE_STILL = "fluid/glowstone_still";
    private static final String CRYOTHEUM_STILL = "fluid/cryotheum_still";
    private static final Translation HALF_TRANSLATION = new Translation(0.5, 0.5, 0.5);
    private final BlockRenderDispatcher blockRenderer;
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

    public DuctBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
        DuctModels.init();
    }

    @Override
    public void render(DuctBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        String ductName = ductNameCache.computeIfAbsent(state.getBlock(),
                b -> b.builtInRegistryHolder().key().location().getPath());
        DuctTextures tex = DUCT_TEX.getOrDefault(ductName, DUCT_TEX.get("structural_duct"));
        boolean itemRender = isItemRender(be);

        if (tex == null) {
            return;
        }

        int connectionMask = getConnectionMask(be, state);
        Translation trans = HALF_TRANSLATION;

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.baseColour = 0xFFFFFFFF;

        try {
            renderBase(ccrs, bufferSource, poseStack, trans, tex, connectionMask, state, be, itemRender);
            if (!tex.opaque) {
                renderDecorativeOverlays(ccrs, bufferSource, poseStack, trans, tex, connectionMask, be, itemRender);
                renderFluidContents(be, ccrs, bufferSource, poseStack, trans, connectionMask, itemRender);
            }
            renderAttachments(be, ccrs, bufferSource, poseStack, trans);
        } catch (Exception e) {
            DynamiDucts.LOG.error("[DuctBESR] Exception during render at {}", be.getBlockPos(), e);
        }
    }

    private void renderBase(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack,
                             Translation trans, DuctTextures tex, int connectionMask,
                             BlockState state, DuctBlockEntity be, boolean itemRender) {
        if (tex.base != null) {
            TextureAtlasSprite baseSprite = getSprite("block/duct/base/" + tex.base);
            IconTransformation baseIcon = new IconTransformation(baseSprite);
            ccrs.bind(getBaseRenderType(tex, itemRender), bufferSource, poseStack);
            (tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask])
                    .render(ccrs, trans, baseIcon);
        }

        if (tex.fluid != null && tex.fluidAlpha == 255) {
            renderTinted(ccrs, bufferSource, poseStack, DuctModels.modelFluidTubes[connectionMask], trans,
                    resolveSpritePath(tex.fluid), 255, itemRender);
        }

        if (tex.overlay != null) {
            TextureAtlasSprite overlaySprite = getSprite("block/duct/base/" + tex.overlay);
            IconTransformation overlayIcon = new IconTransformation(overlaySprite);
            CCModel overlayModel = tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask];
            ccrs.bind(getCutoutRenderType(itemRender), bufferSource, poseStack);
            ccrs.baseColour = 0xFFFFFFFF;
            overlayModel.render(ccrs, trans, overlayIcon);
        }

        if (itemRender && tex.base != null) {
            TextureAtlasSprite endCapSprite = getSprite("block/duct/base/structure_trans");
            IconTransformation endCapIcon = new IconTransformation(endCapSprite);
            ccrs.bind(getCutoutRenderType(true), bufferSource, poseStack);
            for (Direction dir : Direction.values()) {
                if ((connectionMask & (1 << dir.ordinal())) != 0) {
                    DuctModels.modelConnection[0][dir.ordinal()].render(ccrs, 4, 8, trans, endCapIcon);
                }
            }
        }

        if (tex.connection != null && !itemRender) {
            TextureAtlasSprite connSprite = getSprite("block/duct/connection/" + tex.connection);
            IconTransformation connIcon = new IconTransformation(connSprite);
            ccrs.bind(getCutoutRenderType(false), bufferSource, poseStack);

            for (Direction dir : Direction.values()) {
                if (shouldRenderConnectionDetail(be, state, dir)) {
                    DuctModels.modelConnection[1][dir.ordinal()].render(ccrs, trans, connIcon);
                }
            }
        }

        renderFrameBase(ccrs, bufferSource, poseStack, trans, tex, connectionMask, state, be, itemRender);
    }

    private void renderFrameBase(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack, Translation trans,
                                 DuctTextures tex, int connectionMask, BlockState state, DuctBlockEntity be, boolean itemRender) {
        if (tex.frameType == FrameType.NONE || tex.frame == null) {
            return;
        }

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, bufferSource, poseStack, trans, connectionMask, false, "block/duct/side_ducts", itemRender);
            case FRAME -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (!isConnected(be, state, dir)) {
                        continue;
                    }
                    if (shouldRenderConnectionDetail(be, state, dir) && bandIcon != null) {
                        ccrs.bind(getCutoutRenderType(itemRender), bufferSource, poseStack);
                        DuctModels.modelFrameConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                        ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), bufferSource, poseStack);
                        DuctModels.modelFrame[70 + dir.ordinal()].render(ccrs, trans, frameIcon);
                    }
                }

                if (DuctModels.modelFrameConnection[connectionMask].verts.length != 0) {
                    ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), bufferSource, poseStack);
                    DuctModels.modelFrameConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case LARGE -> {
                ccrs.bind(getRenderTypeForTexture(tex.frame, false, itemRender), bufferSource, poseStack);
                DuctModels.modelLargeTubes[connectionMask].render(ccrs, trans, new IconTransformation(getSprite("block/duct/base/" + tex.frame)));
            }
            case TRANSPORT -> {
                TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + tex.frame);
                TextureAtlasSprite bandSprite = tex.frameBand == null ? null : getSprite("block/duct/base/" + tex.frameBand);
                IconTransformation frameIcon = new IconTransformation(frameSprite);
                IconTransformation bandIcon = bandSprite == null ? null : new IconTransformation(bandSprite);

                for (Direction dir : Direction.values()) {
                    if (shouldRenderConnectionDetail(be, state, dir) && bandIcon != null) {
                        ccrs.bind(getCutoutRenderType(itemRender), bufferSource, poseStack);
                        DuctModels.modelTransportConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
                    }
                }

                if (DuctModels.modelTransportConnection[connectionMask].verts.length != 0) {
                    ccrs.bind(getRenderTypeForTexture(tex.frame, true, itemRender), bufferSource, poseStack);
                    DuctModels.modelTransportConnection[connectionMask].render(ccrs, trans, frameIcon);
                }
            }
            case NONE -> {
            }
        }
    }

    private void renderDecorativeOverlays(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack,
                                          Translation trans, DuctTextures tex, int connectionMask, DuctBlockEntity be, boolean itemRender) {
        if (tex.fluid != null && tex.fluidAlpha > 0 && tex.fluidAlpha < 255) {
            renderTinted(ccrs, bufferSource, poseStack, DuctModels.modelFluidTubes[connectionMask], trans,
                    resolveSpritePath(tex.fluid), tex.fluidAlpha, itemRender);
        }

        if (tex.frameFluid == null || tex.frameFluidAlpha <= 0) {
            return;
        }

        switch (tex.frameType) {
            case SIDE -> renderSideTubes(ccrs, bufferSource, poseStack, trans, connectionMask, true, "block/duct/base/" + tex.frameFluid, tex.frameFluidAlpha, itemRender);
            case FRAME -> {
                TextureAtlasSprite frameFluidSprite = getSprite(resolveSpritePath(tex.frameFluid));
                IconTransformation frameFluidIcon = new IconTransformation(frameFluidSprite);
                ccrs.bind(getRenderTypeForTexture(resolveSpritePath(tex.frameFluid), true, itemRender), bufferSource, poseStack);
                ccrs.baseColour = rgba(255, 255, 255, tex.frameFluidAlpha);

                for (Direction dir : Direction.values()) {
                    if (isConnected(be, be.getBlockState(), dir) && shouldRenderConnectionDetail(be, be.getBlockState(), dir)) {
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
                ccrs.bind(getRenderTypeForTexture(resolveSpritePath(tex.frameFluid), true, itemRender), bufferSource, poseStack);
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

    private void renderFluidContents(DuctBlockEntity be, CCRenderState ccrs, MultiBufferSource bufferSource,
                                     PoseStack poseStack, Translation trans, int connectionMask, boolean itemRender) {
        var energyUnit = be.getDuctUnit(DuctToken.ENERGY);

        var fluidUnit = be.getDuctUnit(DuctToken.FLUID);
        if (fluidUnit instanceof FluidDuctUnit fdu && fdu.isTransparent()) {
            FluidStack fluid = fdu.getVisualFluid();
            int level = fdu.getVisualFluidLevel();
            if (!fluid.isEmpty() && level > 0) {
                IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
                ResourceLocation stillTex = fluidExt.getStillTexture(fluid);
                if (stillTex != null) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTex);
                    IconTransformation icon = new IconTransformation(sprite);
                    int color = argbToRgba(fluidExt.getTintColor(fluid));

                    ccrs.bind(getTranslucentRenderType(itemRender), bufferSource, poseStack);
                    ccrs.baseColour = color;
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
            }
        }
    }

    private void renderSideTubes(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack,
                                 Translation trans, int connectionMask, boolean inner, String spritePath, boolean itemRender) {
        renderSideTubes(ccrs, bufferSource, poseStack, trans, connectionMask, inner, spritePath, 255, itemRender);
    }

    private void renderSideTubes(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack,
                                 Translation trans, int connectionMask, boolean inner, String spritePath, int alpha, boolean itemRender) {
        CCModel[] models = inner ? DuctModels.modelSideTubesInner : DuctModels.modelSideTubes;
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);

        ccrs.bind(getRenderTypeForTexture(spritePath, inner || alpha < 255, itemRender), bufferSource, poseStack);
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        if (models[connectionMask].verts.length != 0) {
            models[connectionMask].render(ccrs, trans, icon);
        }
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private void renderTinted(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack, CCModel model,
                              Translation trans, String spritePath, int alpha, boolean itemRender) {
        TextureAtlasSprite sprite = getSprite(spritePath);
        IconTransformation icon = new IconTransformation(sprite);
        ccrs.bind(getRenderTypeForTexture(spritePath, alpha < 255, itemRender), bufferSource, poseStack);
        ccrs.baseColour = alpha == 255 ? 0xFFFFFFFF : rgba(255, 255, 255, alpha);
        model.render(ccrs, trans, icon);
        ccrs.baseColour = 0xFFFFFFFF;
    }

    private void renderAttachments(DuctBlockEntity be, CCRenderState ccrs, MultiBufferSource bufferSource,
                                    PoseStack poseStack, Translation trans) {
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

            TextureAtlasSprite sprite = getSprite(texPath);
            IconTransformation icon = new IconTransformation(sprite);

            ccrs.bind(getCutoutRenderType(false), bufferSource, poseStack);
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
                Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(ResourceLocation.fromNamespaceAndPath(DynamiDucts.MODID, p)));
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
