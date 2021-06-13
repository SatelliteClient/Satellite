package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import org.lwjgl.input.Keyboard;

public class Ground extends Module {

    public Ground() {
        super("Ground", Keyboard.KEY_NONE, Category.PLAYER);
    }

    BooleanSetting ground;
    ModeSetting type;

    @Override
    public void init() {
        this.ground = new BooleanSetting("Ground", true);
        this.type = new ModeSetting("Ground", "Pre", "Pre", "Post");
        addSetting(ground, type);
        super.init();
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventMotion) {
            if (type.getMode().equals("Pre") && e.isPre())
                mc.player.onGround = ground.isEnable();
            else if (e.isPost()) mc.player.onGround = ground.isEnable();
        }
        super.onEvent(e);
    }

}
