package com.github.satellite.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockUtils {
	
	protected static Minecraft mc = Minecraft.getMinecraft();
	
	public BlockPos pos;
	public int a;
	public EnumFacing f;
	public double dist;
	public double rotx, roty;
	
	public static BlockUtils isPlaceable(BlockPos pos, double dist, boolean Collide) {
		BlockUtils event = new BlockUtils(pos, 0, null, dist);

		if(!isAir(pos))
			return null;

		//if(!itemblock.canPlaceBlockOnSide(mc.world, mc.player.inventoryContainer.getInventory().get(mc.player.inventory.currentItem), pos, player, itemstack))
		//    return EnumActionResult.FAIL;
		if(!(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock)) return null;

		for(EnumFacing f : EnumFacing.values())
		{
			if(!isAir(new BlockPos(pos.getX()-f.getDirectionVec().getX(), pos.getY()-f.getDirectionVec().getY(), pos.getZ()-f.getDirectionVec().getZ())))
			{
				event.f=f;

				AxisAlignedBB axisalignedbb = ((ItemBlock)mc.player.inventory.getCurrentItem().getItem()).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, pos);

				if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), (Entity)null))
				{
					return null;
				}

				return event;
			}
		}
		if(isRePlaceable(pos))
		{
			event.f=EnumFacing.UP;
			event.pos.offset(EnumFacing.UP);
			pos.offset(EnumFacing.DOWN);

			AxisAlignedBB axisalignedbb = ((ItemBlock)mc.player.inventory.getCurrentItem().getItem()).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, pos);

			if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), (Entity)null))
			{
				return null;
			}

			return event;
		}
		return null;
	}

	public static boolean isAir(BlockPos pos)
	{
		Block block = mc.world.getBlockState(pos).getBlock();
		return block.isReplaceable(mc.world, pos) && block instanceof BlockAir;
	}
	public static boolean isRePlaceable(BlockPos pos)
	{
		Block block = mc.world.getBlockState(pos).getBlock();
		return block.isReplaceable(mc.world, pos) && !(block instanceof BlockAir);
	}
	
	public BlockUtils(BlockPos pos, int a, EnumFacing f, double dist) {
		this.pos=pos;
		this.a=a;
		this.f=f;
		this.dist=dist;
	}

	public void doPlace(boolean swing) {
		double dx=((pos.getX()+0.5-mc.player.posX) - ((double)f.getDirectionVec().getX())/2 );
		double dy=((pos.getY()+0.5-mc.player.posY) - ((double)f.getDirectionVec().getY())/2 )-mc.player.getEyeHeight();
		double dz=((pos.getZ()+0.5-mc.player.posZ) - ((double)f.getDirectionVec().getZ())/2 );

		double x=getDirection2D(dz, dx);
		double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));
		
		Vec3d vec = getVectorForRotation(-y, x-90);
		
		this.roty=-y;
		this.rotx=x-90;
		
		EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, new BlockPos(pos.getX() - f.getDirectionVec().getX(), pos.getY() - f.getDirectionVec().getY(), pos.getZ() - f.getDirectionVec().getZ()), f, vec, EnumHand.MAIN_HAND);

        if (enumactionresult == EnumActionResult.SUCCESS)
        {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            return;
        }
	}
	
	public void doBreak() {
		mc.playerController.onPlayerDamageBlock(new BlockPos(pos.getX() - f.getDirectionVec().getX(), pos.getY() - f.getDirectionVec().getY(), pos.getZ() - f.getDirectionVec().getZ()), f);
	}
	
    protected final Vec3d getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
    
    protected final Vec3d getVectorForRotation(double pitch, double yaw)
    {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
    
	public static double getDirection2D(double dx, double dy)
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
}
