package satellite.module.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import satellite.event.Event;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventUpdate;
import satellite.module.Module;
import satellite.utils.PlayerUtil;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		super.onDisable();
	}

	double lastTickSpeed=0;
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			PlayerUtil player = new PlayerUtil(mc.player);
			player.freeze();
						
			player.vClip(mc.player.ticksExisted%2==0?1E-10:0);
			
			player.vClip(mc.player.onGround?0.42:0);
			player.Strafe(mc.player.onGround? 1 :lastTickSpeed<0.24?0.24:lastTickSpeed-lastTickSpeed/150);
			
			lastTickSpeed=mc.player.isCollidedHorizontally?0.24:player.getSpeed();
		}
		if(e instanceof EventRecievePacket) {
			EventRecievePacket event = ((EventRecievePacket)e);
			Packet packet = event.getPacket();
			System.out.print(packet+"\n");
		}
		super.onEvent(e);
	}
	
}
