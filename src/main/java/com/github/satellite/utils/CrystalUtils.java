package com.github.satellite.utils;

import javax.annotation.Nullable;

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
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
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
		}else if (InventoryUtils.pickItem(426, false) != -1) {
			InventoryUtils.setSlot(InventoryUtils.pickItem(426, false));
			return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
		}
		return EnumActionResult.FAIL;
	}

	public static boolean placeCrystalSilent(BlockPos pos) {
		pos.offset(EnumFacing.DOWN);
		double dx=(pos.getX()+0.5-mc.player.posX);
		double dy=(pos.getY()+0.5-mc.player.posY) - .5 -mc.player.getEyeHeight();
		double dz=(pos.getZ()+0.5-mc.player.posZ);

		double x=getDirection2D(dz, dx);
		double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));
		int slot = InventoryUtils.pickItem(426, false);
		if (slot == -1 && mc.player.inventory.offHandInventory.get(0).getItem() != Items.END_CRYSTAL) return false;

		Vec3d vec = getVectorForRotation(-y, x-90);
		if (mc.player.inventory.offHandInventory.get(0).getItem() == Items.END_CRYSTAL) {
			mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.OFF_HAND, 0, 0, 0));
		}else if (InventoryUtils.pickItem(426, false) != -1) {
			mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
			mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
		}
		return true;
	}

	public static double getDamage(Vec3d pos, @Nullable Entity target) {
		Entity entity = target == null ? mc.player : target;
		float damage = 6.0F;
		float f3 = damage * 2.0F;
		Vec3d vec3d = pos;

		if (!entity.isImmuneToExplosions())
		{
			double d12 = entity.getDistance(pos.x, pos.y, pos.z) / (double)f3;

			if (d12 <= 1.0D)
			{
				double d5 = entity.posX - pos.x;
				double d7 = entity.posY + (double)entity.getEyeHeight() - pos.y;
				double d9 = entity.posZ - pos.z;
				double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

				if (d13 != 0.0D)
				{
					d5 = d5 / d13;
					d7 = d7 / d13;
					d9 = d9 / d13;
					double d14 = (double)mc.world.getBlockDensity(pos, entity.getEntityBoundingBox());
					double d10 = (1.0D - d12) * d14;
					return (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D));
				}
			}
		}
		return 0;
	}

}
