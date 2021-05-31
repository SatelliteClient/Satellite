package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

public class AntiChunkBan extends Module {
	
	public AntiChunkBan() {
		super("AntiChunkBan", 0, Category.MISC);
	}
	
	ModeSetting mode;
	
	@Override
	public void init() {
		mode = new ModeSetting("Mode", "Map", new String[] {"Piston", "Map", "Clear Entity"});
		addSetting(mode);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		for(Entity entity : mc.world.loadedEntityList) if(entity != mc.player) mc.world.removeEntity(entity);
		if(e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if(event.isIncoming()) {
				switch (mode.getMode()) {
				case "Piston":
					if(p instanceof SPacketBlockChange) event.setCancelled(true);
					if(p instanceof SPacketMultiBlockChange) event.setCancelled(true);
					//locks.GLOWSTONE.lightValue=0;
				case "Map":
					if(p instanceof SPacketEntityMetadata) event.setCancelled(true);
					if(p instanceof SPacketBlockAction) event.setCancelled(true);
					break;
				case "Clear Entity":
					for(Entity entity : mc.world.loadedEntityList) if(entity != mc.player) mc.world.removeEntity(entity);
					break;
					

				default:
					break;
				}
			}
		}
		super.onEvent(e);
	}
}
