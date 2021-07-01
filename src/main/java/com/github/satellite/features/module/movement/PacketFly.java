package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntityPlayerSP;
import com.github.satellite.mixin.client.AccessorNetHandlerPlayClient;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

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
		MovementUtils.vClip2(-1024, true);
		super.onEnable();
	}

	int teleportId = 0, clearLagTeleportId = 0;
	double lastPacketX = 0, lastPacketY = 0, lastPacketZ = 0;
	double speed=0;

	ArrayList<Vec3d> catchVec = new ArrayList<>();

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
						catchVec.add(mc.player.getPositionVector());
						MovementUtils.vClip2(1010, true);

						mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

						if (!MovementUtils.isInsideBlock()) {
							if(mc.player.ticksExisted%3==0 && MovementUtils.InputY() <= 0) {
								mc.player.motionY=-.04D;
							}

							if(mc.player.ticksExisted%16==0 && MovementUtils.InputY() > 0) {
								mc.player.motionY=-.10D;
							}
						}
					}

					mc.player.motionY += teleportId==0?0: MovementUtils.InputY()* .062D;

					speed = teleportId==0? .04 : !MovementUtils.isInsideBlock() ?.25 : .1;
					speed*= MovementUtils.InputY()>0?0:1;

					MovementUtils.Strafe(speed);
					if (phase.isEnable()) {
						mc.player.setPosition(mc.player.posX+mc.player.motionX, mc.player.posY+mc.player.motionY, mc.player.posZ+mc.player.motionZ);
					}else {
						MovementUtils.move();
					}
					MovementUtils.freeze();

					clearLagTeleportId++;
				}
			}
		}

		if(e instanceof EventMotion) {
			EventMotion event = ((EventMotion)e);
			event.setYaw(((AccessorEntityPlayerSP)mc.player).lastReportedYaw());
			event.setPitch(((AccessorEntityPlayerSP)mc.player).lastReportedPitch());
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
					Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());

					if (debug.isEnable())
						mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(String.valueOf(packet.getTeleportId())));

					for (Vec3d vec : catchVec) {
						if (vec.equals(pos)) {
							catchVec.remove(vec);
							mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
							event.cancel();
							return;
						}
					}
					if (!mc.inGameHasFocus) return;
					EntityPlayer entityplayer = mc.player;
					double d0 = packet.getX();
					double d1 = packet.getY();
					double d2 = packet.getZ();
					float f = packet.getYaw();
					float f1 = packet.getPitch();

					if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
					{
						d0 += entityplayer.posX;
					}
					else
					{
						entityplayer.motionX = 0.0D;
					}

					if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
					{
						d1 += entityplayer.posY;
					}
					else
					{
						entityplayer.motionY = 0.0D;
					}

					if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
					{
						d2 += entityplayer.posZ;
					}
					else
					{
						entityplayer.motionZ = 0.0D;
					}

					if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
					{
						f1 += entityplayer.rotationPitch;
					}

					if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
					{
						f += entityplayer.rotationYaw;
					}

					entityplayer.setPosition(d0, d1, d2);
					mc.getConnection().sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
					mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

					if (!((AccessorNetHandlerPlayClient)mc.getConnection()).doneLoadingTerrain())
					{
						mc.player.prevPosX = mc.player.posX;
						mc.player.prevPosY = mc.player.posY;
						mc.player.prevPosZ = mc.player.posZ;
						((AccessorNetHandlerPlayClient)mc.getConnection()).setDoneLoadingTerrain(true);
						mc.displayGuiScreen((GuiScreen)null);
					}

					event.cancel();
					clearLagTeleportId = teleportId;
				}
			}
		}
		super.onEvent(e);
	}

}
