package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventLightingUpdate;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
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
	NumberSetting maxLightHeight;
	
	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "Map", new String[] {"Light", "Map", "Clear Entity", "BlockChange"});
		this.maxLightHeight = new NumberSetting("LightHeight", 255, 0, 255, 1);
		addSetting(mode, maxLightHeight);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		switch (mode.getMode()) {

			case "Lighit":
				if (e instanceof EventLightingUpdate) {
					if (((EventLightingUpdate)e).getPos().getY() > maxLightHeight.value) {
						e.cancel();
					}
				}

		}
		super.onEvent(e);
	}
}
