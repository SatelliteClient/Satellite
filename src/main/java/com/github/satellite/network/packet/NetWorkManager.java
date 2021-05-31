package com.github.satellite.network.packet;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventRecieveSateNet;
import com.github.satellite.network.packet.packets.SPChangeBlockAction;
import com.github.satellite.network.packet.packets.SPFillBlocks;
import com.github.satellite.network.packet.packets.SPGoto;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class NetWorkManager {
	
	public List<Class<? extends Packet>> registedPacket = new ArrayList<Class<? extends Packet>>();

	public void registerPackets() {
		this.registedPacket.add(SPGoto.class);
		this.registedPacket.add(SPChangeBlockAction.class);
		this.registedPacket.add(SPFillBlocks.class);
	}

	public void readPacket(String str) {
		for(Class<? extends Packet> p : registedPacket) {
			if(registedPacket.indexOf(p)==Integer.parseInt(str.split("")[0]+str.split("")[1])) {
				Class<? extends Packet> p1 = registedPacket.get(Integer.parseInt(str.split("")[0]+str.split("")[1]));
				
				if(p1 == SPGoto.class) {
					SPGoto packet = new SPGoto();
					packet.readPacket(str.substring(2, str.length()));
					EventRecieveSateNet e = new EventRecieveSateNet(packet);
					Satellite.onEvent(e);
				}
				if(p1 == SPChangeBlockAction.class) {
					SPChangeBlockAction packet = new SPChangeBlockAction();
					packet.readPacket(str.substring(2, str.length()));
					EventRecieveSateNet e = new EventRecieveSateNet(packet);
					Satellite.onEvent(e);
				}
				if(p1 == SPFillBlocks.class) {
					SPFillBlocks packet = new SPFillBlocks();
					packet.readPacket(str.substring(2, str.length()));
					System.out.print(str);
					EventRecieveSateNet e = new EventRecieveSateNet(packet);
					Satellite.onEvent(e);
				}
			}
		}
	}
	
	public void sendPacket(Packet packetIn) {
		packetIn.writePacket();
		Satellite.SatelliteNet.conn.sendQueue.add(packetIn.getData());
	}
}
