package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.PlayerUtils;

import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;

public class PhaseFly extends Module {
	
	
	public PhaseFly() {
		super("PhaseFly", 0, Category.MOVEMENT);
	}

	float yaw = 0;
	int tickTimer = 0;
	
	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			PlayerUtils.freeze();
			double x = 0, y = 0, z = 0;
			mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			if (PlayerUtils.InputY() > 0) {
				if (PlayerUtils.isInsideBlock()) {
					y += 1;
					tickTimer = 0;
				}else {
					double y1 = mc.player.posY;
					mc.player.move(MoverType.SELF, 0, 1, 0);
					if(mc.player.collidedVertically) {
						if (tickTimer%2==0) {
							mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
							event.y+=.01;
							if (tickTimer%3==0) {
								y += 1;
							}
						}
					}else {
						tickTimer = 0;
					}
					y += mc.player.posY - y1;
					tickTimer++;	
				}
			}else if(PlayerUtils.InputY() < 0) {
				y--;
			}
			
			PlayerUtils.Strafe(1);
			x = mc.player.motionX;
			z = mc.player.motionZ;
			
			event.y += 0.15;
			mc.player.setPosition(mc.player.lastTickPosX + x, mc.player.lastTickPosY + y, mc.player.lastTickPosZ + z);
		}
	}
}