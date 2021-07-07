package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}

	ModeSetting mode;
	NumberSetting speedSetting;
	BooleanSetting phase;
	ModeSetting hypixelBoost;

	@Override
	public void init() {
		mode = new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "Hypixel", "2b2t Japan", "TEST1", "TEST2"});
		speedSetting = new NumberSetting("Speed", 8, 0, 25, 0.5D);
		hypixelBoost = new ModeSetting("hypixelBoost", "Normal", new String[] {"Normal", "Funcraft", "Boost", "PacketBoost", "DamageBoost", "TEST1", "TEST2"});
		phase = new BooleanSetting("Phase", false);
		addSetting(mode, speedSetting, hypixelBoost, phase);
		super.init();
	}

	@Override
	public void onDisable() {
		tickTimer = 0;
		teleportId = 0;
		clearLagTeleportId = 0;
		lastTickSpeed = 0;
		lastTickYaw = 0;
		lastTickPitch = 0;
		fallDistance = 0;

		ClientUtils.setTimer(1F);
		super.onDisable();
	}

	ArrayList<Vec3d> catchVec = new ArrayList<>();

	@Override
	public void onEnable() {
		catchVec = new ArrayList<>();
		if (mc.player == null) {
			toggle();
			return;
		}
		lastTickYaw = mc.player.rotationYaw;
		lastTickPitch = mc.player.rotationPitch;
		double fallDistance;

		switch(mode.getMode()) {
			case "Hypixel":
				switch(hypixelBoost.getMode()) {
					case "PacketBoost":
						lastTickSpeed = .24D;
						if (!mc.player.onGround)
							break;
						lastTickSpeed = mc.player.isPotionActive(Potion.getPotionById(1)) ? 1.34D : 1.261D ;
						break;
					case "DamageBoost":
						if (!mc.player.onGround)
							break;
						fallDistance = 3.0125D;
						while (fallDistance > 0.0D) {
							mc.getConnection().sendPacket((Packet<?>)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0624986421D, mc.player.posZ, false));
							mc.getConnection().sendPacket((Packet<?>)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625D, mc.player.posZ, false));
							mc.getConnection().sendPacket((Packet<?>)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0624986421D, mc.player.posZ, false));
							mc.getConnection().sendPacket((Packet<?>)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6D, mc.player.posZ, false));
							fallDistance -= 0.0624986421D;
						}
						mc.getConnection().sendPacket((Packet<?>)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
						lastTickSpeed = mc.player.onGround ? mc.player.isPotionActive(Potion.getPotionById(1)) ? 1.34D : 1.261D : MovementUtils.getSpeed();
						break;
					case "Boost":
						lastTickSpeed = .24D;
						if (!mc.player.onGround)
							break;
						lastTickSpeed = mc.player.isPotionActive(Potion.getPotionById(1)) ? 1.34D : 1.261D ;
						break;
					case "Funcraft":
					case "Normal":
						lastTickSpeed = mc.player.onGround ? .5D : MovementUtils.getSpeed();
						break;
				}
				if(!mc.player.onGround)
					break;
				mc.player.jump();
				mc.player.posY += .41999998688697815D;
		}
		super.onEnable();
	}

	double lastTickSpeed=0;
	double lastTickYaw=0;
	double lastTickPitch=0;
	double tickTimer=0;
	int CansellingTeleport;
	int clearLagTeleportId;
	int teleportId;
	int speed;
	float fallDistance = 0;

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventUpdate) {
			setDisplayName("Fly \u00A77" + mode.getMode());
		}

		switch(mode.getMode()) {

			case "Vanilla":
			{
				if(e instanceof EventMotion) {
					if (e.isPre()) {
						double speed = speedSetting.value;
						mc.player.motionY= MovementUtils.InputY()*(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:speed);
						MovementUtils.Strafe(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:speed);
						MovementUtils.move();
						MovementUtils.freeze();
					}
				}
				if(e instanceof EventRenderGUI) {
					String speed = String.valueOf(Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2)) * 10);
					new Gui().drawString(mc.fontRenderer, speed, mc.displayWidth/4 - mc.fontRenderer.getStringWidth(speed)/2, mc.displayHeight/4 - mc.fontRenderer.FONT_HEIGHT/2, 0xffffffff);
				}

				break;
			}



			case "Hypixel":
			{
				switch(hypixelBoost.getMode()) {
					case "PacketBoost":
						if(e instanceof EventMotion && e.isPre()) {
							ClientUtils.setTimer(1F);
							mc.player.motionY=0;
							MovementUtils.Strafe(mc.player.onGround? 1F : lastTickSpeed < .24? .24 : lastTickSpeed - lastTickSpeed/150);
							/*if (tickTimer > 7) {
								MovementUtils.Strafe(MovementUtils.getSpeed()/4);
							}*/
							lastTickSpeed=mc.player.collidedHorizontally||mc.gameSettings.keyBindJump.isKeyDown() ? .24 : MovementUtils.getSpeed();
							lastTickSpeed -= tickTimer>=7?lastTickSpeed/4:0;
						}
						break;
					case "DamageBoost":
						if(e instanceof EventMotion && e.isPre()) {
							ClientUtils.setTimer(tickTimer > 0 ? 1 : ClientUtils.getTimer());
							mc.player.motionY = 0;
							MovementUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed/150);
							lastTickSpeed = mc.player.collidedHorizontally ? 0.26 : MovementUtils.getSpeed();
						}
						break;
					case "Boost":
						if(e instanceof EventMotion && e.isPre()) {
							mc.player.motionY = 0;
							if (tickTimer==10) {
								if(mc.player.isPotionActive(Potion.getPotionById(1)))
									lastTickSpeed = .464531D;
								else
									lastTickSpeed = 0D;
							}
							if (mc.player.isPotionActive(Potion.getPotionById(1)) && tickTimer==20) {
								lastTickSpeed = 0D;
							}
							MovementUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed/150);
							lastTickSpeed = mc.player.collidedHorizontally ? .26D : MovementUtils.getSpeed();
						}
						break;
					case "Funcraft":
						if(e instanceof EventMotion && e.isPre()) {
							mc.player.motionY = 0;
							MovementUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed / 150);
							lastTickSpeed = mc.player.collidedHorizontally ? .26D : MovementUtils.getSpeed();
						}
						break;
					case "Normal":
						if(e instanceof EventMotion && e.isPre()) {
							mc.player.motionY = 0;
							MovementUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed / 15);
							lastTickSpeed = mc.player.collidedHorizontally ? .26D : MovementUtils.getSpeed();
						}
						break;
				}

				if(e instanceof EventMotion) {
					EventMotion event = (EventMotion)e;
					event.y+=mc.player.ticksExisted%2==0?1E-5:-1E-5;
					if(mc.player.ticksExisted%2!=0) {
						mc.player.motionY=-1E-5D;
					}
					tickTimer ++;
				}
				if(e instanceof EventPacket) {
					EventPacket event = (EventPacket)e;
					if(event.isIncoming()) {
						Packet p = event.getPacket();
						if(p instanceof SPacketEntityVelocity) {
							event.setCancelled(true);
						}
					}
				}
				break;
			}



			case "2b2t Japan":
			{
				if (e instanceof EventUpdate) {
					mc.player.motionY=0;

					CansellingTeleport=0;

					for(int i = 0; i < 1; i++) {
						if(clearLagTeleportId<=teleportId)
						{
							clearLagTeleportId=teleportId+1;
						}
						;
						catchVec.add(mc.player.getPositionVector());
						MovementUtils.vClip2(999, true);
						mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));

						double speed=(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:speedSetting.value);
						if(teleportId==0) {
							speed=0;
						}
						MovementUtils.Strafe(speed);
						MovementUtils.vClip(MovementUtils.InputY()*speed);
						if (phase.isEnable()) {
							MovementUtils.clip();
						}else {
							MovementUtils.move();
						}
						CansellingTeleport++;

						MovementUtils.freeze();
						clearLagTeleportId++;
					}
				}

				if(e instanceof EventHandleTeleport) {
					EventHandleTeleport event = ((EventHandleTeleport)e);

					teleportId=(int) event.getTeleportId();

					Vec3d pos = new Vec3d(event.x, event.y, event.z);

					for (Vec3d vec : catchVec) {
						if (vec.equals(pos)) {
							catchVec.remove(vec);
							event.setCancellTeleporting(true);
							CansellingTeleport--;
							return;
						}
					}
				}

				if(e instanceof EventRenderGUI) {
					String speed = String.valueOf(Math.sqrt(Math.pow(mc.player.posX - mc.player.lastTickPosX, 2) + Math.pow(mc.player.posZ - mc.player.lastTickPosZ, 2)) * 20);
					new Gui().drawString(mc.fontRenderer, speed, mc.displayWidth/4 - mc.fontRenderer.getStringWidth(speed)/2, mc.displayHeight/4 - mc.fontRenderer.FONT_HEIGHT/2, 0xffffffff);
				}
				break;
			}

			case "TEST1":
			{
				if (e instanceof EventJump && e.isPost()) {
					MovementUtils.Strafe(MovementUtils.getSpeed());
				}
				if (e instanceof EventUpdate) {
					if (mc.player.onGround) {
						//mc.player.jump();
					}
				}
				if (e instanceof EventMotion) {
					EventMotion event = (EventMotion)e;
					mc.player.motionY -= (mc.player.motionY - MovementUtils.nextY(mc.player.motionY)) * .058D;
					mc.player.motionX += (mc.player.motionX - MovementUtils.nextSpeed(mc.player.motionX)) * .149D;
					mc.player.motionZ += (mc.player.motionZ - MovementUtils.nextSpeed(mc.player.motionZ)) * .149D;
					lastTickSpeed = mc.player.motionY;
				}
				break;
			}

			case "TEST2":
			{
				if (e instanceof EventMotion) {
					EventMotion event = (EventMotion)e;
					event.pitch = -90F;
					ClientUtils.setTimer(1.0F);
					if (mc.player.motionY > 0) return;
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
					ClientUtils.setTimer(0.27F);
					tickTimer ++;
					if (tickTimer%2==0) {
						mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
						ClientUtils.setTimer(0.3F);
					}else {
						mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
						ClientUtils.setTimer(.05F);
					}
					if (tickTimer > 0) {
						//tickTimer = 0;
					}
				}
				if (e instanceof EventPacket && e.isIncoming()) {
					EventPacket event = (EventPacket)e;
					Packet p = event.getPacket();
					if (p instanceof SPacketEntityVelocity) {
						SPacketEntityVelocity packet = (SPacketEntityVelocity)p;
						if (packet.getEntityID() == mc.player.getEntityId()) {
							//mc.player.motionY = .36075;
							mc.player.motionY = packet.getMotionY()/8000.0D;
							//MovementUtils.Strafe(5);
							e.cancel();
						}
					}
				}
				break;
			}

		}
		super.onEvent(e);
	}
}
