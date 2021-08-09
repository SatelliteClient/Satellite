package com.github.satellite.features.module.movement;

import org.omg.CORBA.BooleanSeqHelper;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
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
	NumberSetting timer;

	@Override
	public void init() {
		addSetting(this.antiOutside = new BooleanSetting("AntiOutside", null, true));
		addSetting(this.timer = new NumberSetting("Timer", null, 1, 0.1, 2, .01));
		super.init();
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			setEnable(false);
			return;
		}
		tickTimer = 0-10;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		ClientUtils.setTimer(1f);
		mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
		super.onDisable();
	}

	boolean flag, flag1, flag2;

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventMotion && e.isPre()) {
			EventMotion event = (EventMotion)e;
			MovementUtils.freeze();
			flag1 = MovementUtils.isInsideBlock();
			boolean f = MovementUtils.isMoving();
			/*
			if (!flag1)
				MovementUtils.move(MovementUtils.InputX(), Math.min(f?0:1, MovementUtils.InputY()), MovementUtils.InputZ());
			else
				MovementUtils.clip(MovementUtils.InputX(), MovementUtils.InputY(), MovementUtils.InputZ());*/
			MovementUtils.move(MovementUtils.InputX(), Math.min(f?0:1, MovementUtils.InputY()), MovementUtils.InputZ());
			ClientUtils.setTimer(1f);
			if (mc.player.collided && flag1) {
				ClientUtils.setTimer((float)timer.value);
				MovementUtils.clip(MovementUtils.InputX(), MovementUtils.InputY(), MovementUtils.InputZ());
			}
			if (!MovementUtils.isMoving())
				mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			else
				mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			if (flag) {
				mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				event.y += .1;
				flag = false;
				MovementUtils.vClip(.01);
				event.x = mc.player.lastTickPosX;
				event.z = mc.player.lastTickPosZ;
			}
			flag2 = flag;
			if (!flag1 && mc.player.collidedVertically) {
				flag = true;
			}
			if (antiOutside.enable && flag1 && !MovementUtils.isInsideBlock())
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
			MovementUtils.freeze();
		}
	}
}