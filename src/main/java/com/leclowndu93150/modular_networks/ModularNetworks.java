package com.leclowndu93150.modular_networks;

import com.leclowndu93150.modular_networks.client.renderer.DuctBlockEntityRenderer;
import com.leclowndu93150.modular_networks.client.renderer.ItemDuctRenderer;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.init.MNBlockEntities;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNCreativeTab;
import com.leclowndu93150.modular_networks.init.MNAttachments;
import com.leclowndu93150.modular_networks.init.MNItems;
import com.leclowndu93150.modular_networks.init.MNMenuTypes;
import com.leclowndu93150.modular_networks.init.MNRecipeSerializers;
import com.leclowndu93150.modular_networks.network.payload.AttachmentConfigPayload;
import com.leclowndu93150.modular_networks.screen.AttachmentScreen;
import com.leclowndu93150.modular_networks.screen.RelayScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(ModularNetworks.MODID)
public class ModularNetworks {

    public static final String MODID = "modular_networks";
    public static final Logger LOG = LogUtils.getLogger();

    public ModularNetworks(IEventBus modEventBus, ModContainer modContainer) {
        MNAttachments.bootstrap();

        MNBlocks.BLOCKS.register(modEventBus);
        MNItems.ITEMS.register(modEventBus);
        MNBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MNMenuTypes.MENU_TYPES.register(modEventBus);
        MNCreativeTab.CREATIVE_TABS.register(modEventBus);
        MNRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, MNConfig.SPEC);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(AttachmentConfigPayload.TYPE, AttachmentConfigPayload.STREAM_CODEC, AttachmentConfigPayload::handle);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MNMenuTypes.ATTACHMENT_MENU.get(), AttachmentScreen::new);
            event.register(MNMenuTypes.RELAY_MENU.get(), RelayScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_BASIC, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_HARDENED, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_REINFORCED, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_SIGNALUM, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_RESONANT, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_REINFORCED_EMPTY, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_SIGNALUM_EMPTY, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_RESONANT_EMPTY, false);
            ductRenderer(event, MNBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, false);

            ductRenderer(event, MNBlockEntities.FLUID_DUCT_BASIC, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_BASIC_OPAQUE, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_HARDENED, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_HARDENED_OPAQUE, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_ENERGY, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_ENERGY_OPAQUE, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_SUPER, false);
            ductRenderer(event, MNBlockEntities.FLUID_DUCT_SUPER_OPAQUE, false);

            ductRenderer(event, MNBlockEntities.ITEM_DUCT_BASIC, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_BASIC_OPAQUE, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_FAST, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_FAST_OPAQUE, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_ENERGY, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_ENERGY_OPAQUE, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_ENERGY_FAST, true);
            ductRenderer(event, MNBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE, true);

            ductRenderer(event, MNBlockEntities.TRANSPORT_DUCT_BASIC, false);
            ductRenderer(event, MNBlockEntities.TRANSPORT_DUCT_LONG_RANGE, false);
            ductRenderer(event, MNBlockEntities.TRANSPORT_DUCT_LINKING, false);
            ductRenderer(event, MNBlockEntities.TRANSPORT_DUCT_FRAME, false);

            ductRenderer(event, MNBlockEntities.STRUCTURAL_DUCT, false);
            ductRenderer(event, MNBlockEntities.LUX_DUCT, false);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void ductRenderer(EntityRenderersEvent.RegisterRenderers event,
                                          DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder,
                                          boolean itemDuct) {
            BlockEntityType type = holder.get();
            if (itemDuct) {
                event.registerBlockEntityRenderer(type, ItemDuctRenderer::new);
            } else {
                event.registerBlockEntityRenderer(type, DuctBlockEntityRenderer::new);
            }
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderMultipartHighlight(RenderHighlightEvent.Block event) {
            if (Minecraft.getInstance().level == null) {
                return;
            }

            BlockState state = Minecraft.getInstance().level.getBlockState(event.getTarget().getBlockPos());
            if (!(state.getBlock() instanceof DuctBlock)) {
                return;
            }

            var blockEntity = Minecraft.getInstance().level.getBlockEntity(event.getTarget().getBlockPos());
            var ductBE = blockEntity instanceof com.leclowndu93150.modular_networks.blockentity.DuctBlockEntity duct ? duct : null;
            var hit = DuctHitHelper.resolve(state, ductBE, event.getTarget().getBlockPos(), event.getTarget());
            var box = DuctHitHelper.outlineBox(hit, ductBE);
            var cameraPos = event.getCamera().getPosition();
            var blockPos = event.getTarget().getBlockPos();
            var builder = event.getMultiBufferSource().getBuffer(RenderType.lines());

            event.setCanceled(true);
            LevelRenderer.renderLineBox(
                    event.getPoseStack(),
                    builder,
                    box.move(
                            blockPos.getX() - cameraPos.x,
                            blockPos.getY() - cameraPos.y,
                            blockPos.getZ() - cameraPos.z
                    ),
                    0.0F,
                    0.0F,
                    0.0F,
                    0.4F
            );
        }
    }
}
