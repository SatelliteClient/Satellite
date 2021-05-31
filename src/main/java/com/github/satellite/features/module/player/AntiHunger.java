package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import org.lwjgl.input.Keyboard;

public class AntiHunger extends Module {

	public AntiHunger() {
		super("AntiHunger", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Override
	public void onEnable() {
		mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(mc.player.isSprinting()) {
			mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
		}
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPacket) {
			EventPacket event = ((EventPacket)e);
			if(event.isOutgoing()) {
				Packet<?> p = event.getPacket();
				
				if(p instanceof CPacketEntityAction) {
					CPacketEntityAction packet = (CPacketEntityAction)p;
					if(
							packet.getAction() == CPacketEntityAction.Action.START_SPRINTING ||
							packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING
					) {
						event.setCancelled(true);
					}
				}
			}
		}
		super.onEvent(e);
	}
	
}
