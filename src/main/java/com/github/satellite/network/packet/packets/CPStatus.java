package com.github.satellite.network.packet.packets;

import com.github.satellite.network.packet.Packet;

public class CPStatus extends Packet {

	int status;
	
	public CPStatus(Status status) {
		this.status = status.index;
	}
	
	@Override
	public void writePacket() {
		this.data="01";
		this.data+=String.valueOf(status);
		super.writePacket();
	}
	
	enum Status {
		DONELINE(0),
		DONEMOVING(1),
		FAILEDLINE(2),
		FAILEDMOVING(3);
		
		public int index;
		
		Status(int i) {
			this.index = i;
		}
	}
}
