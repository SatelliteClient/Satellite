package com.github.satellite.utils;

import com.github.satellite.mixin.client.AccessorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public static Vec3d getMotionVector() {
		return new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
	}

	public static void setMotionVector(Vec3d vec) {
		mc.player.motionX = vec.x;
		mc.player.motionY = vec.y;
		mc.player.motionZ = vec.z;
	}

	public static void setPosition(double x, double y, double z) {
		mc.player.setPosition(x, y, z);
	}

	public static void setPosition(BlockPos pos) {
		mc.player.setPosition(pos.getX()+.5, pos.getY(), pos.getZ()+.5);
	}

	public static void clip(double x, double y, double z) {
		mc.player.setPosition(mc.player.posX+x, mc.player.posY+y, mc.player.posZ+z);
	}

	public static void yClip(double y) {
		mc.player.setPosition(mc.player.posX, y, mc.player.posZ);
	}

	public static void vClip(double d) {
		mc.player.setPosition(mc.player.posX, mc.player.posY + d, mc.player.posZ);
	}

	public static void vClip2(double d, boolean onGround) {
		mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY+d, mc.player.posZ, onGround));
	}

	public static int InputY() {
		return (mc.player.movementInput.jump ? 1 : 0) + (mc.player.movementInput.sneak ? -1 : 0);
	}

	public static double InputX() {
		if (!isMoving()) return 0;
		float Forward = (mc.player.movementInput.forwardKeyDown?1:0)-(mc.player.movementInput.backKeyDown?1:0);
		float Strafing = (mc.player.movementInput.rightKeyDown?1:0)-(mc.player.movementInput.leftKeyDown?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.player.rotationYaw);
		return Math.sin(r);
	}

	public static double InputZ() {
		if (!isMoving()) return 0;
		float Forward = (mc.player.movementInput.forwardKeyDown?1:0)-(mc.player.movementInput.backKeyDown?1:0);
		float Strafing = (mc.player.movementInput.rightKeyDown?1:0)-(mc.player.movementInput.leftKeyDown?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.player.rotationYaw);
		return Math.cos(r);
	}

	public static double getSpeed() {
		if (mc.player == null) return 0;
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

	public static void move(double x, double y, double z) {
		mc.player.move(MoverType.SELF, x, y, z);
	}

	public static void clip() {
		mc.player.setPosition(mc.player.posX+mc.player.motionX, mc.player.posY+mc.player.motionY, mc.player.posZ+mc.player.motionZ);
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

	public static float getBaseSpeed() {
		float baseSpeed = 0.2873F;
		if (mc.player.isPotionActive(Potion.getPotionById(1))) {
			int amp = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
			baseSpeed *= 1.0F + 0.2F * (amp + 1);
		}
		return baseSpeed;
	}

	public static double nextY(double y) {
		return (y - .08D) * .9800000190734863D;
	}

	public static double nextSpeed(double d) {
		return d*.9900000095367432D;
	}

	public static Vec3d getInputVec2d() {
		int x = (mc.player.movementInput.rightKeyDown?1:0)-(mc.player.movementInput.leftKeyDown?1:0);
		int z = (mc.player.movementInput.forwardKeyDown?1:0)-(mc.player.movementInput.backKeyDown?1:0);
		double r = Math.atan2(z, x)-1.57079633-Math.toRadians(mc.player.rotationYaw);
		double d = Math.sqrt(x*x+z*z);
		boolean f = d == 0;
		return new Vec3d(f?0:Math.sin(r), 0, f?0:Math.cos(r));
	}
}