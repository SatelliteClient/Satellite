package com.github.satellite.features.module.misc;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventLightingUpdate;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorMapData;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.*;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import scala.collection.script.Update;

import java.util.Map;

public class AntiChunkBan extends Module {
	
	public AntiChunkBan() {
		super("AntiChunkBan", 0, Category.MISC);
	}
	
	BooleanSetting light;
	BooleanSetting map;
	NumberSetting maxLightHeight;
	
	@Override
	public void init() {
		this.map = new BooleanSetting("Map", true);
		this.light = new BooleanSetting("Light", true);
		this.maxLightHeight = new NumberSetting("LightHeight", 128, 0, 255, 1);
		addSetting(map, light, maxLightHeight);
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (light.isEnable()) {
			if (e instanceof EventLightingUpdate) {
				if (((EventLightingUpdate)e).getPos().getY() > maxLightHeight.value) {
					e.cancel();
				}
			}
		}

		if (map.isEnable()) {
			/**
			 * @author 34663
			 */
			if (e instanceof EventUpdate) {
				ItemStack currentItem = mc.player.inventory.getCurrentItem();
				if (!currentItem.isEmpty() && currentItem.getItem() instanceof ItemMap) {
					MapData mapData = ((ItemMap) currentItem.getItem()).getMapData(currentItem, mc.world);
					if (mapData != null) {
						this.getMapDecorations(mapData).clear();
					}
				}

				for (Entity entity : mc.world.loadedEntityList) {
					if (entity instanceof EntityItemFrame) {
						EntityItemFrame frame = (EntityItemFrame) entity;
						ItemStack frameItem = frame.getDisplayedItem();
						if (!frameItem.isEmpty() && frameItem.getItem() instanceof ItemMap) {
							MapData mapData = ((ItemMap) frameItem.getItem()).getMapData(frameItem, frame.world);
							if (mapData != null) {
								this.getMapDecorations(mapData).clear();
							}
						}
					}
				}
			}
			if (e instanceof EventPacket) {
				if (((EventPacket)e).getPacket() instanceof SPacketMaps) {
					if (e.isIncoming()) {
						e.cancel();
					}
				}
			}
		}
		super.onEvent(e);
	}

	public Map<String, MapDecoration> getMapDecorations(MapData mapData) {
		return ((AccessorMapData)mapData).getMapDecorations();
	}
}
