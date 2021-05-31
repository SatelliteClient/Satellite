package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import net.minecraft.network.play.client.*;
import org.lwjgl.input.Keyboard;

public class PacketCanceller extends Module {

	public PacketCanceller() {
		super("PacketCanceller", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	BooleanSetting[] allowPacket;
	
	@Override
	public void init() {
		allowPacket = new BooleanSetting[] {
				new BooleanSetting("ConfirmTeleport", true),
				new BooleanSetting("ChatMessage", true),
				new BooleanSetting("ConfirmTransaction", true),
				new BooleanSetting("UseEntity", true),
				new BooleanSetting("KeepAlive", true),
				new BooleanSetting("Player", true),
				new BooleanSetting("VehicleMove", true),
				new BooleanSetting("PlayerAbilities", true),
				new BooleanSetting("PlayerDigging", true),
				new BooleanSetting("EntityAction", true),
				new BooleanSetting("Input", true),
				new BooleanSetting("TryUseItemOnBlock", true),
				new BooleanSetting("TryUseItem", true)
		};
		addSetting(allowPacket);
		super.init();
	}
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			if(event.getPacket() instanceof CPacketConfirmTeleport && allowPacket[0].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketChatMessage && allowPacket[1].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketConfirmTransaction && allowPacket[2].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketUseEntity && allowPacket[3].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketKeepAlive && allowPacket[4].isEnable())
				event.setCancelled(true);
			if((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Rotation) && allowPacket[5].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketVehicleMove && allowPacket[6].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketPlayerAbilities && allowPacket[7].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketPlayerDigging && allowPacket[8].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketEntityAction && allowPacket[9].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketInput && allowPacket[10].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && allowPacket[11].isEnable())
				event.setCancelled(true);
			if(event.getPacket() instanceof CPacketPlayerTryUseItem && allowPacket[12].isEnable())
				event.setCancelled(true);
		}
		super.onEvent(e);
	}
	
}
