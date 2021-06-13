package com.github.satellite.features.module.movement;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventJump;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;

public class HighJump extends Module {

    public HighJump() {
        super("HighJump", 0, Category.MOVEMENT);
    }

    ModeSetting mode;
    BooleanSetting useTimer;

    boolean inTimer;
    int state;

    @Override
    public void init() {
        this.mode = new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "NCP"});
        this.useTimer = new BooleanSetting("UseTimer", true);
        addSetting(mode, useTimer);
        super.init();
    }

    @Override
    public void onDisable() {
        if (inTimer) {
            ClientUtils.setTimer(1.0f);
            inTimer = false;
        }
        super.onDisable();
    }

    @Override
    public void onEvent(Event<?> e) {
        switch(mode.getMode()) {

            case "NCP":
                if (e instanceof EventUpdate) {
                    if (state > 0) {
                        state--;
                    } else if (inTimer) {
                        inTimer = false;
                        ClientUtils.setTimer(1);
                    }
                }
                if (e instanceof EventJump && e.isPre()) {
                    double[] ncp = new double[] {0.425D, 0.821D, 0.699D, 0.599D};
                    for (double d : ncp) {
                        PlayerUtils.vClip2(d, false);
                    }
                    if (useTimer.isEnable()) {
                        this.state = 1;
                        this.inTimer = true;
                        ClientUtils.setTimer(Math.min(1.0F, 1.0F / (1+ncp.length)));
                    }
                    PlayerUtils.vClip(.599D);
                    mc.player.motionY = .425D;
                }
        }
        super.onEvent(e);
    }
}