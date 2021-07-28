package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.network.play.client.CPacketEntityAction;

public class PhaseFly extends Module {

	public PhaseFly() {
		super("PhaseFly", 0, Category.MOVEMENT);
	}

	BooleanSetting antiOutside;

	@Override
	public void init() {
		this.antiOutside = new BooleanSetting("AntiOutside", null, true);
		addSetting(antiOutside);
		super.init();
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			setEnable(false);
			return;
		}
		mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
		onEvent(new EventMotion(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
		super.onDisable();
	}

	boolean flag;
	boolean flag1;

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventMotion && e.isPre()) {
			EventMotion event = (EventMotion)e;
			MovementUtils.freeze();
			boolean f = MovementUtils.isMoving();
			if (!flag1)
				MovementUtils.move(MovementUtils.InputX(), Math.min(f?0:1, MovementUtils.InputY()), MovementUtils.InputZ());
			else
				MovementUtils.clip(MovementUtils.InputX(), MovementUtils.InputY(), MovementUtils.InputZ());
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
			if (!flag1 && mc.player.collidedVertically) {
				flag = true;
			}
			if (antiOutside.enable && flag1 && !MovementUtils.isInsideBlock())
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
			flag1 = MovementUtils.isInsideBlock();
		}
	}
}