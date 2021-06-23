package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventLightingUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void preCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        if (Satellite.onEvent(new EventLightingUpdate(pos, lightType)).isCancelled()) {
            cir.cancel();
        }
    }
}