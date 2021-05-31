package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {

	public Velocity() {
		super("Velocity", Keyboard.KEY_K, Category.PLAYER);
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPacket) {
			EventPacket event = ((EventPacket)e);
			if(event.isIncoming()) {
				Packet<?> p = event.getPacket();
				
				if(p instanceof SPacketEntityVelocity) {
					event.setCancelled(true);
				}
				if(p instanceof SPacketExplosion) {
					event.setCancelled(true);
				}
			}
		}
		super.onEvent(e);
	}
	
}
