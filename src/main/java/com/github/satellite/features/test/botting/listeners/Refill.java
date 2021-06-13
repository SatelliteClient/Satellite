package com.github.satellite.features.test.botting.listeners;

import com.github.satellite.features.test.botting.BotEvent;
import net.minecraft.util.math.BlockPos;

public class Refill extends BotEvent {

	public int x, y, z;

	public Refill(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

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
	
	public double getDistance(BlockPos pos) {
		double dx = x - pos.getX(), dz = z - pos.getZ();
		return Math.sqrt(dx*dx+dz*dz);
	}
}
