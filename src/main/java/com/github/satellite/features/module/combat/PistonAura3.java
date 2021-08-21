package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.*;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PistonAura3 extends Module {

	public PistonAura3() {
		super("PistonAura3", Keyboard.KEY_NONE, Category.COMBAT);
	}

	NumberSetting range, delay1, delay2, min, thread;

	@Override
	public void init() {
		super.init();
		addSetting(this.range = new NumberSetting("Range", null, 5.2, 0, 15, .1));
		addSetting(this.delay1 = new NumberSetting("ChangeDelay", null, 5, 0, 20, 1));
		addSetting(this.delay2 = new NumberSetting("PlaceDelay", null, 2, -1, 100, 1));
		addSetting(this.min = new NumberSetting("MinDamage", null, 21, 0, 100, .5));
		addSetting(this.thread = new NumberSetting("Thread", null, 1, 0, 10, 1));
	}

	int progress = 0;

	EnumFacing facing;
	int sleep;

	List<PA> attackable;
	List<PA> attack;

	@Override
	public void onEnable() {
		progress = 0;
		attackable = new ArrayList<>();
		attack = new CopyOnWriteArrayList<>();
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {

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

			if (attackable.isEmpty() || mc.player.ticksExisted%(int)Math.max(1, delay1.value/Math.max(1, thread.value))==0) {
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

		}
		if (e instanceof EventMotion && e.isPre()) {
			if (!TargetUtils.findTarget(range.value)) return;

			EventMotion event = (EventMotion)e;


			if (!attackable.isEmpty() && attack.size()<thread.value && mc.player.ticksExisted%(int)Math.max(1, delay1.value/Math.max(1, thread.value))==0) {
				attack.add(attackable.get(0));
			}

			for (PA pa : attack) {
				pa.updatePA(event);
				if (pa.stage>delay1.value+1) {
					attack.remove(pa);
				}
			}

			for (Entity et : mc.world.loadedEntityList) {
				if (et instanceof EntityEnderCrystal) {
					if (!TargetUtils.canAttack(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), et.getPositionVector().add(0.0D, 1.7D, 0.0D))) continue;
					mc.playerController.attackEntity(mc.player, et);
					mc.player.swingArm(EnumHand.MAIN_HAND);
					mc.world.removeEntity(et);
				}
			}
		}
		if (e instanceof EventRenderWorld) {
			for (PA pa : attack) {
				Color col = ThemeManager.getTheme().dark(2);
				RenderUtils.drawBlockBox(pa.crystal, ColorUtils.alpha(col, 0x40));
				RenderUtils.drawBlockBox(pa.piston, ColorUtils.alpha(col, 0x40));
				if (pa.power != null)
					RenderUtils.drawBlockBox(pa.power, ColorUtils.alpha(col, 0x40));
				RenderUtils.drawBlockBox(pa.crystal.offset(pa.pistonFacing), ColorUtils.alpha(new Color(0xffffff), 0x20));
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

//	public static final BlockPos[] pistonoff = new BlockPos[] {
//			new BlockPos(1, 0, 0),
//			new BlockPos(0, 1, 0),
//			new BlockPos(0, 0, 1),
//			new BlockPos(-1, 0, 0),
//			new BlockPos(0, -1, 0),
//			new BlockPos(0, 0, -1),
//			new BlockPos(0, -1, -1),
//			new BlockPos(-1, -1, 0),
//			new BlockPos(0, -1, 1),
//			new BlockPos(1, -1, 0),
//			new BlockPos(-1, 0, -1),
//			new BlockPos(1, 0, -1),
//			new BlockPos(1, 0, 1),
//			new BlockPos(-1, 0, 1),
//			new BlockPos(0, 1, -1),
//			new BlockPos(1, 1, 0),
//			new BlockPos(0, 1, 1),
//			new BlockPos(-1, 1, 0),
//			new BlockPos(1, -1, 1),
//			new BlockPos(1, -1, -1),
//			new BlockPos(1, 1, -1),
//			new BlockPos(-1, -1, -1),
//			new BlockPos(-1, -1, 1),
//			new BlockPos(-1, 1, -1),
//			new BlockPos(-1, 1, 1),
//			new BlockPos(1, 1, 1)
//	};

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
					boolean d = false;
					for (Entity e : mc.world.getEntitiesWithinAABBExcludingEntity(null, Block.FULL_BLOCK_AABB.offset(pispos.offset(pistonFacing)))) {
						if (e == mc.player) {
							d = true;
							break;
						}
					}
					if (d) continue;
					if (crypos.offset(EnumFacing.UP).equals(pispos)) continue;
					if (crypos.offset(pistonFacing).equals(pispos)) continue;
					if (mc.player.getDistanceSq((double)pispos.getX() + 0.5D, (double)pispos.getY() + 0.5D, (double)pispos.getZ() + 0.5D) >= 64.0D) continue;
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
//						if (true)
//							continue;
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
			int pitem = InventoryUtils.pickItem(33, false);
			int powtem1 = InventoryUtils.pickItem(152, false);
			int powtem2 = InventoryUtils.pickItem(76, false);
			int cryst = InventoryUtils.pickItem(426, false);

			if (delay2.value == -1) {
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
			}

			if (delay2.value == -1) {
				mc.getConnection().sendPacket(new CPacketPlayer.Rotation(event.yaw, event.pitch, mc.player.onGround));
			}
			if (stage == delay2.value || (stage == 0 && delay2.value == -1)) {
				int pow = powtem1==-1?powtem2:powtem1;
				if (power != null && pow == -1) return;
				if (pitem == -1 || cryst == -1) return;
				BlockUtils.doPlaceSilent(BlockUtils.isPlaceable(piston, 0, false), pitem, false);

				if (power != null) {
					BlockUtils.doPlaceSilent(BlockUtils.isPlaceable(power, 0, false), pow, false);
				}

				BlockUtils.doPlaceSilent(BlockUtils.isPlaceable(piston, 0, false), pitem, false);

				CrystalUtils.placeCrystalSilent(crystal);

				if (power != null) {
					BlockUtils.doPlaceSilent(BlockUtils.isPlaceable(power, 0, false), pow, false);
				}
			}

			mc.world.setBlockToAir(piston);
			mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, piston, EnumFacing.UP));
			if (power != null) {
				mc.world.setBlockToAir(power);
				mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, power, EnumFacing.UP));
			}

			if (delay2.value == -1) {
				mc.getConnection().sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
			}
			stage ++;
			mc.getConnection().sendPacket(new CPacketHeldItemChange(InventoryUtils.getSlot()));
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
