package satellite.futures.module.render;

import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import satellite.event.Event;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.futures.module.ModuleManager;
import satellite.ui.clickGUI.element.Category;

public class ClickGUI extends Module {
	
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT,	Category.RENDER);
	}
	
	@Override
	public void onEnable() {
		mc.displayGuiScreen(new satellite.ui.clickGUI.ClickGUI());
		super.onEnable();
	}
}
