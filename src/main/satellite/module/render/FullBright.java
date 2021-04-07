package satellite.module.render;

import org.lwjgl.input.Keyboard;

import satellite.event.Event;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;

public class FullBright extends Module {

	public FullBright() {
		super("FullBright", Keyboard.KEY_C, Category.RENDER);
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting=1;
		super.onDisable();
	}

	@Override
	public void onEnable() {
		mc.gameSettings.gammaSetting=100;
		super.onEnable();
	}
	
}
