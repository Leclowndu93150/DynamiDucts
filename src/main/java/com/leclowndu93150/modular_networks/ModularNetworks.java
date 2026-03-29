package com.leclowndu93150.modular_networks;

import com.leclowndu93150.modular_networks.client.renderer.DuctBlockEntityRenderer;
import com.leclowndu93150.modular_networks.client.renderer.DuctBlockItemRenderer;
import com.leclowndu93150.modular_networks.client.renderer.ItemDuctRenderer;
import com.leclowndu93150.modular_networks.client.renderer.CoverItemRenderer;
import com.leclowndu93150.modular_networks.block.DuctBlock;
import com.leclowndu93150.modular_networks.block.DuctHitHelper;
import com.leclowndu93150.modular_networks.item.CoverItem;
import com.leclowndu93150.modular_networks.init.MNBlockEntities;
import com.leclowndu93150.modular_networks.init.MNBlocks;
import com.leclowndu93150.modular_networks.init.MNCreativeTab;
import com.leclowndu93150.modular_networks.init.MNDataComponents;
import com.leclowndu93150.modular_networks.init.MNAttachments;
import com.leclowndu93150.modular_networks.init.MNItems;
import com.leclowndu93150.modular_networks.init.MNDataAttachments;
import com.leclowndu93150.modular_networks.init.MNEntityTypes;
import com.leclowndu93150.modular_networks.init.MNMenuTypes;
import com.leclowndu93150.modular_networks.init.MNRecipeSerializers;
import com.leclowndu93150.modular_networks.network.payload.AttachmentConfigPayload;
import com.leclowndu93150.modular_networks.network.payload.ItemTravelSyncPayload;
import com.leclowndu93150.modular_networks.network.payload.RelayConfigPayload;
import com.leclowndu93150.modular_networks.network.payload.TransportRenamePayload;
import com.leclowndu93150.modular_networks.network.payload.TransportRequestPayload;
import com.leclowndu93150.modular_networks.screen.AttachmentScreen;
import com.leclowndu93150.modular_networks.screen.RelayScreen;
import com.leclowndu93150.modular_networks.screen.TransportConfigScreen;
import com.leclowndu93150.modular_networks.screen.TransportScreen;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(ModularNetworks.MODID)
public class ModularNetworks {

    public static final String MODID = "modular_networks";
    public static final Logger LOG = LogUtils.getLogger();
    private static final float TD_COVER_PREVIEW_ALPHA = 80.0F / 255.0F;

    public ModularNetworks(IEventBus modEventBus, ModContainer modContainer) {
        MNAttachments.bootstrap();

        MNBlocks.BLOCKS.register(modEventBus);
        MNDataComponents.DATA_COMPONENTS.register(modEventBus);
        MNItems.ITEMS.register(modEventBus);
        MNBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MNMenuTypes.MENU_TYPES.register(modEventBus);
        MNEntityTypes.ENTITY_TYPES.register(modEventBus);
        MNDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
        MNCreativeTab.CREATIVE_TABS.register(modEventBus);
        MNRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, MNConfig.SPEC);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ItemTravelSyncPayload.TYPE, ItemTravelSyncPayload.STREAM_CODEC, ItemTravelSyncPayload::handle);
        registrar.playToServer(AttachmentConfigPayload.TYPE, AttachmentConfigPayload.STREAM_CODEC, AttachmentConfigPayload::handle);
        registrar.playToServer(RelayConfigPayload.TYPE, RelayConfigPayload.STREAM_CODEC, RelayConfigPayload::handle);
        registrar.playToServer(TransportRenamePayload.TYPE, TransportRenamePayload.STREAM_CODEC, TransportRenamePayload::handle);
        registrar.playToServer(TransportRequestPayload.TYPE, TransportRequestPayload.STREAM_CODEC, TransportRequestPayload::handle);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MNMenuTypes.ATTACHMENT_MENU.get(), AttachmentScreen::new);
            event.register(MNMenuTypes.RELAY_MENU.get(), RelayScreen::new);
            event.register(MNMenuTypes.TRANSPORT_MENU.get(), TransportScreen::new);
            event.register(MNMenuTypes.TRANSPORT_CONFIG_MENU.get(), TransportConfigScreen::new);
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

            event.registerEntityRenderer(MNEntityTypes.TRANSPORT.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        }

        @SubscribeEvent
        public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
            IClientItemExtensions ductExtensions = new IClientItemExtensions() {
                @Override
                public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return DuctBlockItemRenderer.get();
                }
            };

            event.registerItem(ductExtensions,
                    MNItems.ENERGY_DUCT_BASIC,
                    MNItems.ENERGY_DUCT_HARDENED,
                    MNItems.ENERGY_DUCT_REINFORCED,
                    MNItems.ENERGY_DUCT_SIGNALUM,
                    MNItems.ENERGY_DUCT_RESONANT,
                    MNItems.ENERGY_DUCT_SUPERCONDUCTOR,
                    MNItems.ENERGY_DUCT_REINFORCED_EMPTY,
                    MNItems.ENERGY_DUCT_SIGNALUM_EMPTY,
                    MNItems.ENERGY_DUCT_RESONANT_EMPTY,
                    MNItems.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY,
                    MNItems.FLUID_DUCT_BASIC,
                    MNItems.FLUID_DUCT_BASIC_OPAQUE,
                    MNItems.FLUID_DUCT_HARDENED,
                    MNItems.FLUID_DUCT_HARDENED_OPAQUE,
                    MNItems.FLUID_DUCT_ENERGY,
                    MNItems.FLUID_DUCT_ENERGY_OPAQUE,
                    MNItems.FLUID_DUCT_SUPER,
                    MNItems.FLUID_DUCT_SUPER_OPAQUE,
                    MNItems.ITEM_DUCT_BASIC,
                    MNItems.ITEM_DUCT_BASIC_OPAQUE,
                    MNItems.ITEM_DUCT_FAST,
                    MNItems.ITEM_DUCT_FAST_OPAQUE,
                    MNItems.ITEM_DUCT_ENERGY,
                    MNItems.ITEM_DUCT_ENERGY_OPAQUE,
                    MNItems.ITEM_DUCT_ENERGY_FAST,
                    MNItems.ITEM_DUCT_ENERGY_FAST_OPAQUE,
                    MNItems.TRANSPORT_DUCT_BASIC,
                    MNItems.TRANSPORT_DUCT_LONG_RANGE,
                    MNItems.TRANSPORT_DUCT_LINKING,
                    MNItems.TRANSPORT_DUCT_FRAME,
                    MNItems.STRUCTURAL_DUCT,
                    MNItems.LUX_DUCT
            );

            event.registerItem(new IClientItemExtensions() {
                @Override
                public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return CoverItemRenderer.get();
                }
            }, MNItems.COVER);
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

            var player = Minecraft.getInstance().player;
            if (player == null || ductBE == null) {
                return;
            }

            var heldStack = player.getMainHandItem();
            BlockState coverState = heldStack.getItem() instanceof CoverItem ? CoverItem.getCoverState(heldStack) : null;
            if (coverState == null || hit.part() == DuctHitHelper.HitPart.COVER || ductBE.getAttachment(hit.side()) != null) {
                return;
            }

            event.getPoseStack().pushPose();
            event.getPoseStack().translate(
                    blockPos.getX() - cameraPos.x,
                    blockPos.getY() - cameraPos.y,
                    blockPos.getZ() - cameraPos.z
            );
            DuctBlockEntityRenderer.renderCoverPreview(
                    coverState,
                    Minecraft.getInstance().level,
                    blockPos,
                    hit.side(),
                    event.getPoseStack(),
                    event.getMultiBufferSource(),
                    LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos),
                    OverlayTexture.NO_OVERLAY,
                    TD_COVER_PREVIEW_ALPHA
            );
            event.getPoseStack().popPose();
        }
    }
}
