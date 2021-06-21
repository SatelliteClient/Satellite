package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntityPlayerSP;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class ElytraFly extends Module {

    public ElytraFly() {
        super("ElytraFly", 0, Category.MOVEMENT);
    }

    ModeSetting mode;
    BooleanSetting autoClose;
    BooleanSetting packet;
    BooleanSetting fly;

    @Override
    public void init() {
        this.mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Packet");
        this.autoClose = new BooleanSetting("AutoClose", false);
        this.packet = new BooleanSetting("Packet", false);
        this.fly = new BooleanSetting("float", false);
        addSetting(mode, autoClose, packet, fly);
        super.init();
    }

    private boolean serverFlyingStat;
    private boolean flyFlag;

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventFlag) {
            EventFlag event = (EventFlag)e;
            if (packet.isEnable() && event.isOutgoing() && event.flag == 7 && event.entity == mc.player.getEntityId()) {
                event.setSet(false);
            }
        }

        if (e instanceof EventHandleTeleport && e.isPost()) {
            serverFlyingStat = false;
            flyFlag = true;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }

        if(e instanceof EventUpdate) {
            if (mc.player.onGround) {
                MovementUtils.vClip2(.42, false);
                MovementUtils.vClip2(.42 - .08, false);
            }else {
                if (mode.is("Packet")) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                }else {
                    if (mc.player.onGround)
                        flyFlag = false;
                    if (!((AccessorEntityPlayerSP)mc.player).getServerSneakState() || !serverFlyingStat || flyFlag) {
                        serverFlyingStat = true;
                        ((AccessorEntityPlayerSP)mc.player).setServerSneakState(true);
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    }

                    if (mc.player.ticksExisted%20==0) {
                        ((AccessorEntityPlayerSP)mc.player).setServerSneakState(false);
                    }
                }
                if (fly.isEnable())
                    mc.player.motionY = 0;
            }
        }
        if (e instanceof EventMotion && packet.isEnable()) {
            EventMotion event = (EventMotion)e;
            if (mode.is("Vanilla") && !flyFlag) {
                event.y += .42;
                event.onGround = false;
            }
        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        if (autoClose.isEnable()) {
            MovementUtils.vClip2(0, true);
        }else {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }

        ((AccessorEntityPlayerSP)mc.player).setServerSneakState(mc.player.isSneaking());
        if (mc.player.isSneaking())
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        else
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        super.onDisable();
    }
}