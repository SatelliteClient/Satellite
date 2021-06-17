package com.github.satellite.utils;

import net.minecraft.client.Minecraft;

public class TimeHelper {
	
	private long lastMS;

	public TimeHelper() {
		this.lastMS = this.getCurrentMS();
	}

	public long getCurrentMS() {
		return Minecraft.getSystemTime() / 1000000L;
	}

	public void update() {
		this.lastMS = this.getCurrentMS();
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
