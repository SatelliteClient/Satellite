package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import com.google.common.base.Supplier;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
	NumberSetting speedSetting1;
	NumberSetting speedSetting2;
	BooleanSetting phase;
	ModeSetting hypixelBoost;

	@Override
	public void init() {
		mode = new ModeSetting("Mode", null, "Vanilla", new String[] {"Vanilla", "NCPHypixel", "2b2t Japan", "TEST1", "TEST2", "TEST3"});

		speedSetting = new NumberSetting("Speed", new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return mode.is("2b2t Japan");
			}
		}, 8, 0, 25, 0.5D);
		speedSetting1 = new NumberSetting("Speed", new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return !mode.is("2b2t Japan");
			}
		}, 1, 0, 10, 0.1D);
		speedSetting2 = new NumberSetting("ElytraSpeed", new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return mode.is("Vanilla");
			}
		}, 1, 0, 10, 0.1D);



		hypixelBoost = new ModeSetting("hypixelBoost", new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return mode.is("NCPHypixel");
			}
		}, "Normal", new String[] {"Normal", "Funcraft", "Boost", "PacketBoost", "DamageBoost", "TEST1", "TEST2"});
		phase = new BooleanSetting("Phase", null, false);
		addSetting(mode, speedSetting, speedSetting1, speedSetting2, hypixelBoost, phase);
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

	Vec3d lastCatch = Vec3d.ZERO;

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
			case "NCPHypixel":
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
				if (e instanceof EventUpdate) {
				}
				if (e instanceof EventMotion && e.isPre()) {
					if (e.isPre()) {
						if (mc.player.isElytraFlying()) {
							mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
						}
						double speed = mc.player.isElytraFlying()?speedSetting2.value:speedSetting1.value;
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



			case "NCPHypixel":
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

					case "TEST1":
						if(e instanceof EventMotion && e.isPre()) {
							mc.player.motionY = MovementUtils.InputY()*speedSetting.value;
							mc.player.motionX = MovementUtils.InputX()*speedSetting.value;
							mc.player.motionZ = MovementUtils.InputZ()*speedSetting.value;
						}
						break;
				}

				if(e instanceof EventMotion) {
					EventMotion event = (EventMotion)e;
					event.y+=mc.player.ticksExisted%2==0?1E-5:-1E-5;
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
				//Matrix Fly
				if(e instanceof EventMotion) {
					if (mc.player.ticksExisted%2==0) {
						MovementUtils.Strafe(.1);
					}else {
						mc.player.motionY = 0;
					}
					MovementUtils.Strafe(.1);
					if ((int)(tickTimer/6)%20!=0) {
						ClientUtils.setTimer(8F);
					}else {
						ClientUtils.setTimer(.1F);
					}
					tickTimer ++;
					//mc.player.motionY = 0;
				}
				if (e instanceof EventPacket) {
					EventPacket event = (EventPacket)e;
					Packet p = event.getPacket();
					if (p instanceof SPacketPlayerPosLook) {
					}
				}

				//ACR
				/*if (e instanceof EventMotion) {
					if (mc.player.ticksExisted%4!=0) {
						MovementUtils.Strafe(1);
					}else {
						MovementUtils.Strafe(99);
					}
				}*/
				
				/*if(e instanceof EventMotion) {
					EventMotion event = (EventMotion)e;
					if (mc.player.ticksExisted%20==0) {
						mc.getConnection().sendPacket(new CPacketChatMessage("/heal"));
					}
					if (mc.player.fallDistance > 0) {
						event.setOnGround(true);
						MovementUtils.freeze();
						float dist = mc.player.fallDistance;
						double y = mc.player.posY;
						MovementUtils.move(0, -1, 0);
						ClientUtils.setTimer(0.5F);
						if (mc.player.collidedVertically) {
							MovementUtils.vClip2(-1E-10, true);
							lastCatch = new Vec3d(mc.player.posX, mc.player.posY-1E-10, mc.player.posZ);
						}else {
							mc.getConnection().sendPacket(new CPacketPlayer.Position(lastCatch.x, lastCatch.y, lastCatch.z, true));
						}
						MovementUtils.yClip(y);
						mc.player.fallDistance = dist;
						MovementUtils.Strafe(0.1);
					}
				}*/
				break;
			}
			case "TEST3":
				if (e instanceof EventMotion) {
					if (e.isPost()) {
						mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player).offset(EnumFacing.DOWN, 2), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 1, 0));
						ClientUtils.setTimer(ClientUtils.getTimer()+(mc.player.ticksExisted%10==0?1:0));
						ClientUtils.setTimer(ClientUtils.getTimer()+(1-ClientUtils.getTimer())/5f);
					}
				}
		}
		super.onEvent(e);
	}
}
