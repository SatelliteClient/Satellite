package com.github.satellite.setting;

public class BooleanSetting extends Setting {

	public boolean enable;
	
	public BooleanSetting(String name, boolean enable) {
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
