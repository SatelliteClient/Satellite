package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.CrystalUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.TargetUtils;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class PistonAura3 extends Module {

	public PistonAura3() {
		super("PistonAura3", Keyboard.KEY_NONE, Category.COMBAT);
	}

	NumberSetting range, delay1, delay2, min;
	BooleanSetting cheackPiston;

	@Override
	public void init() {
		super.init();
		addSetting(this.range = new NumberSetting("Range", null, 5.2, 0, 15, .1));
		addSetting(this.delay1 = new NumberSetting("ChangeDelay", null, 5, 0, 20, 1));
		addSetting(this.delay2 = new NumberSetting("PlaceDelay", null, 2, 0, 100, 1));
		addSetting(this.min = new NumberSetting("MinDamage", null, 21, 0, 100, .5));
	}

	int progress = 0;

	EnumFacing facing;
	int sleep;

	List<PA> attackable;

	@Override
	public void onEnable() {
		progress = 0;
		attackable = new ArrayList<>();
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			InventoryUtils.push();
			if (!TargetUtils.findTarget(range.value)) return;
			Entity entity = TargetUtils.currentTarget;
			Entity player= TargetUtils.currentTarget;
			BlockPos playerPos = new BlockPos(TargetUtils.currentTarget);

			int range = (int) this.range.value;

			if (attackable.isEmpty() || attackable.get(0).stage>delay1.value) {
				attackable = new ArrayList<>();
				for (int dx = -range; dx <= range; dx++) {
					for (int dy = -range; dy <= range; dy++) {
						for (int dz = -range; dz <= range; dz++) {
							BlockPos pos = new BlockPos(mc.player).add(dx, dy, dz);
							if (player.getDistanceSq(pos) > range*range) continue;
							boolean b = false;
							for (BlockPos off : pistonoff) {
								if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockObsidian) {
									b = true;
									break;
								}
								if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockEmptyDrops) {
									b = true;
									break;
								}
							}
							if (!b) continue;
							float damage = getDamage(new Vec3d(pos).add(.5, 0, .5));
							if (damage < min.value) continue;
							PA pa = new PA(pos, damage);
							if (!pa.canPA()) continue;
							attackable.add(pa);
						}
					}

					attackable.sort(new Comparator<PA>() {
						@Override
						public int compare(PA a, PA b) {
							if (a == null && b == null)
								return 0;

							if (a.damage<b.damage)
								return 1;

							if (a.damage>b.damage)
								return -1;

							return 0;
						}
					});
				}
			}

			InventoryUtils.pop();
		}
		if (e instanceof EventMotion) {
			InventoryUtils.push();
			if (!TargetUtils.findTarget(range.value)) return;

			EventMotion event = (EventMotion)e;

			if (!attackable.isEmpty()) {
				attackable.get(0).updatePA(event);
			}

			for (Entity et : mc.world.loadedEntityList) {
				if (et instanceof EntityEnderCrystal) {
					mc.playerController.attackEntity(mc.player, et);
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}

			InventoryUtils.pop();
		}
		if (e instanceof EventRenderWorld) {
			if (!attackable.isEmpty()) {
				RenderUtils.drawBlockBox(attackable.get(0).crystal, ColorUtils.alpha(new Color(0xff0000), 0x20));
				RenderUtils.drawBlockBox(attackable.get(0).piston, ColorUtils.alpha(new Color(0x00ff00), 0x20));
				RenderUtils.drawBlockBox(attackable.get(0).power, ColorUtils.alpha(new Color(0x0000ff), 0x20));
				RenderUtils.drawBlockBox(attackable.get(0).crystal.offset(attackable.get(0).pistonFacing), ColorUtils.alpha(new Color(0xffffff), 0x20));
			}
		}
		super.onEvent(e);
	}

	public float getDamage(Vec3d pos) {
		Entity entity = TargetUtils.currentTarget;
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

	public static final BlockPos[] pistonoff = new BlockPos[] {
			/*y = -1*/
			new BlockPos(-1, -1, -1),
			new BlockPos(0, -1, -1),
			new BlockPos(1, -1, -1),
			new BlockPos(-1, -1, 0),
			new BlockPos(0, -1, 0),
			new BlockPos(1, -1, 0),
			new BlockPos(-1, -1, 1),
			new BlockPos(0, -1, 1),
			new BlockPos(1, -1, 1),
			/*y = 0*/
			new BlockPos(-1, 0, -1),
			new BlockPos(0, 0, -1),
			new BlockPos(1, 0, -1),
			new BlockPos(-1, 0, 0),
			new BlockPos(0, 0, 0),
			new BlockPos(1, 0, 0),
			new BlockPos(-1, 0, 1),
			new BlockPos(0, 0, 1),
			new BlockPos(1, 0, 1),
			/*y = 1*/
			new BlockPos(-1, 1, -1),
			new BlockPos(0, 1, -1),
			new BlockPos(1, 1, -1),
			new BlockPos(-1, 1, 0),
			new BlockPos(0, 1, 0),
			new BlockPos(1, 1, 0),
			new BlockPos(-1, 1, 1),
			new BlockPos(0, 1, 1),
			new BlockPos(1, 1, 1)
	};

	public class PA {

		public BlockPos pos;
		public BlockPos crystal;
		public BlockPos power;
		public EnumFacing pistonFacing;
		public BlockPos piston;
		public float damage;

		public PA(BlockPos pos, float damage) {
			this.pos = pos;
			this.damage = damage;
			this.stage = 0;
		}

		public boolean canPA() {
			double pist = .5;
			for (EnumFacing f : EnumFacing.values()) {
				BlockPos crypos = pos.offset(f);
				if (!mc.world.isAirBlock(crypos)) continue;
				if (!mc.world.isAirBlock(crypos.offset(EnumFacing.UP))) continue;
				if (!(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian) && !(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops)) continue;
				if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(crypos))) continue;
				this.crystal = crypos;
				this.pistonFacing = rotateHantaigawa(f);
				if (pistonFacing == EnumFacing.UP) continue;
				if (!mc.world.isAirBlock(crypos.offset(pistonFacing))) this.damage /= 2;

				for (BlockPos off : pistonoff) {
					BlockPos pispos = crystal.add(off);
					if (pispos.equals(crypos)) continue;
					if (crypos.offset(EnumFacing.UP).equals(pispos)) continue;
					if (crypos.offset(pistonFacing).equals(pispos)) continue;
					if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(pispos))) continue;
					if (BlockUtils.isPlaceable(pispos, 0, true) == null) continue;
					if (pistonFacing.getDirectionVec().getX()>0 && pispos.getX()-pist > crypos.getX()) continue;
					if (pistonFacing.getDirectionVec().getY()>0 && pispos.getY()-pist > crypos.getY()) continue;
					if (pistonFacing.getDirectionVec().getZ()>0 && pispos.getZ()-pist > crypos.getZ()) continue;
					if (pistonFacing.getDirectionVec().getX()<0 && pispos.getX()+pist < crypos.getX()) continue;
					if (pistonFacing.getDirectionVec().getY()<0 && pispos.getY()+pist < crypos.getY()) continue;
					if (pistonFacing.getDirectionVec().getZ()<0 && pispos.getZ()+pist < crypos.getZ()) continue;
					if (!mc.world.isAirBlock(pispos)) continue;
					if (!mc.world.isAirBlock(pispos.offset(pistonFacing))) continue;
					if (pispos.getY()<crystal.getY() && pistonFacing.getAxis() != Axis.Y) continue;
					this.piston = pispos;

					for (EnumFacing fa : EnumFacing.values()) {
						BlockPos powpos = pispos.offset(fa);
						if (pispos.equals(powpos)) continue;
						if (pispos.offset(pistonFacing).equals(powpos)) continue;
						if (crypos.equals(powpos)) continue;
						if (crypos.offset(EnumFacing.UP).equals(powpos)) continue;
						if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(powpos))) continue;

						if (pistonFacing.getDirectionVec().getX()>0 && powpos.getX()-pist > crypos.getX()) continue;
						if (pistonFacing.getDirectionVec().getY()>0 && powpos.getY()-pist > crypos.getY()) continue;
						if (pistonFacing.getDirectionVec().getZ()>0 && powpos.getZ()-pist > crypos.getZ()) continue;
						if (pistonFacing.getDirectionVec().getX()<0 && powpos.getX()+pist < crypos.getX()) continue;
						if (pistonFacing.getDirectionVec().getY()<0 && powpos.getY()+pist < crypos.getY()) continue;
						if (pistonFacing.getDirectionVec().getZ()<0 && powpos.getZ()+pist < crypos.getZ()) continue;
						if (!mc.world.isAirBlock(powpos)) continue;
						this.power = powpos;

						return true;
					}
				}
			}

			return false;
		}

		public int stage;

		public void updatePA(EventMotion event) {
			int obsiitem = InventoryUtils.pickItem(49, false);
			int pitem = InventoryUtils.pickItem(33, false);
			int powtem = InventoryUtils.pickItem(152, false);
			int cryst = InventoryUtils.pickItem(426, false);

			switch (pistonFacing) {

				case SOUTH:
					event.yaw = 180;
					event.pitch = 0;
					break;
				case NORTH:
					event.yaw = 0;
					event.pitch = 0;
					break;
				case EAST:
					event.yaw = 90;
					event.pitch = 0;
					break;
				case WEST:
					event.yaw = -90;
					event.pitch = 0;
					break;
				case UP:
					event.pitch = 90;
					break;
				case DOWN:
					event.pitch = 90;
					break;

			}

			if (stage == delay2.value) {
				InventoryUtils.setSlot(pitem);
				BlockUtils.doPlace(BlockUtils.isPlaceable(piston, 0, false), true);

				InventoryUtils.setSlot(powtem);
				BlockUtils.doPlace(BlockUtils.isPlaceable(power, 0, false), true);

				InventoryUtils.setSlot(cryst);
				CrystalUtils.placeCrystal(crystal);

				BlockUtils.doPlace(BlockUtils.isPlaceable(piston, 0, false), true);
			}

			if (stage == delay2.value+1) {
				mc.world.setBlockToAir(piston);
				mc.world.setBlockToAir(power);
			}
			stage ++;
		}
	}

	public EnumFacing rotateHantaigawa(EnumFacing f)
	{
		switch (f)
		{
			case WEST:
				return EnumFacing.EAST;

			case EAST:
				return EnumFacing.WEST;

			case SOUTH:
				return EnumFacing.NORTH;

			case NORTH:
				return EnumFacing.SOUTH;

			case UP:
				return EnumFacing.DOWN;

			case DOWN:
				return EnumFacing.UP;

			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}
}
