package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventRenderWorld;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugView:Z")})
    private void renderWorldPassPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo paramCallbackInfo) {
        EventRenderWorld e = new EventRenderWorld(partialTicks);
        Satellite.onEvent(e);
    }
}
