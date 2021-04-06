package satellite.module.movement;

import org.lwjgl.input.Keyboard;

import satellite.module.Module;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}

	
	
}
