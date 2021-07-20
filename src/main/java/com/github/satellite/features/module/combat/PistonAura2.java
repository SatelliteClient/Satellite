package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.CrystalUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.TargetUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class PistonAura2 extends Module {
	
	public PistonAura2() {
		super("PistonAura2", Keyboard.KEY_NONE, Category.COMBAT);
	}

	NumberSetting range;
	BooleanSetting checkPiston;
	
    @Override
    public void init() {
    	super.init();
    	this.range = new NumberSetting("Range", 5.2, Integer.MIN_VALUE, 100, .1);
    	this.checkPiston = new BooleanSetting("checkPiston", true);
    	addSetting(range, checkPiston);
    }
    
    int progress = 0;
    
	EnumFacing facing;
	int sleep;

    @Override
    public void onEnable() {
    	progress = 0;
    	super.onEnable();
    }
    
	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			InventoryUtils.push();
			TargetUtils.findTarget(range.value);
			if (TargetUtils.getDistance(null) > range.value) return;
			Entity entity = TargetUtils.currentTarget;
			
			if (entity instanceof EntityPlayer) {
				int obsidian = InventoryUtils.pickItem(49, false);
				int piston = InventoryUtils.pickItem(33, false);
				int power = InventoryUtils.pickItem(152, false);
				int crystal = InventoryUtils.pickItem(426, false);
				
				if (obsidian == -1 || piston == -1 || power == -1 || (crystal == -1 && !mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass()))) {
					ClientUtils.addChatMsg("\u00A77[Satellite] \u00A74Item Not Found ");
					toggle();
					return;
				}

				BlockPos pos = new BlockPos(entity);

				ClientUtils.addChatMsg("" + progress);
				if (sleep>0) {
					sleep --;
				}else {
					switch (progress) {
					case 0:
						
						facing = getFacing(pos);
						
						if (facing == null) return;
						
						// base
						BlockPos Pos = new BlockPos(pos).offset(facing).offset(EnumFacing.DOWN);

						InventoryUtils.setSlot(obsidian);
						
						BlockUtils event = BlockUtils.isPlaceable(Pos, 0, false);
						if(event != null) {
							event.doPlace(true);
						}
						
						Pos = new BlockPos(pos).offset(facing);

						InventoryUtils.setSlot(obsidian);
						
						event = BlockUtils.isPlaceable(Pos, 0, false);
						
						if(event != null) {
							event.doPlace(true);
						}
						
						if (mc.world.isAirBlock(Pos))
						
						break;
					case 1:
						// piston
						Pos = new BlockPos(pos).offset(facing).offset(facing);
						InventoryUtils.setSlot(piston);
						event = BlockUtils.isPlaceable(Pos, 0, false);
						BlockUtils.doPlace(event, true);
						BlockUtils.doBreak(Pos, EnumFacing.UP);
						mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, Pos, EnumFacing.UP));

						Pos = new BlockPos(pos).offset(facing).offset(EnumFacing.UP);					
						InventoryUtils.setSlot(crystal);
						CrystalUtils.placeCrystal(Pos);
						
						Pos = new BlockPos(pos).offset(facing).offset(facing).offset(facing);
						InventoryUtils.setSlot(power);
						event = BlockUtils.isPlaceable(Pos, 0, false);
						BlockUtils.doPlace(event, true);

						mc.world.setBlockToAir(new BlockPos(pos).offset(facing).offset(facing));
						mc.world.setBlockToAir(new BlockPos(pos).offset(facing).offset(facing).offset(facing));
						
						progress ++;
						sleep = 1;
						break;
					case 2:
						int i = 0;
						for (Entity target : mc.world.loadedEntityList) {
							if (entity.getDistance(target) > range.value)
								continue;
		                    if (target instanceof EntityEnderCrystal) {
		                        mc.playerController.attackEntity(mc.player, target);
		                        i++;
		                    }
		                }
						
						progress = 1;
					}	
				}
			}
			InventoryUtils.pop();
		}
		if (e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			
			event.pitch = 90;
		}
		super.onEvent(e);
	}
	
	public EnumFacing getFacing(BlockPos position) {
		for (EnumFacing f : EnumFacing.values()) {
			if (f.getAxis() == Axis.Y) continue;
			BlockPos pos = new BlockPos(position).offset(f, 2);
			
			if (mc.world.isAirBlock(pos)) {
				return f;
			}
		}
		return null;
	}
}
