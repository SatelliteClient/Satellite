package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventFlag;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class ElytraFly extends Module {

    public ElytraFly() {
        super("ElytraFly", 0, Category.MOVEMENT);
    }

    BooleanSetting autoClose;

    @Override
    public void init() {
        this.autoClose = new BooleanSetting("AutoClose", false);
        addSetting(autoClose);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventFlag) {
            EventFlag event = (EventFlag)e;
            if (event.isOutgoing() && event.flag == 7 && event.entity == mc.player.getEntityId()) {
                event.setSet(false);
            }
        }
        if(e instanceof EventUpdate) {
            if (mc.player.onGround) {
                PlayerUtils.vClip(.42);
            }else {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                mc.player.motionY = 0;
            }
        }
        super.onEvent(e);
    }

    @Override
    public void onDisable() {
        if (autoClose.isEnable()) {
            PlayerUtils.vClip2(0, true);
        }else {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }
        super.onDisable();
    }
}