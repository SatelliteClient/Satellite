package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.CrystalUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.TargetUtils;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrystalAura extends Module {
	
	public CrystalAura() {
		super("CrystalAura", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	NumberSetting range, min, self;
	BooleanSetting antiSuicide;
	
	@Override
	public void init() {
		addSetting(this.range = new NumberSetting("Range", null, 5.2, 0, 15, .1));
		addSetting(this.min = new NumberSetting("MinDamage", null, 21, 0, 20, .1));
		addSetting(this.self = new NumberSetting("MaxSelfDmg", null, 21, 0, 20, .1));
		addSetting(this.antiSuicide = new BooleanSetting("AntiSuicide", null, false));
	}
	
	List<Crystal> crys;
	
	@Override
	public void onEnable() {
		crys = new CopyOnWriteArrayList<Crystal>();
		super.onEnable();
	}

	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onEvent(Event<?> e) {
		
		if (e instanceof EventUpdate) {
			InventoryUtils.push();
			
			if (!TargetUtils.findTarget(range.value)) return;
			Entity player = TargetUtils.currentTarget;
			//BlockPos playerPos = new BlockPos(TargetUtils.currentTarget);
			
			int range = (int) this.range.value;
			float maxdmg = 0;
			BlockPos maxpos = null;

			int crystal = InventoryUtils.pickItem(426, false);
			
			crys = new ArrayList<>();

			for (int dx = -range; dx <= range; dx++) {
				for (int dy = -range; dy <= range; dy++) {
					for (int dz = -range; dz <= range; dz++) {
						BlockPos pos = new BlockPos(mc.player).add(dx, dy, dz);
						BlockPos under = pos.offset(EnumFacing.DOWN);
						if (!(mc.world.getBlockState(under).getBlock() instanceof BlockObsidian) && !(mc.world.getBlockState(under).getBlock() instanceof BlockEmptyDrops)) continue;
						if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) continue;
						if (player.getDistanceSq(pos) > range*range) continue;
						if (!TargetUtils.canAttack(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), new Vec3d(pos).add(.5D, 1.7D, .5D))) continue;//1.7D
						double damage = CrystalUtils.getDamage(new Vec3d(pos).add(.5D, 0.0D, .5D), TargetUtils.currentTarget);
						double selfdmg = CrystalUtils.getDamage(new Vec3d(pos).add(.5D, 0.0D, .5D), null);
						
						if (damage < min.value) continue;
						if (!mc.world.isAirBlock(pos.offset(EnumFacing.UP))) continue;
						//if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(pos))) continue;
						boolean col = false;
						for (Entity ea : mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, Block.FULL_BLOCK_AABB.offset(pos))) {
							if (!(ea instanceof EntityEnderCrystal)) {
								col = true;
								break;
							}
						}
						if (col) continue;
						
						if (damage > maxdmg && selfdmg < self.getValue()) {
							crys.add(new Crystal(pos, damage, selfdmg));
						}
					}
				}
			}

			crys.sort((a, b) -> {
				if (a == null && b == null)
					return 0;

				if (a.damage-a.selfDamage>b.damage-b.selfDamage)
					return -1;

				if (a.damage-a.selfDamage<b.damage-b.selfDamage)
					return 1;
				return 0;
			});
			

			if (!crys.isEmpty())
				maxpos = crys.get(0).pos;
			if (maxpos != null) {
				InventoryUtils.setSlot(crystal);
				CrystalUtils.placeCrystal(maxpos);
			}

			for (Entity target : mc.world.loadedEntityList) {
				if (player.getDistance(target) > range)
					continue;
                if (target instanceof EntityEnderCrystal) {
        			if (!crys.isEmpty()) {
        				Crystal c = crys.get(0);
        				if (c.pos.equals(new BlockPos(target))) 
        					mc.getConnection().sendPacket(new CPacketUseEntity(target));
        			}
                }
            }
			
			
			InventoryUtils.pop();
		}
		if (e instanceof EventRenderWorld) {
			if (!crys.isEmpty()) {
				Crystal c = crys.get(0);
				RenderUtils.drawBlockBox(c.pos.offset(EnumFacing.DOWN), ColorUtils.alpha(ThemeManager.getTheme().light(1), 0x40));
				RenderUtils.drawBlockBox(c.pos.offset(EnumFacing.DOWN), ColorUtils.alpha(ThemeManager.getTheme().light(1), 0x40));
				RenderUtils.drawString(mc.fontRenderer, String.valueOf(c.damage), new Vec3d(c.pos).add(.5, -.5, .5), Color.white, 2);
			}
		}
		
		super.onEvent(e);
	}
    
	public static class Crystal {
		
		public BlockPos pos;
		public double damage;
		public double selfDamage;
		
		public Crystal(BlockPos pos, double damage, double selfDamage) {
			this.pos = pos;
			this.damage = damage;
			this.selfDamage = selfDamage;
		}
	}
}
