package com.github.satellite.features.module.player;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventHandleTeleport;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.CopyOnWriteArrayList;

public class NoFall extends Module {

	public NoFall() {
		super("NoFall", Keyboard.KEY_NONE, Category.PLAYER);
		teleportId=0;
	}
	
	ModeSetting mode;
	
	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "Packet", new String[] {"Packet", "NCP"});
		addSetting(mode);
		super.init();
	}
	
	int teleportId;
	CopyOnWriteArrayList<Vec3d> Catch = new CopyOnWriteArrayList<Vec3d>();
	
	@Override
	public void onEvent(Event<?> e) {
		if(mode.is("Packet")) {
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				event.setOnGround(true);

				//mc.player.rotationYaw=(float) Math.pow(mc.player.ticksExisted%45, Math.PI/2);
			}
		}
		if(mode.is("NCP")) {
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				event.setOnGround(true);
				if(mc.player.fallDistance>5) {
					Catch.add(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ));
					mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
					mc.player.motionY=0;
					PlayerUtils.vClip2(999, true);
					ClientUtils.setTimer(0.95F);
					teleportId++;
					mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
					mc.player.fallDistance=0;
				}
			}
			if(e instanceof EventHandleTeleport) {
				EventHandleTeleport event = (EventHandleTeleport)e;
				teleportId = event.teleportId;
			}
		}
		super.onEvent(e);
	}
	
}
