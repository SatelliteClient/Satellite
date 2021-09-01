package com.github.satellite.features.module.movement;


import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class Phase extends Module {

	public Phase() {
		super("Phase", Keyboard.KEY_H, Category.MOVEMENT);
	}

	ModeSetting mode;
	NumberSetting limit;

	@Override
	public void init() {
		settings.add(mode = new ModeSetting("Mode", null, "NCP", new String[] {"Vanilla", "NCP", "AAC", "Matrix"}));
		settings.add(limit = new NumberSetting("MaxDistance", () -> mode.is("Vanilla"), 2, 0, 5, .1));
		super.init();
	}

	@Override
	public void onDisable() {
		mc.player.capabilities.isFlying = false;
		tickTimer=0;
		teleportId=0;
		clearLagTeleportId=0;
		ClientUtils.setTimer(1.0F);
		super.onDisable();
	}

	double tickTimer=0;
	int clearLagTeleportId;
	int teleportId;

	@Override
	public void onEvent(Event<?> e) {
		switch(mode.getMode()) {
			case "Vanilla":
				if (e instanceof EventMotion && e.isPost()) {
					boolean canClip = false;

					Vec3d vec = MovementUtils.getInputVec2d();
					double x, dx, dz;
					dx = vec.x;
					dz = vec.z;
					for (x=1; x<limit.value*10; x++) {
						if (!mc.world.collidesWithAnyBlock(mc.player.getEntityBoundingBox().offset(dx*x*.1, 0, dz*x*.1))) {
							canClip = true;
							break;
						}
					}
					if (mc.player.collidedHorizontally && canClip) {
						Vec3d m = MovementUtils.getMotionVector();
						mc.player.setPosition(mc.player.posX+vec.x*0.0625D, mc.player.posY, mc.player.posZ+vec.z*0.0625D);
						MovementUtils.vClip2(0, mc.player.onGround);
						mc.player.setPosition(mc.player.posX+dx*x*.1, mc.player.posY, mc.player.posZ+dz*x*.1);
						MovementUtils.setMotionVector(m);
						tickTimer = 0;
					}
				}
				break;
			case "NCP":

			case "AAC": {
				break;
			}


			case "Matrix":
			{
				if(e instanceof EventMotion && e.isPre()) {
				/*EventMotion event = (EventMotion)e;
				ClientUtils.setTimer(2.0F);
				if (mc.player.fallDistance>.5F) {
					MovementUtils.freeze();
					mc.player.fallDistance = 0;
					ClientUtils.setTimer(0.05F);
					event.onGround = true;
				}*/
					EventMotion event = (EventMotion)e;

					Vec3d lpos = mc.player.getPositionVector();
					MovementUtils.Strafe(7);
					mc.player.posX+=mc.player.motionX;
					mc.player.posZ+=mc.player.motionZ;
					MovementUtils.vClip2(0, true);
					MovementUtils.vClip2(0, true);
					mc.player.setPosition(lpos.x, lpos.y, lpos.z);
					MovementUtils.freeze();

					event.y -= 1E-10;
				}
				break;
			}

		}
		super.onEvent(e);
	}
}