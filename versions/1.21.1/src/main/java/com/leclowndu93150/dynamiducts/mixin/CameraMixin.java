package com.leclowndu93150.dynamiducts.mixin;

import com.leclowndu93150.dynamiducts.duct.transport.TransportEntity;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow
    private Entity entity;

    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void modularNetworks$skipClipInTransport(float distance, CallbackInfoReturnable<Float> cir) {
        if (entity != null && entity.getVehicle() instanceof TransportEntity) {
            cir.setReturnValue(distance);
        }
    }
}
