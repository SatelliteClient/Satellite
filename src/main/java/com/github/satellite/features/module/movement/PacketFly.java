package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class PacketFly extends Module {

	public PacketFly() {
		super("PacketFly", Keyboard.KEY_G, Category.MOVEMENT);
	}

	BooleanSetting phase;
	BooleanSetting debug;

	@Override
	public void init() {
		this.phase = new BooleanSetting("Phase", false);
		this.debug = new BooleanSetting("Debug", false);
		addSetting(phase, debug);
		super.init();
	}

	@Override
	public void onDisable() {
		teleportId=0;
		clearLagTeleportId=0;
		speed=0;
		super.onDisable();
	}

	@Override
	public void onEnable() {
		PlayerUtils.vClip2(-1024, true);
		super.onEnable();
	}

	int teleportId = 0, clearLagTeleportId = 0;
	double lastPacketX = 0, lastPacketY = 0, lastPacketZ = 0;
	double speed=0;

	@Override
	public void onEvent(Event<?> e) {
		
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
				mc.player.motionY = 0;

				for(int i = 0; i < 1; i++) {
					if(clearLagTeleportId<=teleportId)
					{
						clearLagTeleportId=teleportId+1;
					}

					if(clearLagTeleportId != 0) {
						PlayerUtils.vClip2(-1024, true);

						mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

						if(mc.player.ticksExisted%3==0 && PlayerUtils.InputY() <= 0) {
							mc.player.motionY=-.04D;
						}

						if(mc.player.ticksExisted%16==0 && PlayerUtils.InputY() > 0) {
							mc.player.motionY=-.10D;
						}
					}

					mc.player.motionY += teleportId==0?0: PlayerUtils.InputY()* .062D;

					speed = teleportId==0? .01 : (mc.world.getBlockState(new BlockPos(mc.player).offset(EnumFacing.DOWN)).getBlock() instanceof BlockAir) ?.25:.1;
					speed*= PlayerUtils.InputY()>0?0:1;

					PlayerUtils.Strafe(speed);
					if (phase.isEnable()) {
						mc.player.setPosition(mc.player.posX+mc.player.motionX, mc.player.posY+mc.player.motionY, mc.player.posZ+mc.player.motionZ);
					}else {
						PlayerUtils.move();
					}
					PlayerUtils.freeze();

					clearLagTeleportId++;
				}
			}
		}
		
		if(e instanceof EventMotion) {
			EventMotion event = ((EventMotion)e);
			event.setYaw(0);
			event.setPitch(0);
			event.setOnGround(true);
			event.cancel();
		}
		
		if(e instanceof EventPacket) {
			EventPacket event = ((EventPacket)e);
			Packet<?> p = event.getPacket();
			if(event.isOutgoing()) {
				if(p instanceof CPacketPlayer.Rotation)
					event.setCancelled(true);

				if(p instanceof CPacketPlayer.PositionRotation) {
					CPacketPlayer.PositionRotation pos = ((CPacketPlayer.PositionRotation) p);
					mc.getConnection().sendPacket(new CPacketPlayer.Position(pos.getX(mc.player.posX), pos.getY(mc.player.posY), pos.getZ(mc.player.posZ), false));
				}

				if(p instanceof CPacketPlayer.Position && debug.isEnable()) {
					CPacketPlayer.Position packet = (CPacketPlayer.Position)p;
					double dx = packet.getX(lastPacketX) - lastPacketX;
					double dy = packet.getY(lastPacketY) - lastPacketY;
					double dz = packet.getZ(lastPacketZ) - lastPacketZ;
					lastPacketX = packet.getX(lastPacketX);
					lastPacketY = packet.getY(lastPacketY);
					lastPacketZ = packet.getZ(lastPacketZ);
					mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(dx)+", "+String.valueOf(dy)+", "+String.valueOf(dz)));
				}
			}

			if(event.isIncoming()) {
				if (p instanceof SPacketPlayerPosLook) {
					SPacketPlayerPosLook packet = (SPacketPlayerPosLook)p;
					teleportId = packet.getTeleportId();
					if (debug.isEnable())
						mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(packet.getTeleportId())));
					if(mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < 2) {
						event.setCancelled(true);
						mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
					}else {
						clearLagTeleportId = teleportId;
					}
				}
			}
		}
		super.onEvent(e);
	}
	
}
