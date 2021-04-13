package satellite.event.listeners;
import net.minecraft.network.Packet;
import satellite.event.Event;

public class EventRecievePacket extends Event<EventRecievePacket> {

	Packet packet;
	boolean cansellReading;
	
	public EventRecievePacket(Packet packetIn) {
		this.packet = packetIn;
		this.cansellReading = false;
	}

	public boolean isCansellReading() {
		return cansellReading;
	}

	public void setCansellReading(boolean cansellReading) {
		this.cansellReading = cansellReading;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}
}
