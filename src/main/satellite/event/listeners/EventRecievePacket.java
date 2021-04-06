package satellite.event.listeners;
import net.minecraft.network.Packet;
import satellite.event.Event;

public class EventRecievePacket extends Event<EventRecievePacket> {

	Packet packet;
	
	public EventRecievePacket(Packet packetIn) {
		this.packet = packetIn;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}
}
