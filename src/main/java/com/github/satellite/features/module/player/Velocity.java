package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.PlayerUtils;
import ibxm.Player;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {

	public Velocity() {
		super("Velocity", Keyboard.KEY_K, Category.PLAYER);
	}

	BooleanSetting speed;

	@Override
	public void init() {
		this.speed = new BooleanSetting("VelocitySpeed", false);
		addSetting(speed);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (speed.isEnable()) {
			if(e instanceof EventPacket) {
				EventPacket event = ((EventPacket)e);
				if(event.isIncoming()) {
					Packet<?> p = event.getPacket();

					if(p instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)p).getEntityID() == mc.player.getEntityId()) {
						SPacketEntityVelocity velocity = (SPacketEntityVelocity)p;
						PlayerUtils.Strafe(Math.sqrt(Math.pow((double)velocity.getMotionX() / 8000.0D, 2) + Math.pow((double)velocity.getMotionZ() / 8000.0D, 2)));
						event.setCancelled(true);
					}
					if(p instanceof SPacketExplosion) {
						SPacketExplosion velocity = (SPacketExplosion)p;
						event.setCancelled(true);
					}
				}
			}
		}else {
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
		}
		super.onEvent(e);
	}
	
}
