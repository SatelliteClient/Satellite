package com.github.satellite.features.module.movement;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.PlayerUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class Phase extends Module {

	public Phase() {
		super("Phase", Keyboard.KEY_H, Category.MOVEMENT);
	}
	
	@Override
	public void init() {
		settings.add(new ModeSetting("Mode", "NCP", new String[] {"NCP", "AAC", "Matrix"}));
		super.init();
	}
	
	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		tickTimer=0;
		teleportId=0;
		clearLagTeleportId=0;
		super.onDisable();
	}

	double lastTickSpeed=0;
	double tickTimer=0;
	int CansellingTeleport;
	int clearLagTeleportId;
	int teleportId;
	int speed;
	int reset;

	@Override
	public void onEvent(Event<?> e) {
		switch(((ModeSetting)settings.get(1)).getMode()) {
		
		case "NCP":
		{
			if(e instanceof EventUpdate) {
				if(mc.player.collidedVertically) {
					if(tickTimer==1) {
						PlayerUtils.vClip2(999, true);
					}
					double speed = 1;
					double late = 60;
					int tick = 50;
					
					late = 1 / late;
					late = 1 - late;
					PlayerUtils.Strafe(speed - speed*Math.pow(late, tickTimer%tick));
					mc.player.setPosition(mc.player.posX += mc.player.motionX, mc.player.posY, mc.player.posZ += mc.player.motionZ);
					PlayerUtils.vClip2(0, true);
					tickTimer++;
				}
			}
			/*if(e instanceof EventUpdate) {
				mc.player.motionY=0.3;
				if(mc.player.isCollidedVertically) {
					PlayerUtils.vClip(1E-10);
				}
			}
			if(e instanceof EventPlayerInput) {
				EventPlayerInput event = (EventPlayerInput)e;
				if(!mc.player.isCollidedVertically&&mc.player.motionY>0) {
					event.setSneak(true);
				}
			}*/
			/*if(e instanceof EventUpdate) {
				PlayerUtils.Strafe(1);
				mc.player.motionY = PlayerUtils.InputY();
				mc.player.setPosition(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY, mc.player.posZ + mc.player.motionZ);
				PlayerUtils.freeze();
			}
			if(e instanceof EventHandleTeleport) {
				EventHandleTeleport event = (EventHandleTeleport)e;
				event.setCancellTeleporting(true);
			}*/
			break;
		}
		
		
		
		case "AAC":
		{
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				event.y -= 1E-10;
				PlayerUtils.vClip2(999, true);
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
				PlayerUtils.vClip2(-1E-10, true);
				PlayerUtils.vClip(-1E-10);
				toggle();
			}
			break;
		}
		
		
		
		case "Matrix":
		{
			if(e instanceof EventMotion) {
				EventMotion event = (EventMotion)e;
				
				Vec3d lpos = mc.player.getPositionVector();
				PlayerUtils.Strafe(7);
				mc.player.posX+=mc.player.motionX;
				mc.player.posZ+=mc.player.motionZ;
				PlayerUtils.vClip2(0, true);
				PlayerUtils.vClip2(0, true);
				mc.player.setPosition(lpos.x, lpos.y, lpos.z);
				PlayerUtils.freeze();
				
				event.y -= 1E-10;
			}
			break;
		}
		
		}
		super.onEvent(e);
	}
}