package com.github.satellite.features.module;

import com.github.satellite.event.Event;
import com.github.satellite.features.module.render.ClickGUI;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

	protected Minecraft mc = Minecraft.getMinecraft();
	
	public Category category;
	public KeyBindSetting keyBindSetting;
	public String name;
	public String displayName;
	public boolean enable;

	public int priority;
	
	public List<Setting> settings = new ArrayList<Setting>();
	
	public Module(String name, Category category) {
		this.name = name;
		this.category = category;
		init();
	}
	
	public Module(String name, int keyCode, Category category) {
		if (this instanceof ClickGUI) {
			this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
		}else {
			this.keyBindSetting = new KeyBindSetting("KeyBind", 0);
		}
		this.name = name;
		this.settings.add(keyBindSetting);
		this.category = category;
		this.priority = 0;
		init();
	}
	
	public Module(String name, int keyCode, Category category, boolean enable) {
		this(name, keyCode, category);
		this.enable = enable;
	}

	public Module(String name, int keyCode, Category category, int priority) {
		this(name, keyCode, category);
		this.priority = priority;
	}

	public void addSetting(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getKeyCode() {
		return keyBindSetting.getKeyCode();
	}

	public void setKeyCode(int keyCode) {
		this.keyBindSetting.setKeyCode(keyCode);
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getDisplayName() {
		return displayName == null ? name : displayName;
	}
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void toggle() {
		enable = !enable;
		if(enable) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void init() {}
	public void onEnable() {}
	public void onDisable() {}
	public void onEvent(Event<?> e) {}
	
	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		MISC("Misc"),
		PLAYER("Player"),
		RENDER("Render"),
		EXPLOIT("Exploit"),
		WORLD("World"),
		OTHER("Other");
		
		public String name;
		
		Category(String name) {
			this.name=name;
		}
	}
	
}
