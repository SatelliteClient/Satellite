package com.github.satellite.network.packet.packets;

import com.github.satellite.network.packet.Packet;

public class SPChangeBlockAction extends Packet {

	public int x, y, z, action;

	@Override
	public void readPacket(String str) {
		String[] pos = str.split(",");
		this.x = Integer.parseInt(pos[0]);
		this.y = Integer.parseInt(pos[1]);
		this.z = Integer.parseInt(pos[2]);
		this.action = Integer.parseInt(pos[3]);
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

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
}
