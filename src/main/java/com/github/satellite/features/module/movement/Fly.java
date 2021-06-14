package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.MoverType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

public class Fly extends Module {

	public Fly() {
		super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
	}
	
	ModeSetting mode;
	ModeSetting hypixelBoost;
	
	@Override
	public void init() {
		mode = new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "Hypixel", "2b2t Japan"});
		hypixelBoost = new ModeSetting("hypixelBoost", "Normal", new String[] {"Normal", "Funcraft", "Boost", "PacketBoost", "DamageBoost"});
		addSetting(mode, hypixelBoost);
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
	
	@Override
	public void onEnable() {
		
		lastTickYaw = mc.player.rotationYaw;
		lastTickPitch = mc.player.rotationPitch;
	    double fallDistance;
		
		switch(mode.getMode()) {
		case "Hypixel":
			switch(hypixelBoost.getMode()) {
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
                lastTickSpeed = mc.player.onGround ? mc.player.isPotionActive(Potion.getPotionById(1)) ? 1.34D : 1.261D : PlayerUtils.getSpeed();
				//ClientUtils.setTimer((float) (0.0207464372));
				break;
			case "Boost":
				lastTickSpeed = .24D;
				if (!mc.player.onGround)
					break;
                lastTickSpeed = mc.player.isPotionActive(Potion.getPotionById(1)) ? 1.34D : 1.261D ;
				break;
			case "Funcraft":
			case "Normal":
				lastTickSpeed = mc.player.onGround ? .5D : PlayerUtils.getSpeed();
				break;
			}
			if(mode.getMode() != "PacketBoost") {
				if(!mc.player.onGround)
					break;
				mc.player.jump();
				mc.player.posY += .41999998688697815D;
			}
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
	        setDisplayName("Fly \u00A77" + ((ModeSetting)settings.get(1)).getMode());
		}
		
		switch(mode.getMode()) {
		
		case "Vanilla":
		{
			if(e instanceof EventMotion) {
				if (e.isPre()) {
					mc.player.motionY= PlayerUtils.InputY()*(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:1);
					PlayerUtils.Strafe(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:1);
					PlayerUtils.move();
					PlayerUtils.freeze();
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
					if(tickTimer>0) {
						ClientUtils.setTimer(1F);
						mc.player.motionY=0;
						if(mc.player.onGround) {
							mc.player.move(MoverType.SELF, 0, tickTimer==1 ? .42 : 0, 0);
							mc.player.onGround=true;
							lastTickSpeed=1;
						}
						PlayerUtils.Strafe(mc.player.onGround? 1F : lastTickSpeed < .24? .24 : lastTickSpeed - lastTickSpeed/150);
						lastTickSpeed=mc.player.collidedHorizontally||mc.gameSettings.keyBindJump.isKeyDown() ? .24 : PlayerUtils.getSpeed();
						lastTickSpeed -= tickTimer>=9?lastTickSpeed/4:0;
					}
					if(tickTimer==0) {
						if(mc.player.onGround) {
							double y = mc.player.posY;
							mc.player.move(MoverType.SELF, 0, .42, 0);
							double y1 = mc.player.posY;
							mc.player.move(MoverType.SELF, 0, -.42, 0);
							y1 -= y;
							int i=0;
							for(i=0; i<5; i++) {
								PlayerUtils.move();
								PlayerUtils.vClip2(y1, false);
								PlayerUtils.move();
								PlayerUtils.vClip2(0, false);
								ClientUtils.setTimer(1F / (i*2));
							}
							PlayerUtils.Strafe(.45D);
							mc.player.motionY=-0.08;
						}
					}
					
					lastTickYaw = mc.player.rotationYaw;
					lastTickPitch = mc.player.rotationPitch;
				}
				break;
			case "DamageBoost":
				if(e instanceof EventMotion && e.isPre()) {
					ClientUtils.setTimer(tickTimer > 0 ? 1 : ClientUtils.getTimer());
					mc.player.motionY = 0;
					PlayerUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed/150);
					lastTickSpeed = mc.player.collidedHorizontally ? 0.26 : PlayerUtils.getSpeed();
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
					PlayerUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed/150);
					lastTickSpeed = mc.player.collidedHorizontally ? .26D : PlayerUtils.getSpeed();
				}
				break;
			case "Funcraft":
				if(e instanceof EventMotion && e.isPre()) {
					mc.player.motionY = 0;
					PlayerUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed / 150);
					lastTickSpeed = mc.player.collidedHorizontally ? .26D : PlayerUtils.getSpeed();
				}
				break;
			case "Normal":
				if(e instanceof EventMotion && e.isPre()) {
					mc.player.motionY = 0;
					PlayerUtils.Strafe(lastTickSpeed < .26D ? .26D : lastTickSpeed - lastTickSpeed / 15);
					lastTickSpeed = mc.player.collidedHorizontally ? .26D : PlayerUtils.getSpeed();
				}
				break;
			}
			
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				event.y+=mc.player.ticksExisted%2==0?1E-5:-1E-5;
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
			if(e instanceof EventUpdate) {
				mc.player.motionY=0;
				
				CansellingTeleport=0;
				
				for(int i = 0; i < 1; i++) {
					if(clearLagTeleportId<=teleportId)
					{
						clearLagTeleportId=teleportId+1;
					}
					
					PlayerUtils.vClip2(999, true);
					mc.getConnection().sendPacket(new CPacketConfirmTeleport(clearLagTeleportId));				
					
					double speed=(Keyboard.isKeyDown(Keyboard.KEY_B)?0.1:2);
					if(teleportId==0) {
						speed=0;
					}
					PlayerUtils.Strafe(speed);
					for(int i1=0; i1<4; i1++) {
						PlayerUtils.vClip(PlayerUtils.InputY()*speed);
						PlayerUtils.move();
						CansellingTeleport++;
					}

					PlayerUtils.freeze();
					clearLagTeleportId++;	
				}
			}
			
			if(e instanceof EventHandleTeleport) {
				EventHandleTeleport event = ((EventHandleTeleport)e);
				
				teleportId=(int) event.getTeleportId();
				
				if(CansellingTeleport>0&&mc.player.getDistance(event.getX(), mc.player.posY, event.getZ()) < 2*CansellingTeleport*CansellingTeleport*2) {
					event.setCancellTeleporting(true);
					CansellingTeleport--;
				}
			}
			
			if(e instanceof EventRenderGUI) {
				String speed = String.valueOf(Math.sqrt(Math.pow(mc.player.posX - mc.player.lastTickPosX, 2) + Math.pow(mc.player.posZ - mc.player.lastTickPosZ, 2)) * 10);
				new Gui().drawString(mc.fontRenderer, speed, mc.displayWidth/4 - mc.fontRenderer.getStringWidth(speed)/2, mc.displayHeight/4 - mc.fontRenderer.FONT_HEIGHT/2, 0xffffffff);
			}
			break;
		}
		
		}
		super.onEvent(e);
	}
}
