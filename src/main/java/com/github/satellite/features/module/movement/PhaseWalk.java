package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.MovementUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class PhaseWalk extends Module {


	public PhaseWalk() {
		super("PhaseWalk", 0, Category.MOVEMENT);
	}

	ModeSetting mode;

	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "Cancel", "Cancel", "Packet");
		addSetting(mode);
		super.init();
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			toggle();
			return;
		}
		lastInSideStat = MovementUtils.isInsideBlock();
		lastPosition = new BlockPos(mc.player);
		super.onEnable();
	}

	boolean lastInSideStat;
	BlockPos lastPosition;
	double serverX;
	double serverY;
	double serverZ;
	Vec3d fix;

	int stack;

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {

			if (stack>0) {
				stack --;
			}
			if (MovementUtils.isInsideBlock() || lastInSideStat) {
				fix = mc.player.getPositionVector();
				MovementUtils.setPosition(new BlockPos(mc.player));
				MovementUtils.vClip(5);
				Vec3d motionVector = MovementUtils.getMotionVector();
				MovementUtils.move(0, -15, 0);
				MovementUtils.setMotionVector(motionVector);
				BlockPos pos = new BlockPos(mc.player).offset(EnumFacing.DOWN, mc.player.isSneaking()?2:1);
				lastPosition = pos;
				serverX = pos.getX()+.5;
				serverY = pos.getY();
				serverZ = pos.getZ()+.5;
				mc.getConnection().sendPacket(new CPacketPlayer.Position(serverX, serverY, serverZ, true));
				MovementUtils.setPosition(fix.x, fix.y, fix.z);
				lastInSideStat = mc.player.collidedVertically;
			}
			if (mc.player.posY<0) {
				mc.player.setPosition(mc.player.posX, 1.1, mc.player.posZ);
				lastInSideStat = true;
			}
		}
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			if (lastInSideStat && event.getPacket() instanceof CPacketPlayer) {
				CPacketPlayer packet = (CPacketPlayer)event.getPacket();
				if (packet.getX(serverX) != serverX) {
					e.cancel();
				}
				if (packet.getY(serverY) != serverY) {
					e.cancel();
				}
				if (packet.getZ(serverZ) != serverZ) {
					e.cancel();
				}
			}

			if (event.getPacket() instanceof SPacketPlayerPosLook && lastInSideStat) {
				MovementUtils.vClip(-8);
				toggle();
			}
			stack++;
			stack++;
		}
		if (e instanceof EventRenderWorld) {
			if (lastInSideStat)
				RenderUtils.drawBlockSolid(new BlockPos(lastPosition), EnumFacing.UP, new Color(0xff, 0xff, 0xff));
		}
	}
}