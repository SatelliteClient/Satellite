package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.features.module.Module;
import org.lwjgl.input.Keyboard;

public class Yaw extends Module {
	
	float yaw = 0;
	
	public Yaw() {
		super("Yaw", Keyboard.KEY_NUMPAD4, Category.MOVEMENT);
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPlayerInput) {
			mc.player.rotationYaw += ((float) ( (Math.floor( ( mc.player.rotationYaw + 22.5) / 45 ) * 45)) - mc.player.rotationYaw) / 3;
			super.onEvent(e);
		}
	}
}
