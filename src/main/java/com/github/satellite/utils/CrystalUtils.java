package com.github.satellite.utils;

import com.github.satellite.Satellite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CrystalUtils {

	protected static Minecraft mc = Satellite.mc;

	public static final AxisAlignedBB CRYSTAL_AABB = new AxisAlignedBB(0, 0, 0, 1, 2, 1);

	public static boolean canPlace(BlockPos pos) {
		if (!(mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops)) {
			return false;
		}
		if (!(mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian)) {
			return false;
		}
		if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(0, 0, 0, 1, 2, 1).offset(pos), (Entity)null))
		{
			return false;
		}
		return true;
	}

	public static EnumActionResult doPlace(BlockPos pos) {
		double dx=(pos.getX()+0.5-mc.player.posX);
		double dy=(pos.getY()-1+0.5-mc.player.posY) - .5 -mc.player.getEyeHeight();
		double dz=(pos.getZ()+0.5-mc.player.posZ);

		double x=getDirection2D(dz, dx);
		double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));

		Vec3d vec = getVectorForRotation(-y, x-90);
		return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, mc.player.getActiveHand());
	}

	protected static final double getDirection2D(double dx, double dy)
	{
		double d;
		if(dy==0) {
			if(dx>0) {
				d = 90;
			}else {
				d = -90;
			}
		}else {
			d = Math.atan(dx/dy) * 57.2957796;
			if(dy<0) {
				if(dx>0) {
					d+=180;
				}else {
					if(dx<0) {
						d -= 180;
					}else {
						d = 180;
					}
				}
			}
		}
		return d;
	}

	protected static final Vec3d getVectorForRotation(double pitch, double yaw)
	{
		float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float)Math.PI));
		float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float)Math.PI));
		float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
		float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
		return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
	}

	public static EnumActionResult placeCrystal(BlockPos pos) {
		pos.offset(EnumFacing.DOWN);
		double dx=(pos.getX()+0.5-mc.player.posX);
		double dy=(pos.getY()+0.5-mc.player.posY) - .5 -mc.player.getEyeHeight();
		double dz=(pos.getZ()+0.5-mc.player.posZ);

		double x=getDirection2D(dz, dx);
		double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));

		Vec3d vec = getVectorForRotation(-y, x-90);
		if (mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass())) {
			return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.OFF_HAND);
		}else if (InventoryUtils.pickItem(426) != -1) {
			InventoryUtils.setSlot(InventoryUtils.pickItem(426));
			return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
		}
		return EnumActionResult.FAIL;
	}

}
