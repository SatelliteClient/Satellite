package com.github.satellite.utils;

import org.lwjgl.Sys;

import net.minecraft.client.Minecraft;

public class TimeHelper {

	private long lastMS;

	public long getCurrentMS() {
		return Minecraft.getSystemTime();
	}

	public long getLastMS() {
		return this.lastMS;
	}

	public boolean hasReached(float f) {
		return (float)(this.getCurrentMS() - this.lastMS) >= f;
	}

	public boolean hasReached(double f) {
		return (double)(this.getCurrentMS() - this.lastMS) >= f;
	}

	public boolean hasReached(long f) {
		return (float)(this.getCurrentMS() - this.lastMS) >= (float)f;
	}

	public void reset() {
		this.lastMS = this.getCurrentMS();
	}

	public void setLastMS(long currentMS) {
		this.lastMS = currentMS;
	}
}
