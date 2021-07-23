package com.github.satellite.setting;

import java.util.function.Supplier;

public class KeyBindSetting extends Setting {

	public int keyCode;

	public KeyBindSetting(String name, Supplier<Boolean> visibility, int keyCode) {
		super(name, visibility, keyCode);
		this.name = name;
		this.keyCode = keyCode;
	}

	public KeyBindSetting(String name, int keyCode) {
		super(name, null, keyCode);
		this.name = name;
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

}
