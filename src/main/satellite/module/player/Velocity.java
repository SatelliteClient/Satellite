package satellite.module.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import satellite.event.Event;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;

public class Velocity extends Module {

	public Velocity() {
		super("Velocity", Keyboard.KEY_K, Category.RENDER);
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventRecievePacket) {
			EventRecievePacket event = ((EventRecievePacket)e);
			Packet packet = event.getPacket();
			
			if(packet instanceof SPacketEntityVelocity) {
				event.setCansellReading(true);
			}
		}
		super.onEvent(e);
	}
	
}
