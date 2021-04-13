package satellite.futures.module.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import satellite.event.Event;
import satellite.event.listeners.EventMotion;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventSendingPacket;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

public class NoFall extends Module {

	public NoFall() {
		super("NoFall", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			
			event.setOnGround(true);
		}
		super.onEvent(e);
	}
	
}
