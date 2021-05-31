package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventHandleTeleport;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class PacketFly extends Module {

	public PacketFly() {
		super("PacketFly", Keyboard.KEY_G, Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		teleportId=0;
		clearLagTeleportId=0;
		speed=0;
		ClientUtils.setTimer(1.0F);
		super.onDisable();
	}

	@Override
	public void onEnable() {
		PlayerUtils.vClip2(-1024, false);
		super.onEnable();
	}

	int teleportId=0, clearLagTeleportId=0, cansellTeleport =0;
	double lastPacketX = 0, lastPacketY = 0, lastPacketZ = 0;
	double speed=0;

	@Override
	public void onEvent(Event<?> e) {
		
		if(e instanceof EventUpdate) {
			mc.player.moveStrafing=1;
			if(e.isPre()) {
				
				cansellTeleport =0;

				mc.player.motionY=0;
				
				for(int i = 0; i < 1; i++) {
					if(clearLagTeleportId<=teleportId)
					{
						clearLagTeleportId=teleportId+1;
					}

					if(teleportId != 0) {
						PlayerUtils.vClip2(-1024, false);

						mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

						if(mc.player.ticksExisted%3==0 && PlayerUtils.InputY() <= 0) {
							mc.player.motionY=-0.04;
							mc.player.onGround=true;
						}

						if(mc.player.ticksExisted%16==0 && PlayerUtils.InputY() > 0) {
							mc.player.motionY=-0.08;
							mc.player.onGround=true;
						}
					}

					
					
					mc.player.motionY += teleportId==0?0: PlayerUtils.InputY()*0.05;
					
					if(teleportId == 0) {
						ClientUtils.setTimer(0.5F);
					}else {
						ClientUtils.setTimer(1F);
					}
					speed=teleportId==0?0:0.1;
					
					PlayerUtils.Strafe(speed);
                    mc.player.setPosition(mc.player.lastTickPosX+mc.player.motionX, mc.player.lastTickPosY+mc.player.motionY, mc.player.lastTickPosZ+mc.player.motionZ);
					PlayerUtils.freeze();
					
					cansellTeleport++;
					cansellTeleport++;
					clearLagTeleportId++;
					if(mc.player.ticksExisted%20==0) {
						teleportId=0;
					}
				}
			}
		}
		
		if(e instanceof EventMotion) {
			EventMotion event = ((EventMotion)e);
			event.setYaw(0);
			event.setPitch(0);
		}
		
		if(e instanceof EventPacket) {
			EventPacket event = ((EventPacket)e);
			if(event.isOutgoing()) {
				Packet<?> p = event.getPacket();
				if(p instanceof CPacketPlayer.Rotation)
					event.setCancelled(true);

				if(p instanceof CPacketPlayer.PositionRotation) {
					CPacketPlayer.PositionRotation pos = ((CPacketPlayer.PositionRotation) p);
					mc.getConnection().sendPacket(new CPacketPlayer.Position(pos.getX(mc.player.posX), pos.getY(mc.player.posY), pos.getZ(mc.player.posZ), false));
				}

				if(p instanceof CPacketPlayer.Position) {
					CPacketPlayer.Position packet = (CPacketPlayer.Position)p;
					double dx = packet.getX(lastPacketX) - lastPacketX;
					double dy = packet.getX(lastPacketY) - lastPacketY;
					double dz = packet.getX(lastPacketZ) - lastPacketZ;
					mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(dx)+));
				}
			}
			if(event.isIncoming()) {

			}
		}
		
		if(e instanceof EventHandleTeleport) {
			EventHandleTeleport event = ((EventHandleTeleport)e);

			event.setYaw(mc.player.cameraYaw);
			event.setPitch(mc.player.cameraPitch);
			
			teleportId=(int) event.getTeleportId();
			
			if(mc.player.getDistance(event.getX(), event.getY(), event.getZ()) < 2) {
				event.setCancellTeleporting(true);
			}else {
				teleportId = 0;
			}
		}
		super.onEvent(e);
	}
	
}
