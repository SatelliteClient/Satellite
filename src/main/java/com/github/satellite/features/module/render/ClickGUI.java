package com.github.satellite.features.module.render;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.ui.gui.clickGUI.GuiClickGUI;
import com.github.satellite.ui.theme.Theme;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Module {

	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT,	Category.RENDER);
	}

	public ModeSetting theme;
	public static BooleanSetting autoGuiScale;

	@Override
	public void init() {
		List<String> mode = new ArrayList<String>();
		for(Theme theme : Satellite.themeManager.themes) {
			mode.add(theme.getName());
		}
		String[] modes = mode.toArray(new String[mode.size()]);
		this.theme = new ModeSetting("Theme", mode.get(0), modes);
		this.autoGuiScale = new BooleanSetting("AutoResize", true);
		addSetting(theme, autoGuiScale);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRenderGUI) {
			Satellite.themeManager.setTheme(theme.getMode());
			mc.displayGuiScreen(new GuiClickGUI(0));
		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(new GuiClickGUI(0));
		super.onEnable();
	}
}
