package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class HoleKicker extends Module {
	
	public HoleKicker() {
		super("HoleKicker", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
    @Override
    public void init() {
    	super.init();
    }

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventUpdate) {
			for(Entity entity : mc.world.loadedEntityList) {
				if(entity == mc.player) continue;
				
				if(entity instanceof EntityLivingBase) {
				}
			}
		}
		super.onEvent(e);
	}
	
	/**
	 * @param entity
	 * @return
	 */
	public boolean canKickable(Entity entity) {
		BlockPos head = new BlockPos(new Vec3d(entity.posX, entity.posY+entity.height, entity.posZ));
		if(!(mc.world.getBlockState(head).getBlock() instanceof BlockAir)) {
			return false;
		}
		
		if(mc.world.getBlockState(slide(head, EnumFacing.NORTH)).getBlock() instanceof BlockAir) {
			head.offset(EnumFacing.NORTH);
			return true;
		}
		else if(mc.world.getBlockState(slide(head, EnumFacing.SOUTH)).getBlock() instanceof BlockAir) {
			head.offset(EnumFacing.SOUTH);
			return true;
		}
		else if(mc.world.getBlockState(slide(head, EnumFacing.WEST)).getBlock() instanceof BlockAir) {
			head.offset(EnumFacing.WEST);
			return true;
		}
		else if(mc.world.getBlockState(slide(head, EnumFacing.EAST)).getBlock() instanceof BlockAir) {
			head=head.offset(EnumFacing.EAST);
			return true;
		}
		else return false;
	}
	
	private BlockPos slide(BlockPos pos, EnumFacing f) {
		return new BlockPos(
				pos.getX()+f.getDirectionVec().getX(),
				pos.getY()+f.getDirectionVec().getY(),
				pos.getZ()+f.getDirectionVec().getZ()
				);
	}
}
