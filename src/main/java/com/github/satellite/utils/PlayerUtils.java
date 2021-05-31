package com.github.satellite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketPlayer;

public class PlayerUtils {
	
	protected static Minecraft mc = Minecraft.getMinecraft();
	
	public static void vClip(double d) {
		mc.player.setPosition(mc.player.posX, mc.player.posY + d, mc.player.posZ);
	}

	public static void vClip2(double d, boolean onGround) {
		mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY+d, mc.player.posZ, onGround));
	}
	
	public static int InputY() {
		return (mc.player.movementInput.jump ? 1 : 0) + (mc.player.movementInput.sneak ? -1 : 0);
	}
	
	public static double getSpeed() {
		return Math.sqrt(Math.pow(mc.player.motionX, 2)+Math.pow(mc.player.motionZ, 2));
	}
	
	public static double toRadian(double d) {
		return d * Math.PI / 180;
	}
	
	public static void Strafe(double d) {
		float Forward = (mc.player.movementInput.forwardKeyDown?1:0)-(mc.player.movementInput.backKeyDown?1:0);
		float Strafing = (mc.player.movementInput.rightKeyDown?1:0)-(mc.player.movementInput.leftKeyDown?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.player.rotationYaw);
		
		if(Forward==0&&Strafing==0) {d=0;}

		mc.player.motionX=0;
		mc.player.motionZ=0;

		mc.player.motionX=Math.sin(r)*d;
		mc.player.motionZ=Math.cos(r)*d;
	}
	
	public static void strafe(float r) {
		mc.player.motionX=Math.sin(r)*getSpeed();
		mc.player.motionZ=Math.cos(r)*getSpeed();
	}
	
	public static void Move() {
		mc.player.move(MoverType.SELF, mc.player.motionX, mc.player.motionY, mc.player.motionZ);
	}
	
	public static void freeze() {
		mc.player.motionX=0;
		mc.player.motionY=0;
		mc.player.motionZ=0;
	}

	public static boolean isMoving() {
		float Forward = (mc.player.movementInput.forwardKeyDown?1:0)-(mc.player.movementInput.backKeyDown?1:0);
		float Strafing = (mc.player.movementInput.rightKeyDown?1:0)-(mc.player.movementInput.leftKeyDown?1:0);
		return Forward!=0||Strafing!=0;
	}
}