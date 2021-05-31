package com.github.satellite.event.listeners;

import com.github.satellite.network.packet.Packet;
import com.github.satellite.event.Event;

public class EventRecieveSateNet extends Event<EventRecieveSateNet> {

	Packet data;

	public EventRecieveSateNet(Packet packetIn) {
		this.data = packetIn;
	}

	public Packet getData() {
		return data;
	}

	public void setData(Packet data) {
		this.data = data;
	}
}
