package com.github.satellite.setting;

import java.util.function.Supplier;

public class Setting <T> {

	public String name;
	public T value;
	public final Supplier<Boolean> visibility;

	public Setting(String name, Supplier<Boolean> visibility, T value) {
		this.name = name;
		this.visibility = visibility;
		this.value = value;
	}

}
