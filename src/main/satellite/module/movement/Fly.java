package satellite.module.movement;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		super.onDisable();
	}

	@Override
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				mc.player.capabilities.isFlying = true;
			}
		}
		super.onEvent(e);
	}
	
}
