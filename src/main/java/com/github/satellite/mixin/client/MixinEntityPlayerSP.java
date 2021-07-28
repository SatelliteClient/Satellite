package com.github.satellite.mixin.client;

import com.github.satellite.Satellite;
import com.github.satellite.event.EventType;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow @Final NetHandlerPlayClient connection;
    @Shadow boolean serverSprintState;
    @Shadow boolean serverSneakState;
    @Shadow double lastReportedPosX;
    @Shadow double lastReportedPosY;
    @Shadow double lastReportedPosZ;
    @Shadow float lastReportedYaw;
    @Shadow float lastReportedPitch;
    @Shadow int positionUpdateTicks;
    @Shadow boolean prevOnGround;
    @Shadow boolean autoJumpEnabled;
    @Shadow Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {super(worldIn, playerProfile);}

    @Shadow protected abstract boolean isCurrentViewEntity();

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        EventUpdate e = new EventUpdate();
        e.setType(EventType.PRE);
        Satellite.onEvent(e);
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void preCheckLightFor(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "HEAD"), cancellable = true)
    private void PreUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        event.setType(EventType.PRE);
        Satellite.onEvent(event);

        if (event.isModded()) {
            ci.cancel();
            sendMovePacket(event);
            System.out.println("Move Modded!");
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "RETURN"), cancellable = true)
    private void PostUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        event.setType(EventType.POST);
        Satellite.onEvent(event);
    }

    public void sendMovePacket(EventMotion event) {
        boolean flag = this.isSprinting();

        if (flag != this.serverSprintState) {
            if (flag) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking();

        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity()) {
            double d0 = event.x - this.lastReportedPosX;
            double d1 = event.y - this.lastReportedPosY;
            double d2 = event.z - this.lastReportedPosZ;
            double d3 = (double) (event.yaw - this.lastReportedYaw);
            double d4 = (double) (event.pitch - this.lastReportedPitch);
            ++this.positionUpdateTicks;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (this.isRiding()) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, event.yaw, event.pitch, event.onGround));
                flag2 = false;
            } else if (flag2 && flag3) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
            } else if (flag2) {
                this.connection.sendPacket(new CPacketPlayer.Position(event.x, event.y, event.z, event.onGround));

            } else if (flag3) {
                this.connection.sendPacket(new CPacketPlayer.Rotation(event.yaw, event.pitch, event.onGround));
            } else if (this.prevOnGround != event.onGround) {
                this.connection.sendPacket(new CPacketPlayer(event.onGround));
            }

            if (flag2) {
                this.lastReportedPosX = event.x;
                this.lastReportedPosY = event.y;
                this.lastReportedPosZ = event.z;
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = event.yaw;
                this.lastReportedPitch = event.pitch;
            }

            this.prevOnGround = event.onGround;
            this.autoJumpEnabled = this.mc.gameSettings.autoJump;
        }
    }
}
