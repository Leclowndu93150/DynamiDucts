package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.CCRenderState;
import com.leclowndu93150.dynamiducts.client.renderer.ccl.Translation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = DynamiDucts.MODID, value = Dist.CLIENT)
public class DuctSectionRenderer {

    private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
    private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
    private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, -1.0F, 0.7F).normalize();

    public static boolean isBaked(DuctBlockEntity be) {
        return be.getLevel() != null && be.getLevel() == Minecraft.getInstance().level;
    }

    @SubscribeEvent
    public static void onAddSectionGeometry(AddSectionGeometryEvent event) {
        Level level = event.getLevel();
        BlockPos origin = event.getSectionOrigin();
        int sectionY = SectionPos.blockToSectionCoord(origin.getY());

        List<BakedDuct> ducts = new ArrayList<>();
        for (BlockEntity blockEntity : level.getChunk(SectionPos.blockToSectionCoord(origin.getX()), SectionPos.blockToSectionCoord(origin.getZ()))
                .getBlockEntities().values()) {
            if (blockEntity instanceof DuctBlockEntity duct && SectionPos.blockToSectionCoord(duct.getBlockPos().getY()) == sectionY) {
                ducts.add(new BakedDuct(duct.getBlockPos(), DuctBlockEntityRenderer.extract(duct)));
            }
        }
        if (ducts.isEmpty()) {
            return;
        }

        Vector3f light1 = level instanceof ClientLevel clientLevel && clientLevel.effects().constantAmbientLight()
                ? NETHER_DIFFUSE_LIGHT_1 : DIFFUSE_LIGHT_1;
        event.addRenderer(context -> renderSection(context, ducts, light1));
    }

    private static void renderSection(AddSectionGeometryEvent.SectionRenderingContext context, List<BakedDuct> ducts, Vector3f light1) {
        DuctModels.init();
        CCRenderState ccrs = CCRenderState.instance();
        DuctBlockEntityRenderer.DuctBuffers buffers = (state, translucent) -> {
            RenderType type = translucent ? RenderType.translucent() : RenderType.cutout();
            state.bind(context.getOrCreateChunkBuffer(type), type.format());
        };

        for (BakedDuct duct : ducts) {
            BlockPos pos = duct.pos();
            ccrs.reset();
            ccrs.brightness = LevelRenderer.getLightColor(context.getRegion(), pos);
            ccrs.setDiffuseLighting(DIFFUSE_LIGHT_0, light1);
            Translation trans = new Translation((pos.getX() & 15) + 0.5, (pos.getY() & 15) + 0.5, (pos.getZ() & 15) + 0.5);
            DuctBlockEntityRenderer.renderDuct(ccrs, buffers, trans, duct.data());
        }
    }

    private record BakedDuct(BlockPos pos, DuctRenderData data) {
    }
}
