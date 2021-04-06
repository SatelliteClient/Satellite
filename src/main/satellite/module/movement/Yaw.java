package satellite.module.movement;

import org.lwjgl.input.Keyboard;

import satellite.module.Category;
import satellite.module.Module;

public class Yaw extends Module{

  float yaw = 0;
  
	public Yaw() {
		super("Yaw", Keyboard.KEY_NUMPAD4, Category.MOVEMENT);
	}

  @Override
  public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre()) {{
        mc.player.rotationYaw = yaw;
			}
		}
		super.onEvent(e);
	}
}
