package com.leclowndu93150.modular_networks.client.renderer;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.uv.IconTransformation;
import com.leclowndu93150.modular_networks.ModularNetworks;
import com.leclowndu93150.modular_networks.attachment.cover.Cover;
import com.leclowndu93150.modular_networks.attachment.relay.Relay;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity;
import com.leclowndu93150.modular_networks.core.attachment.Attachment;
import com.leclowndu93150.modular_networks.core.attachment.ConnectionBase;
import com.leclowndu93150.modular_networks.core.duct.DuctToken;
import com.leclowndu93150.modular_networks.duct.energy.EnergyDuctUnit;
import com.leclowndu93150.modular_networks.duct.energy.EnergyGrid;
import com.leclowndu93150.modular_networks.duct.fluid.FluidDuctUnit;
import com.leclowndu93150.modular_networks.duct.fluid.FluidGrid;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class DuctBlockEntityRenderer implements BlockEntityRenderer<DuctBlockEntity> {

    private static final Map<String, DuctTextures> DUCT_TEX = new HashMap<>();

    static {
        reg("energy_duct_basic", "lead", "lead", "redstone_background", 255);
        reg("energy_duct_hardened", "invar", "invar", "redstone_background", 255);
        reg("energy_duct_reinforced", "electrum", "electrum", "redstone_background", 192);
        reg("energy_duct_signalum", "signalum", "signalum", "redstone_background", 192);
        reg("energy_duct_resonant", "enderium", "enderium", "redstone_background", 192);
        reg("energy_duct_superconductor", "enderium", "enderium", "redstone_background", 255);
        reg("energy_duct_reinforced_empty", "electrum", "electrum", null, 0);
        reg("energy_duct_signalum_empty", "signalum", "signalum", null, 0);
        reg("energy_duct_resonant_empty", "enderium", "enderium", null, 0);
        reg("energy_duct_superconductor_empty", "enderium", "enderium", null, 0);

        reg("fluid_duct_basic", "copper_trans", "copper", null, 0);
        reg("fluid_duct_basic_opaque", "copper", "copper", null, 0);
        reg("fluid_duct_hardened", "invar_trans", "invar", null, 0);
        reg("fluid_duct_hardened_opaque", "invar", "invar", null, 0);
        reg("fluid_duct_energy", "invar_signalum_trans", "invar", null, 0);
        reg("fluid_duct_energy_opaque", "invar_signalum", "invar", null, 0);
        reg("fluid_duct_super", "invar_trans", "invar", null, 0);
        reg("fluid_duct_super_opaque", "invar", "invar", null, 0);

        reg("item_duct_basic", "tin_trans", "tin", null, 0);
        reg("item_duct_basic_opaque", "tin", "tin", null, 0);
        reg("item_duct_fast", "tin_trans", "tin", null, 0);
        reg("item_duct_fast_opaque", "tin_alt", "tin", null, 0);
        reg("item_duct_energy", "tin_signalum_trans", "tin", null, 0);
        reg("item_duct_energy_opaque", "tin_signalum", "tin", null, 0);
        reg("item_duct_energy_fast", "tin_signalum_trans", "tin", null, 0);
        reg("item_duct_energy_fast_opaque", "tin_alt_signalum", "tin", null, 0);

        reg("transport_duct_basic", "copper", "copper", null, 0);
        reg("transport_duct_long_range", "lead", "lead", null, 0);
        reg("transport_duct_linking", "enderium", "enderium", null, 0);
        reg("transport_duct_frame", "copper", "copper", null, 0);

        reg("structural_duct", "structure", null, null, 0);
        reg("lux_duct", "lumium", null, null, 0);
    }

    private static void reg(String name, String base, String connection, String fluid, int fluidAlpha) {
        boolean opaque = !base.contains("trans");
        DUCT_TEX.put(name, new DuctTextures(base, connection, fluid, fluidAlpha, opaque));
    }

    public DuctBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        DuctModels.init();
    }

    @Override
    public void render(DuctBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        String ductName = state.getBlock().builtInRegistryHolder().key().location().getPath();
        DuctTextures tex = DUCT_TEX.getOrDefault(ductName, DUCT_TEX.get("structural_duct"));

        if (tex == null) {
            ModularNetworks.LOG.warn("[DuctBESR] No textures found for duct: {}", ductName);
            return;
        }

        int connectionMask = getConnectionMask(be, state);
        Translation trans = new Translation(0.5, 0.5, 0.5);

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        ccrs.baseColour = 0xFFFFFFFF;

        try {
            renderBase(ccrs, bufferSource, poseStack, trans, tex, connectionMask, state, be);
            renderFluidContents(be, ccrs, bufferSource, poseStack, trans, connectionMask, tex);
            renderAttachments(be, ccrs, bufferSource, poseStack, trans);
        } catch (Exception e) {
            ModularNetworks.LOG.error("[DuctBESR] Exception during render at {}", be.getBlockPos(), e);
        }
    }

    private void renderBase(CCRenderState ccrs, MultiBufferSource bufferSource, PoseStack poseStack,
                             Translation trans, DuctTextures tex, int connectionMask,
                             BlockState state, DuctBlockEntity be) {
        TextureAtlasSprite baseSprite = getSprite("block/duct/base/" + tex.base);
        IconTransformation baseIcon = new IconTransformation(baseSprite);
        ccrs.bind(getBaseRenderType(tex), bufferSource, poseStack);

        (tex.opaque ? DuctModels.modelOpaqueTubes[connectionMask] : DuctModels.modelTransTubes[connectionMask])
                .render(ccrs, trans, baseIcon);

        if (tex.fluid != null && tex.fluidAlpha == 255) {
            TextureAtlasSprite fluidSprite = getSprite("block/duct/base/" + tex.fluid);
            IconTransformation fluidIcon = new IconTransformation(fluidSprite);
            ccrs.bind(getTranslucentRenderType(), bufferSource, poseStack);
            DuctModels.modelFluidTubes[connectionMask].render(ccrs, trans, fluidIcon);
        }

        if (tex.connection != null) {
            TextureAtlasSprite connSprite = getSprite("block/duct/connection/" + tex.connection);
            IconTransformation connIcon = new IconTransformation(connSprite);
            ccrs.bind(getCutoutRenderType(), bufferSource, poseStack);

            for (Direction dir : Direction.values()) {
                if (!state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir))) continue;

                BlockPos neighborPos = be.getBlockPos().relative(dir);
                if (be.getLevel() != null && !(be.getLevel().getBlockState(neighborPos).getBlock() instanceof DuctBlock)) {
                    DuctModels.modelConnection[1][dir.ordinal()].render(ccrs, trans, connIcon);
                }
            }
        }
    }

    private void renderFluidContents(DuctBlockEntity be, CCRenderState ccrs, MultiBufferSource bufferSource,
                                      PoseStack poseStack, Translation trans, int connectionMask, DuctTextures tex) {
        if (tex.fluid != null && tex.fluidAlpha != 255 && tex.fluidAlpha > 0) {
            var energyUnit = be.getDuctUnit(DuctToken.ENERGY);
            if (energyUnit instanceof EnergyDuctUnit edu && edu.getGrid() != null && edu.getGrid().isPowered()) {
                TextureAtlasSprite fluidSprite = getSprite("block/duct/base/" + tex.fluid);
                IconTransformation fluidIcon = new IconTransformation(fluidSprite);

                ccrs.bind(getTranslucentRenderType(), bufferSource, poseStack);
                ccrs.baseColour = rgba(255, 255, 255, tex.fluidAlpha);
                DuctModels.modelFluidTubes[connectionMask].render(ccrs, trans, fluidIcon);
                ccrs.baseColour = 0xFFFFFFFF;
            }
        }

        var fluidUnit = be.getDuctUnit(DuctToken.FLUID);
        if (fluidUnit instanceof FluidDuctUnit fdu && fdu.isTransparent() && fdu.getGrid() != null) {
            FluidGrid grid = fdu.getGrid();
            FluidStack fluid = grid.getTank().getFluid();
            if (!fluid.isEmpty()) {
                IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
                ResourceLocation stillTex = fluidExt.getStillTexture(fluid);
                if (stillTex != null) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTex);
                    IconTransformation icon = new IconTransformation(sprite);
                    int color = argbToRgba(fluidExt.getTintColor(fluid));

                    ccrs.bind(getTranslucentRenderType(), bufferSource, poseStack);
                    ccrs.baseColour = color;
                    DuctModels.modelFluidTubes[connectionMask].render(ccrs, trans, icon);
                    ccrs.baseColour = 0xFFFFFFFF;
                }
            }
        }
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
                    model = DuctModels.modelConnection[isAttachmentPowered(be)][i];
                } else if (conn.isFilter()) {
                    texPath = "block/duct/attachment/filter/filter_" + tierIndex;
                    model = DuctModels.modelConnection[1][i];
                } else if (conn.isRetriever()) {
                    texPath = "block/duct/attachment/retriever/retriever_base_0_" + tierIndex;
                    model = DuctModels.modelConnection[isAttachmentPowered(be)][i];
                } else {
                    continue;
                }
            } else if (att instanceof Cover) {
                texPath = "block/duct/attachment/cover/cover_side";
                model = DuctModels.modelConnection[0][i];
            } else if (att instanceof Relay relay) {
                texPath = "block/duct/attachment/signallers/signaller";
                model = DuctModels.modelConnection[1 + (relay.getType() & 1)][i];
            } else {
                continue;
            }

            TextureAtlasSprite sprite = getSprite(texPath);
            IconTransformation icon = new IconTransformation(sprite);

            ccrs.bind(getCutoutRenderType(), bufferSource, poseStack);
            model.render(ccrs, trans, icon);
        }
    }

    protected int getConnectionMask(DuctBlockEntity be, BlockState state) {
        int mask = 0;
        for (Direction dir : Direction.values()) {
            boolean connected = state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir));
            Attachment attachment = be.getAttachment(dir);
            if (attachment != null && !(attachment instanceof Cover)) {
                connected = true;
            }
            if (connected) {
                mask |= (1 << dir.ordinal());
            }
        }
        return mask;
    }

    private TextureAtlasSprite getSprite(String path) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ResourceLocation.fromNamespaceAndPath(ModularNetworks.MODID, path));
    }

    private static RenderType getBaseRenderType(DuctTextures tex) {
        return tex.opaque ? getCutoutRenderType() : getTranslucentRenderType();
    }

    private static RenderType getCutoutRenderType() {
        return Sheets.cutoutBlockSheet();
    }

    private static RenderType getTranslucentRenderType() {
        return RenderTypeHelper.getEntityRenderType(RenderType.translucent(), false);
    }

    private static int isAttachmentPowered(DuctBlockEntity be) {
        return be.getLevel() != null && be.getLevel().hasNeighborSignal(be.getBlockPos()) ? 1 : 2;
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

    private record DuctTextures(String base, String connection, String fluid, int fluidAlpha, boolean opaque) {
    }
}
