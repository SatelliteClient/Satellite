package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.CrystalUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.TargetUtils;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.omg.CORBA.BooleanSeqHelper;


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

			int pitem = InventoryUtils.pickItem(33, false);
			int cryst = InventoryUtils.pickItem(426, false);
			int powtem1 = InventoryUtils.pickItem(152, false);
			int powtem2 = InventoryUtils.pickItem(76, false);
			if (pitem == -1 || cryst == -1 || (powtem1 == -1 && powtem2 == -1)) {
				ClientUtils.addChatMsg("\u00A77[Satellite] \u00A74Item Not Found ");
				toggle();
			}
			if (!TargetUtils.findTarget(range.value)) return;
			Entity entity = TargetUtils.currentTarget;
			Entity player= TargetUtils.currentTarget;
			BlockPos playerPos = new BlockPos(TargetUtils.currentTarget);

			int range = (int) this.range.value;

			/*InventoryUtils.setSlot(InventoryUtils.pickItem(49, false));
			BlockUtils.doPlace(BlockUtils.isPlaceable(playerPos.offset(EnumFacing.EAST, 1), 0, true), false);
			BlockUtils.doPlace(BlockUtils.isPlaceable(playerPos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP), 0, true), false);
			BlockUtils.doPlace(BlockUtils.isPlaceable(playerPos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP, 2), 0, true), false);
			BlockUtils.doPlace(BlockUtils.isPlaceable(playerPos.offset(EnumFacing.UP, 2), 0, true), false);*/

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
							double damage = CrystalUtils.getDamage(new Vec3d(pos).add(.5, 0, .5), TargetUtils.currentTarget);
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
					if (et.getDistance(mc.player) > range.value) continue;
					mc.playerController.attackEntity(mc.player, et);
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}

			InventoryUtils.pop();
		}
		if (e instanceof EventRenderWorld) {
			if (!attackable.isEmpty()) {
				Color col = ThemeManager.getTheme().dark(2);
				RenderUtils.drawBlockBox(attackable.get(0).crystal, ColorUtils.alpha(col, 0x20));
				RenderUtils.drawBlockBox(attackable.get(0).piston, ColorUtils.alpha(col, 0x20));
				if (attackable.get(0).power != null)
					RenderUtils.drawBlockBox(attackable.get(0).power, ColorUtils.alpha(col, 0x20));
				RenderUtils.drawBlockBox(attackable.get(0).crystal.offset(attackable.get(0).pistonFacing), ColorUtils.alpha(new Color(0xffffff), 0x20));
			}
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
		public double damage;

		public PA(BlockPos pos, double damage) {
			this.pos = pos;
			this.damage = damage;
			this.stage = 0;
		}

		public boolean canPA() {
			boolean isTorch = InventoryUtils.pickItem(76, false) != -1;
			double pist = .5;
			for (EnumFacing f : EnumFacing.values()) {
				BlockPos crypos = pos.offset(f);
				//check
				if (!mc.world.isAirBlock(crypos)) continue;
				if (!mc.world.isAirBlock(crypos.offset(EnumFacing.UP))) continue;
				if (!TargetUtils.canAttack(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), new Vec3d(crypos).add(.5D, 1.7D, .5D))) continue;
				if (!(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian) && !(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops)) continue;
				if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(crypos))) continue;
				if (mc.player.getDistanceSq((double)crypos.getX() + 0.5D, (double)crypos.getY() + 0.5D, (double)crypos.getZ() + 0.5D) >= 64.0D) continue;
				//check2
				this.crystal = crypos;
				this.pistonFacing = rotateHantaigawa(f);
				if (pistonFacing == EnumFacing.DOWN) continue;
				if (!mc.world.isAirBlock(crypos.offset(pistonFacing))) continue;

				for (BlockPos off : pistonoff) {
					BlockPos pispos = crystal.add(off);
					if (pispos.equals(crypos)) continue;
					if (crypos.offset(EnumFacing.UP).equals(pispos)) continue;
					if (crypos.offset(pistonFacing).equals(pispos)) continue;
					EnumFacing sfac = EnumFacing.getDirectionFromEntityLiving(pispos, mc.player);
					if (sfac.getAxis() == Axis.Y) {
						if (pistonFacing != sfac) continue;
					}
					if (pistonFacing.getAxis() == Axis.Y) {
						if (pistonFacing != sfac) continue;
					}
					this.power = null;
					if (mc.world.isBlockPowered(pispos)) {
						if (BlockUtils.isPlaceable(pispos, 0, true) == null) continue;
					}else {
						for (EnumFacing fa : EnumFacing.values()) {
							BlockPos powpos = pispos.offset(fa);
							if (pispos.equals(powpos)) continue;
							if (pispos.offset(pistonFacing).equals(powpos)) continue;
							if (crypos.equals(powpos)) continue;
							if (crypos.offset(EnumFacing.UP).equals(powpos)) continue;
							if (mc.player.getDistanceSq((double)powpos.getX() + 0.5D, (double)powpos.getY() + 0.5D, (double)powpos.getZ() + 0.5D) >= 64.0D) continue;
							if (BlockUtils.isPlaceable(powpos, 0, true) == null) continue;

							if (pistonFacing.getDirectionVec().getX()>0 && powpos.getX()-pist > crypos.getX()) continue;
							if (pistonFacing.getDirectionVec().getY()>0 && powpos.getY()-pist > crypos.getY()) continue;
							if (pistonFacing.getDirectionVec().getZ()>0 && powpos.getZ()-pist > crypos.getZ()) continue;
							if (pistonFacing.getDirectionVec().getX()<0 && powpos.getX()+pist < crypos.getX()) continue;
							if (pistonFacing.getDirectionVec().getY()<0 && powpos.getY()+pist < crypos.getY()) continue;
							if (pistonFacing.getDirectionVec().getZ()<0 && powpos.getZ()+pist < crypos.getZ()) continue;
							if (!mc.world.isAirBlock(powpos)) continue;
							this.power = powpos;
						}
						if (power == null) continue;
					}
					if (mc.player.getDistanceSq((double)pispos.getX() + 0.5D, (double)pispos.getY() + 0.5D, (double)pispos.getZ() + 0.5D) >= 64.0D) continue;
					if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(pispos))) continue;
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
					return true;
				}
			}
			return false;
		}

		public int stage;

		public void updatePA(EventMotion event) {
			int obsiitem = InventoryUtils.pickItem(49, false);
			int pitem = InventoryUtils.pickItem(33, false);
			int powtem1 = InventoryUtils.pickItem(152, false);
			int powtem2 = InventoryUtils.pickItem(76, false);
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

				if (power != null) {
					InventoryUtils.setSlot(powtem1);
					InventoryUtils.setSlot(powtem2);
					BlockUtils.doPlace(BlockUtils.isPlaceable(power, 0, false), true);
				}

				InventoryUtils.setSlot(pitem);
				BlockUtils.doPlace(BlockUtils.isPlaceable(piston, 0, false), true);

				InventoryUtils.setSlot(cryst);
				CrystalUtils.placeCrystal(crystal);

				if (power != null) {
					InventoryUtils.setSlot(powtem1);
					InventoryUtils.setSlot(powtem2);
					BlockUtils.doPlace(BlockUtils.isPlaceable(power, 0, false), true);
				}
			}

			if (stage == delay2.value+1) {
				InventoryUtils.setSlot(cryst);
				mc.world.setBlockToAir(piston);
				if (power != null) {
					mc.world.setBlockToAir(power);
				}
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
