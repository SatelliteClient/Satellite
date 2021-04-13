package satellite.futures.module;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import satellite.event.Event;
import satellite.setting.Setting;
import satellite.setting.KeyBindSetting;
import satellite.utils.PlayerUtil;

public class Module {

	protected Minecraft mc = Minecraft.getMinecraft();
	
	public Category category;
	public KeyBindSetting keyBindSetting;
	public String name;
	public boolean enable;
	
	public List<Setting> settings = new ArrayList<Setting>();
	
	public Module(String name, Category category) {
		this.name = name;
		this.category = category;
		init();
	}
	
	public Module(String name, int keyCode, Category category) {
		this.name = name;
		this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
		this.settings.add(keyBindSetting);
		this.category = category;
		init();
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
	public void onEvent(Event e) {}
	
	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
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
