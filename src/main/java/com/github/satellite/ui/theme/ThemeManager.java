package com.github.satellite.ui.theme;

import com.github.satellite.Satellite;
import com.github.satellite.ui.theme.themes.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class ThemeManager {

	public CopyOnWriteArrayList<Theme> themes = new CopyOnWriteArrayList<Theme>();
	
	public int index=0;
	
	public ThemeManager() {
		themes.add(new QuietCry());
		themes.add(new DarkTheme());
		themes.add(new BlueSky());
		themes.add(new CorporateBlew());
		themes.add(new DeepBlueSomething());
	}
	
	public Theme getCurrentTheme() {
		return themes.get(index);
	}
	
	public boolean setTheme(String name) {
		for(Theme theme : themes) {
			if(theme.getName() == name) {
				this.index = themes.indexOf(theme);
				return true;
			}
		}
		return false;
	}
	
	public static Theme getTheme() {
		return Satellite.themeManager.getCurrentTheme();
	}
	
	
}
