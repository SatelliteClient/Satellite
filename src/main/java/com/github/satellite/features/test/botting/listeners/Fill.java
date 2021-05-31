package com.github.satellite.features.test.botting.listeners;

import com.github.satellite.features.test.botting.BotEvent;
import net.minecraft.util.math.BlockPos;

public class Fill extends BotEvent {
	
	public int x1, y1, z1, x2, y2, z2, block;

	public Fill(int x1, int y1, int z1, int x2, int y2, int z2, int block) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.block = block;
	}

	public BlockPos getFill1() {
		return new BlockPos(x1, y1, z1);
	}
	public BlockPos getFill2() {
		return new BlockPos(x2, y2, z2);
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getZ1() {
		return z1;
	}

	public void setZ1(int z1) {
		this.z1 = z1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public int getZ2() {
		return z2;
	}

	public void setZ2(int z2) {
		this.z2 = z2;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}
}