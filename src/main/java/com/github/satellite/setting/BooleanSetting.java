package com.github.satellite.setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {

	public boolean enable;

	public BooleanSetting(String name, Supplier<Boolean> visibility, boolean enable) {
		super(name, visibility, enable);
		this.name = name;
		this.enable = enable;
	}

	public BooleanSetting(String name, boolean enable) {
		super(name, null, enable);
		this.name = name;
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public void toggle() {
		enable = !enable;
	}
}
