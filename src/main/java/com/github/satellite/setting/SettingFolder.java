package com.github.satellite.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingFolder extends Setting {

	public List<Setting> settings = new ArrayList<Setting>();
	boolean isExpanded;

	public SettingFolder(String name, boolean expanded, Setting... setting) {
		this.name = name;
		this.settings = Arrays.asList(setting);
		this.isExpanded = expanded;
	}
	
	public void addSetting(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}
	
	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
}
