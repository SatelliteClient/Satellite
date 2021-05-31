package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import org.lwjgl.input.Keyboard;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", Keyboard.KEY_K,	Category.RENDER);
	}

	float lastGamma;

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		mc.gameSettings.gammaSetting = 10E+3F;
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		lastGamma = mc.gameSettings.gammaSetting;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting = lastGamma;
		super.onDisable();
	}
}
