package com.leclowndu93150.dynamiducts;

import com.leclowndu93150.dynamiducts.client.renderer.DuctBlockEntityRenderer;
import com.leclowndu93150.dynamiducts.client.renderer.DuctBlockItemRenderer;
import com.leclowndu93150.dynamiducts.client.renderer.ItemDuctRenderer;
import com.leclowndu93150.dynamiducts.client.renderer.CoverItemRenderer;
import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.block.DuctHitHelper;
import com.leclowndu93150.dynamiducts.item.CoverItem;
import com.leclowndu93150.dynamiducts.init.DDBlockEntities;
import com.leclowndu93150.dynamiducts.init.DDBlocks;
import com.leclowndu93150.dynamiducts.init.DDCreativeTab;
import com.leclowndu93150.dynamiducts.init.DDDataComponents;
import com.leclowndu93150.dynamiducts.init.DDAttachments;
import com.leclowndu93150.dynamiducts.init.DDItems;
import com.leclowndu93150.dynamiducts.init.DDDataAttachments;
import com.leclowndu93150.dynamiducts.init.DDEntityTypes;
import com.leclowndu93150.dynamiducts.init.DDMenuTypes;
import com.leclowndu93150.dynamiducts.init.DDRecipeSerializers;
import com.leclowndu93150.dynamiducts.network.payload.AttachmentConfigPayload;
import com.leclowndu93150.dynamiducts.network.payload.ItemTravelSyncPayload;
import com.leclowndu93150.dynamiducts.network.payload.RelayConfigPayload;
import com.leclowndu93150.dynamiducts.network.payload.TransportRenamePayload;
import com.leclowndu93150.dynamiducts.network.payload.TransportRequestPayload;
import com.leclowndu93150.dynamiducts.screen.AttachmentScreen;
import com.leclowndu93150.dynamiducts.screen.RelayScreen;
import com.leclowndu93150.dynamiducts.screen.TransportConfigScreen;
import com.leclowndu93150.dynamiducts.screen.TransportScreen;
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

@Mod(DynamiDucts.MODID)
public class DynamiDucts {

    public static final String MODID = "dynamiducts";
    public static final Logger LOG = LogUtils.getLogger();
    private static final float TD_COVER_PREVIEW_ALPHA = 80.0F / 255.0F;

    public DynamiDucts(IEventBus modEventBus, ModContainer modContainer) {
        DDAttachments.bootstrap();

        DDBlocks.BLOCKS.register(modEventBus);
        DDDataComponents.DATA_COMPONENTS.register(modEventBus);
        DDItems.ITEMS.register(modEventBus);
        DDBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        DDMenuTypes.MENU_TYPES.register(modEventBus);
        DDEntityTypes.ENTITY_TYPES.register(modEventBus);
        DDDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
        DDCreativeTab.CREATIVE_TABS.register(modEventBus);
        DDRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

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
            event.register(DDMenuTypes.ATTACHMENT_MENU.get(), AttachmentScreen::new);
            event.register(DDMenuTypes.RELAY_MENU.get(), RelayScreen::new);
            event.register(DDMenuTypes.TRANSPORT_MENU.get(), TransportScreen::new);
            event.register(DDMenuTypes.TRANSPORT_CONFIG_MENU.get(), TransportConfigScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_BASIC, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_HARDENED, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_REINFORCED, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SIGNALUM, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_RESONANT, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_REINFORCED_EMPTY, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SIGNALUM_EMPTY, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_RESONANT_EMPTY, false);
            ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, false);

            ductRenderer(event, DDBlockEntities.FLUID_DUCT_BASIC, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_BASIC_OPAQUE, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_HARDENED, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_HARDENED_OPAQUE, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_ENERGY, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_SUPER, false);
            ductRenderer(event, DDBlockEntities.FLUID_DUCT_SUPER_OPAQUE, false);

            ductRenderer(event, DDBlockEntities.ITEM_DUCT_BASIC, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_BASIC_OPAQUE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_FAST, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_FAST_OPAQUE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_FAST, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_DENSE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_DENSE_OPAQUE, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_VACUUM, true);
            ductRenderer(event, DDBlockEntities.ITEM_DUCT_VACUUM_OPAQUE, true);

            ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_BASIC, false);
            ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_LONG_RANGE, false);
            ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_LINKING, false);
            ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_FRAME, false);

            ductRenderer(event, DDBlockEntities.STRUCTURAL_DUCT, false);
            ductRenderer(event, DDBlockEntities.LUX_DUCT, false);

            event.registerEntityRenderer(DDEntityTypes.TRANSPORT.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
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
                    DDItems.ENERGY_DUCT_BASIC,
                    DDItems.ENERGY_DUCT_HARDENED,
                    DDItems.ENERGY_DUCT_REINFORCED,
                    DDItems.ENERGY_DUCT_SIGNALUM,
                    DDItems.ENERGY_DUCT_RESONANT,
                    DDItems.ENERGY_DUCT_SUPERCONDUCTOR,
                    DDItems.ENERGY_DUCT_REINFORCED_EMPTY,
                    DDItems.ENERGY_DUCT_SIGNALUM_EMPTY,
                    DDItems.ENERGY_DUCT_RESONANT_EMPTY,
                    DDItems.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY,
                    DDItems.FLUID_DUCT_BASIC,
                    DDItems.FLUID_DUCT_BASIC_OPAQUE,
                    DDItems.FLUID_DUCT_HARDENED,
                    DDItems.FLUID_DUCT_HARDENED_OPAQUE,
                    DDItems.FLUID_DUCT_ENERGY,
                    DDItems.FLUID_DUCT_ENERGY_OPAQUE,
                    DDItems.FLUID_DUCT_SUPER,
                    DDItems.FLUID_DUCT_SUPER_OPAQUE,
                    DDItems.ITEM_DUCT_BASIC,
                    DDItems.ITEM_DUCT_BASIC_OPAQUE,
                    DDItems.ITEM_DUCT_FAST,
                    DDItems.ITEM_DUCT_FAST_OPAQUE,
                    DDItems.ITEM_DUCT_ENERGY,
                    DDItems.ITEM_DUCT_ENERGY_OPAQUE,
                    DDItems.ITEM_DUCT_ENERGY_FAST,
                    DDItems.ITEM_DUCT_ENERGY_FAST_OPAQUE,
                    DDItems.ITEM_DUCT_DENSE,
                    DDItems.ITEM_DUCT_DENSE_OPAQUE,
                    DDItems.ITEM_DUCT_VACUUM,
                    DDItems.ITEM_DUCT_VACUUM_OPAQUE,
                    DDItems.TRANSPORT_DUCT_BASIC,
                    DDItems.TRANSPORT_DUCT_LONG_RANGE,
                    DDItems.TRANSPORT_DUCT_LINKING,
                    DDItems.TRANSPORT_DUCT_FRAME,
                    DDItems.STRUCTURAL_DUCT,
                    DDItems.LUX_DUCT
            );

            event.registerItem(new IClientItemExtensions() {
                @Override
                public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return CoverItemRenderer.get();
                }
            }, DDItems.COVER);
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
            var ductBE = blockEntity instanceof com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity duct ? duct : null;
            var hit = DuctHitHelper.resolve(state, ductBE, event.getTarget().getBlockPos(), event.getTarget());
            var box = DuctHitHelper.outlineBox(hit, ductBE, state.getBlock() instanceof DuctBlock db && db.getShapeCache() == DuctBlock.SHAPE_LARGE);
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
