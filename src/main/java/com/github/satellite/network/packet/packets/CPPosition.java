package com.github.satellite.network.packet.packets;

import com.github.satellite.network.packet.Packet;

public class CPPosition extends Packet {

	int x, y, z;
	
	public CPPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	
	@Override
	public void writePacket() {
		this.data="00";
		this.data+=String.valueOf(x);
		this.data+=",";
		this.data+=String.valueOf(y);
		this.data+=",";
		this.data+=String.valueOf(z);
		super.writePacket();
	}
}
