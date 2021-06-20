package com.github.satellite.features.module.movement;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventJump;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class AntiOutside extends Module {
	
	
	public AntiOutside() {
		super("AntiOutside", 0, Category.MOVEMENT);
	}
	
	NumberSetting offset;
	NumberSetting factor;
	NumberSetting offsetY;
	NumberSetting checkY;
	
	@Override
	public void init() {
		this.offset = new NumberSetting("offset", 0.005, Integer.MIN_VALUE, 10, 0.001);
		this.factor = new NumberSetting("factor", 100, 1, 10000, 1);
		this.offsetY = new NumberSetting("packetY", .5, -10, 10, .1);
		this.checkY = new NumberSetting("checkY", 5, 0, 10, 1);
		addSetting(offset, factor, offsetY, checkY);
		super.init();
	}
	
	@Override
	public void onEnable() {
		lastInSideStat = PlayerUtils.isInsideBlock();
		super.onEnable();
	}
	
	boolean lastInSideStat;
	
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventMotion && e.isPre()) {
			ClientUtils.setTimer(1);
			EventMotion event = (EventMotion)e;
			mc.player.width = .01f;
			int tp = 0;
			int packet = 0;
			if (PlayerUtils.isInsideBlock() || lastInSideStat) {
				for (int i = 0; i<200; i++) {
					int uy = 0;
					for (int y = 0; y<5+uy; y++) {
						boolean block = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY+uy-y, mc.player.posZ)).isFullBlock();
						if (!block) {
							if (mc.player.posY>0)
								PlayerUtils.vClip(-1);
						}else {
							break;
						}
					}
					PlayerUtils.Strafe(0.005);
					for (int i1 = 0; i1<1; i1++) {
						mc.player.setPosition(mc.player.posX + mc.player.motionX, mc.player.posY, mc.player.posZ + mc.player.motionZ);
						if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).isFullBlock() && tp < 1) {
							if (mc.player.posY>0)
								PlayerUtils.vClip(1);
							tp++;
						}
					}	
				}
				event.y = mc.player.posY - .5;
				lastInSideStat = PlayerUtils.isInsideBlock();	
			}
			if (mc.player.posY<0) {
				mc.player.setPosition(mc.player.posX, 1, mc.player.posZ);
				lastInSideStat = true;
			}
		}
		if (e instanceof EventPlayerInput) {
			EventPlayerInput event = (EventPlayerInput)e;
			event.setJump(false);
		}
	}
}