package com.github.satellite.features.test.botting.listeners;

import com.github.satellite.features.test.botting.BotEvent;

public class Goto extends BotEvent {
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int x, y, z;

	public Goto(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
}
