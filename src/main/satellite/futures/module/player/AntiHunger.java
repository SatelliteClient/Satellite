package satellite.futures.module.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import satellite.event.Event;
import satellite.event.listeners.EventRecievePacket;
import satellite.event.listeners.EventSendingPacket;
import satellite.event.listeners.EventUpdate;
import satellite.futures.module.Module;
import satellite.utils.PlayerUtil;

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
	public void onEvent(Event e) {
		if(e instanceof EventSendingPacket) {
			EventSendingPacket event = ((EventSendingPacket)e);
			Packet p = event.getPacket();
			
			if(p instanceof CPacketEntityAction) {
				CPacketEntityAction packet = (CPacketEntityAction)p;
				if(
						packet.getAction() == CPacketEntityAction.Action.START_SPRINTING ||
						packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING
				) {
					event.setCansellSending(true);
				}
			}
		}
		super.onEvent(e);
	}
	
}
