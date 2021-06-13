package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventType;
import com.github.satellite.event.listeners.EventJump;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Shadow protected abstract boolean isPlayer();

    protected Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "jump", at = @At("HEAD"))
    private void onPreUpdate(CallbackInfo ci) {
        if (!this.isPlayer())
            return;
        EventJump e = new EventJump();
        e.setType(EventType.PRE);
        Satellite.onEvent(e);
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void onPostUpdate(CallbackInfo ci) {
        if (!this.isPlayer())
            return;
        EventJump e = new EventJump();
        e.setType(EventType.POST);
        Satellite.onEvent(e);
    }
}
