package com.github.satellite.features.module.movement;

import java.awt.Color;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventJump;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPacket;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PhaseWalk extends Module {


	public PhaseWalk() {
		super("PhaseWalk", 0, Category.MOVEMENT);
	}

	BooleanSetting align;
	NumberSetting offset;
	NumberSetting factor;
	NumberSetting offsetY;
	NumberSetting checkY;

	@Override
	public void init() {
		this.align = new BooleanSetting("Align", true);
		this.offset = new NumberSetting("offset", 0.005, 0, 10, 0.001);
		this.factor = new NumberSetting("factor", 100, 1, 10000, 1);
		this.offsetY = new NumberSetting("packetY", .5, -10, 10, .1);
		this.checkY = new NumberSetting("checkY", 5, 0, 10, 1);
		addSetting(offset, align, factor, offsetY, checkY);
		super.init();
	}

	@Override
	public void onEnable() {
		lastInSideStat = MovementUtils.isInsideBlock();
		lastPosition = mc.player.getPositionVector();
		super.onEnable();
	}

	boolean lastInSideStat;
	Vec3d lastPosition;
	Vec3d fix;

	int stack;

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventMotion) {
			ClientUtils.setTimer(1);
			EventMotion event = (EventMotion)e;
			int tp = 0;
			int packet = 0;
			if (stack>0) {
				stack --;
			}
			if (MovementUtils.isInsideBlock() || lastInSideStat) {
				//mc.player.width = 0.01F;
				if (e.isPre()) {
					fix = mc.player.getPositionVector();
					double y = mc.player.posY;
					MovementUtils.setPosition(new BlockPos(mc.player));
					MovementUtils.vClip(5);
					Vec3d motionVector = MovementUtils.getMotionVector();
					MovementUtils.move(0, -15, 0);
					MovementUtils.setMotionVector(motionVector);
					event.setPosition(new BlockPos(mc.player).offset(EnumFacing.DOWN, 0).offset(EnumFacing.DOWN, mc.player.isSneaking()?2:1));
					if (event.y < lastPosition.y) {
						event.x = lastPosition.x+.5;
						event.z = lastPosition.z+.5;
					}
					lastPosition = new Vec3d(new BlockPos(event.x, event.y, event.z));
				}else {
					MovementUtils.setPosition(fix.x, fix.y, fix.z);
					lastInSideStat = true;
				}
				//mc.player.motionY = 0;
				mc.player.onGround = true;
				lastInSideStat = true;
			}
			if (mc.player.posY<0) {
				mc.player.setPosition(mc.player.posX, 1.1, mc.player.posZ);
				lastInSideStat = true;
			}
		}
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			if (event.getPacket() instanceof SPacketPlayerPosLook && lastInSideStat) {
				//ClientUtils.addNotification("Stack Detected");
				//toggle();
			}
			stack++;
			stack++;
		}
		if (e instanceof EventRenderWorld) {
			RenderUtils.drawBlockBox(new BlockPos(lastPosition), new Color(0xff, 0xff, 0xff, 0xff));
		}
	}
}