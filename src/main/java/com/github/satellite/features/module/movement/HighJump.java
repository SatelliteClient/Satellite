package com.github.satellite.features.module.movement;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntityPlayer;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class HighJump extends Module {

    public HighJump() {
        super("HighJump", 0, Category.MOVEMENT);
    }

    @Override
    public void init() {
        settings.add(new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "NCP"}));
        super.init();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onEvent(Event<?> e) {
        switch(((ModeSetting)settings.get(1)).getMode()) {

            case "NCP":
                if (e instanceof EventMotion && e.isPre()) {
                    double[] ncp = new double[] {0.425D, 0.821D, 0.699D, 0.599D};

                    if(mc.player.posY - mc.player.lastTickPosY == 0.42) {
                        mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
                        for (double d : ncp) {
                            PlayerUtils.vClip2(d, false);
                        }
                        PlayerUtils.vClip(.599D);
                        mc.player.jump();
                    }
                }
        }
        super.onEvent(e);
    }
}