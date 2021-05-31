package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventType;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends EntityPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void PreUpdateWalkingPlayer(CallbackInfo ci) {
        EventUpdate e = new EventUpdate();
        e.setType(EventType.PRE);
        Satellite.onEvent(e);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void PostUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion e = new EventMotion(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        e.setType(EventType.PRE);
        Satellite.onEvent(e);
    }
}
