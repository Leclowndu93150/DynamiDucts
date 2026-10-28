package com.leclowndu93150.dynamiducts.client;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.duct.transport.TransportEntity;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Pose;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID, value = Dist.CLIENT)
public class TransportClientHandler {

    private static final ContextKey<Direction> TRANSPORT_DIRECTION = new ContextKey<>(Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "transport_direction"));

    private static CameraType previousCameraType;
    private static boolean wasRiding;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        LocalPlayer player = mc.player;
        boolean riding = player.getVehicle() instanceof TransportEntity;

        if (riding && !wasRiding) {
            previousCameraType = mc.options.getCameraType();
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        } else if (!riding && wasRiding) {
            if (previousCameraType != null) {
                mc.options.setCameraType(previousCameraType);
                previousCameraType = null;
            }
        }
        wasRiding = riding;

        if (player.getVehicle() instanceof TransportEntity transport) {
            player.setSwimming(true);
            player.setPose(Pose.SWIMMING);

            float t = transport.getProgress() / 100F;
            Direction dir = t < 0.5F ? transport.getOldDirection() : transport.getDirection();
            float targetYaw = directionToYaw(dir);
            float targetPitch = directionToPitch(dir);

            player.setYRot(lerpAngle(player.getYRot(), targetYaw, 0.3F));
            player.setXRot(lerpAngle(player.getXRot(), targetPitch, 0.3F));
            player.yHeadRot = player.getYRot();
            player.yBodyRot = player.getYRot();
        }
    }

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                if (avatar.getVehicle() instanceof TransportEntity transport) {
                    avatar.setSwimming(true);
                    avatar.setPose(Pose.SWIMMING);
                    renderState.pose = Pose.SWIMMING;
                    renderState.isVisuallySwimming = true;

                    float t = transport.getProgress() / 100F;
                    renderState.setRenderData(TRANSPORT_DIRECTION, t < 0.5F ? transport.getOldDirection() : transport.getDirection());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Direction dir = event.getRenderState().getRenderData(TRANSPORT_DIRECTION);
        if (dir == Direction.UP || dir == Direction.DOWN) {
            float pitch = dir == Direction.UP ? -90F : 90F;
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(0, 0.4, 0);
            event.getPoseStack().mulPose(Axis.XP.rotationDegrees(pitch));
            event.getPoseStack().translate(0, -0.4, 0);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Direction dir = event.getRenderState().getRenderData(TRANSPORT_DIRECTION);
        if (dir == Direction.UP || dir == Direction.DOWN) {
            event.getPoseStack().popPose();
        }
    }

    private static float directionToYaw(Direction dir) {
        return switch (dir) {
            case NORTH -> 180F;
            case SOUTH -> 0F;
            case WEST -> 90F;
            case EAST -> -90F;
            case UP, DOWN -> 0F;
        };
    }

    private static float directionToPitch(Direction dir) {
        return switch (dir) {
            case UP -> -90F;
            case DOWN -> 90F;
            default -> 0F;
        };
    }

    private static float lerpAngle(float current, float target, float factor) {
        float diff = target - current;
        while (diff > 180F) diff -= 360F;
        while (diff < -180F) diff += 360F;
        return current + diff * factor;
    }
}
