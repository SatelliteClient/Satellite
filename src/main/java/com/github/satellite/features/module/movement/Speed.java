package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventJump;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntity;
import com.github.satellite.mixin.client.AccessorEntityPlayer;
import com.github.satellite.mixin.client.AccessorEntityPlayerSP;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

public class Speed extends Module {


	public Speed () {
		super("Speed", Keyboard.KEY_Z, Category.MOVEMENT);
	}

	ModeSetting mode;

	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "NCP", new String[] {"NCP", "OldNCP", "YPort", "TEST"});
		addSetting(mode);
		super.init();
	}

	double lastSpeed;
	boolean lastGround;
	boolean moving;
	boolean OnGround = false;
	int tickTimer;
	int progress;
	double moveSpeed;
	double lastDist;
	int teleportIdF = 0;

	int teleportId = 0, clearLagTeleportId = 0;

	boolean inTimer;

	@Override
	public void onEvent(Event<?> e) {
		/*if (e instanceof EventPacket) {
			Packet p = ((EventPacket)e).getPacket();
			if (p instanceof SPacketSetSlot && mc.player.isEntityAlive() && !mc.player.isDead) {
				SPacketSetSlot packet = (SPacketSetSlot)p;
				if (packet.getStack().getItem() instanceof ItemAir) {
					mc.getConnection().sendPacket(new CPacketChatMessage("/kill"));
					mc.player.setDead();
				}
			}
		}*/
		if(e instanceof EventMotion && e.isPre()) {
			EventMotion event = (EventMotion)e;
			switch (mode.getMode()) {

				case "NCP":
					lastSpeed += 0.0015;
					lastSpeed*=.9900000095367432D;
					//mc.player.motionY -= mc.player.motionY < .33319999363422365D ? 9.9999E-4D : 0;
					if(mc.player.motionY == .33319999363422365) {
						mc.player.motionY -= mc.player.motionY < .33319999363422365D ? 9.9999E-4D : 0;
					}
					if (!mc.player.onGround) {
						MovementUtils.Strafe(lastSpeed);
					}else {
						MovementUtils.Strafe(mc.player.isSprinting()?.15:.12);
						lastSpeed = Math.sqrt(Math.pow(Math.abs(mc.player.posX - mc.player.lastTickPosX), 2) + Math.pow(Math.abs(mc.player.posZ - mc.player.lastTickPosZ), 2));
					}
					/*if (MovementUtils.isMoving()) {
						if (mc.player.onGround) {
							mc.player.jump();
						}
						mc.player.speedInAir = (.0223F);
						MovementUtils.Strafe(MovementUtils.getSpeed());
					} else {
						mc.player.motionX = 0.0D;
						mc.player.motionZ = 0.0D;
					}*/
					break;
				case "OldNCP":
					if (MovementUtils.isMoving()) {
						if (mc.player.onGround) {
							mc.player.jump();
							mc.player.motionX *= 1.05D;
							mc.player.motionZ *= 1.05D;
						}
						mc.player.motionY -= mc.player.motionY < .33319999363422365D ? 9.9999E-4D*5 : 0;

						MovementUtils.Strafe(MovementUtils.getSpeed() + (mc.player.motionY == .33319999363422365D && MovementUtils.getSpeed() > 0.3 ? 0.05 : 0));
					} else {
						mc.player.motionX = 0.0D;
						mc.player.motionZ = 0.0D;
					}
					break;
				case "TEST":
					if (mc.player.motionY <= -.42D) {
						if(clearLagTeleportId<=teleportId)
						{
							clearLagTeleportId=teleportId+1;
						}

						if(clearLagTeleportId != 0) {
							MovementUtils.vClip2(-1024, true);

							mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

							mc.player.motionY = 0;
							MovementUtils.Strafe(.24D);
						}

						clearLagTeleportId++;
					}
			}
		}

		if (e instanceof EventJump) {
			lastSpeed = .32;
			MovementUtils.Strafe(.29);
		}

		if(e instanceof EventMotion && e.isPre()) {
			EventMotion event = (EventMotion)e;
			switch (mode.getMode()) {

				case "YPort":
					if(mc.player.onGround && MovementUtils.isMoving()) {
						if(tickTimer % 2 != 0) {
							event.y += .4;
						}

						MovementUtils.Strafe(tickTimer % 2 == 0 ? .45F : .2F);
						ClientUtils.setTimer(1.095F);
						tickTimer++;
						if (!mc.player.onGround) {
							tickTimer = 1;
						}
					}
					if(mc.player.motionY == .33319999363422365) {
						MovementUtils.Strafe(0);
					}
			}
		}

		if(e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			switch (mode.getMode()) {
				case "TEST":
					if(e instanceof EventPacket) {
						if(event.isIncoming()) {
							if (p instanceof SPacketPlayerPosLook) {
								SPacketPlayerPosLook packet = (SPacketPlayerPosLook)p;
								teleportId = packet.getTeleportId();
								if(mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < 4) {
									event.setCancelled(true);
									mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
								}else {
									clearLagTeleportId = teleportId;
								}
							}
						}
					}
			}
		}
		if(e instanceof EventPlayerInput) {
			EventPlayerInput event = (EventPlayerInput)e;
			if (!(mode.is("YPort") && tickTimer % 2 != 0) && !mode.is("NCP")) {
				event.setJump(false);
			}
		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
		lastSpeed  = MovementUtils.getSpeed();
		switch (mode.getMode()) {
			case "TEST":
				teleportId         = 0;
				clearLagTeleportId = 0;
				MovementUtils.vClip2(999, true);
		}
		super.onEnable();
	}

	@Override
	public void onDisable()	{
		((AccessorEntityPlayer)mc.player).speedInAir(.02F);
		ClientUtils.setTimer(1F);
		super.onDisable();
	}
}