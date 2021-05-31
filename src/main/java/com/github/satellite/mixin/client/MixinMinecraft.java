package com.github.satellite.mixin.client;

import com.github.satellite.features.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = {"shutdown"}, at = @At("HEAD"))
    public void shutdown(CallbackInfo ci)
    {
        ModuleManager.saveModuleSetting();
    }

}
