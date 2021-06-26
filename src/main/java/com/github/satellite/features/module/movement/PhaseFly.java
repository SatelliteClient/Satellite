package com.github.satellite.features.module.movement;

import org.omg.CORBA.BooleanSeqHelper;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;

import com.github.satellite.utils.WorldUtils;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;

public class PhaseFly extends Module {


	public PhaseFly() {
		super("PhaseFly", 0, Category.MOVEMENT);
	}

	float yaw = 0;
	int tickTimer = 0;

	BooleanSetting antiOutside;

	@Override
	public void init() {
		this.antiOutside = new BooleanSetting("AntiOutside", true);
		addSetting(antiOutside);
		super.init();
	}

	@Override
	public void onEnable() {
		mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
		if (mc.player.movementInput.jump) {
			mc.player.move(MoverType.SELF, 0, 1, 0);
			if (mc.player.collidedVertically) {
				tickTimer = 1;
				flag = true;
				flag1 = true;
			}
		}
		super.onEnable();
	}

	boolean flag;
	boolean flag1;
	boolean flag2;

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			MovementUtils.freeze();
			double x = 0, y = 0, z = 0;
			mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			double x1 = mc.player.posX;
			double z1 = mc.player.posZ;
			if (flag) {
				ClientUtils.setTimer(1);
			}
			if (MovementUtils.InputY() > 0) {
				if (MovementUtils.isInsideBlock()) {
					y += 1;
					tickTimer = 0;
				}else {
					double y1 = mc.player.posY;
					mc.player.move(MoverType.SELF, 0, 1, 0);
					if((mc.player.posY - y1) < 1 || flag) {
						BlockPos pos = new BlockPos(mc.player);
						mc.player.setPosition(pos.getX()+.5, mc.player.posY, pos.getZ()+.5);
						flag = true;
						if (tickTimer%2==0) {
							mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
							event.y+=.01;
							if (tickTimer%3==0) {
								y += 1;
								flag = false;
							}
						}
					}else {
						tickTimer = 0;
					}
					y += mc.player.posY - y1;
					tickTimer++;
				}
			}else if(MovementUtils.InputY() < 0) {
				if (MovementUtils.isInsideBlock()) {
					y--;
				}else {
					double y1 = mc.player.posY;
					mc.player.move(MoverType.SELF, 0, -1, 0);
					y += mc.player.posY - y1;
				}
			}

			MovementUtils.Strafe(1);
			if (MovementUtils.isInsideBlock()) {
				x = mc.player.motionX;
				z = mc.player.motionZ;
			}else {
				MovementUtils.move();
				x += mc.player.posX - x1;
				z += mc.player.posZ - z1;
			}

			event.y += 0.15;
			mc.player.setPosition(mc.player.lastTickPosX + x, mc.player.lastTickPosY + y, mc.player.lastTickPosZ + z);

			if (!MovementUtils.isInsideBlock() && antiOutside.isEnable() && flag2) {
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
			}

			flag2 = MovementUtils.isInsideBlock();
		}
	}
}