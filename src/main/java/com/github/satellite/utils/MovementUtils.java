package com.github.satellite.utils;

import com.github.satellite.mixin.client.AccessorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MovementUtils {
	
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
	
	public static void move() {
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

	public static boolean isInsideBlock() {
		try {
			AxisAlignedBB playerBoundingBox = ((AccessorEntity)mc.player).boundingBox();
			for(int x = MathHelper.floor(playerBoundingBox.minX); x < MathHelper.floor(playerBoundingBox.maxX) + 1; x++) {
				for(int y = MathHelper.floor(playerBoundingBox.minY); y < MathHelper.floor(playerBoundingBox.maxY) + 1; y++) {
					for(int z = MathHelper.floor(playerBoundingBox.minZ); z < MathHelper.floor(playerBoundingBox.maxZ) + 1; z++) {
						Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z)).getBlock();
						if(block != null && !(block instanceof BlockAir)) {
							AxisAlignedBB boundingBox = block.getCollisionBoundingBox(Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z)), Minecraft.getMinecraft().world, new BlockPos(x, y, z)).offset(x, y, z);
							if(block instanceof BlockHopper)
								boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
							if(boundingBox != null && playerBoundingBox.intersects(boundingBox))
								return true;
						}
					}
				}
			}
		}catch (Exception e) {
			return false;
		}
		return false;
	}
}