package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventDirection;
import com.github.satellite.event.EventType;
import com.github.satellite.event.listeners.EventHandleTeleport;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow Minecraft client;
    @Shadow NetworkManager netManager;
    @Shadow boolean doneLoadingTerrain;

    @Inject(method = "handlePlayerPosLook", at = @At("HEAD"), cancellable = true)
    public void handlePlayerPosLook(SPacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        EventHandleTeleport e = new EventHandleTeleport(packetIn);
        e.setDirection(EventDirection.INCOMING);
        e.setType(EventType.PRE);
        Satellite.onEvent(e);

        if (e.isCancellTeleporting() || e.isCancelled()) {
            ci.cancel();
            EntityPlayer entityplayer = this.client.player;
            double d0 = packetIn.getX();
            double d1 = packetIn.getY();
            double d2 = packetIn.getZ();
            float f = packetIn.getYaw();
            float f1 = packetIn.getPitch();

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
            {
                d0 += entityplayer.posX;
            }
            else
            {
                entityplayer.motionX = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
            {
                d1 += entityplayer.posY;
            }
            else
            {
                entityplayer.motionY = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
            {
                d2 += entityplayer.posZ;
            }
            else
            {
                entityplayer.motionZ = 0.0D;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
            {
                f1 += entityplayer.rotationPitch;
            }

            if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
            {
                f += entityplayer.rotationYaw;
            }

            this.netManager.sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
            this.netManager.sendPacket(new CPacketPlayer.PositionRotation(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

            if (!this.doneLoadingTerrain)
            {
                this.client.player.prevPosX = this.client.player.posX;
                this.client.player.prevPosY = this.client.player.posY;
                this.client.player.prevPosZ = this.client.player.posZ;
                this.doneLoadingTerrain = true;
                this.client.displayGuiScreen((GuiScreen)null);
            }
        }
    }

    @Inject(method = "handlePlayerPosLook", at = @At("RETURN"))
    public void PostHandlePlayerPosLook(SPacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        EventHandleTeleport e = new EventHandleTeleport(packetIn);
        e.setDirection(EventDirection.INCOMING);
        e.setType(EventType.POST);
        Satellite.onEvent(e);
    }
}
