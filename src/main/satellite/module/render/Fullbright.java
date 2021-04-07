package satellite.module.render;

import org.lwjgl.input.Keyboard;

import satellite.module.Category;
import satellite.module.Module;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", Keyboard.KEY_K,	Category.RENDER);
	}

	float lastGamma;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRecievePacket) {
		          mc.gameSettings.gammaSetting = 10E+3F;
            }
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
