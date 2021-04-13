package satellite.event.listeners;

import net.minecraft.network.Packet;
import satellite.event.Event;

public class EventSendingPacket extends Event<EventSendingPacket> {
	
	public Packet packet;
	boolean cansellSending;
	
	public EventSendingPacket(Packet packet) {
		this.packet = packet;
		this.cansellSending = false;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public boolean isCansellSending() {
		return cansellSending;
	}

	public void setCansellSending(boolean cansellSending) {
		this.cansellSending = cansellSending;
	}
}
