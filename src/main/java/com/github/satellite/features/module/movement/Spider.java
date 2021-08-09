package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import org.lwjgl.input.Keyboard;

public class Spider extends Module {

    public Spider () {
        super("Spider", Keyboard.KEY_NONE, Category.MOVEMENT);
    }
    
    @Override
    public void init() {
		settings.add(new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "Packet"}));
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}