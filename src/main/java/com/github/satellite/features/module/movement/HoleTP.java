package com.github.satellite.features.module.movement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.HoleUtils;
import com.github.satellite.utils.HoleUtils.BlockSafety;
import com.github.satellite.utils.HoleUtils.HoleType;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.MovementUtils;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class HoleTP extends Module {

	public HoleTP() {
		super("HoleTP", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	NumberSetting range;
	BooleanSetting dupHole;
	BooleanSetting blockPlace;
	BooleanSetting autoDisable;

	@Override
	public void init() {
		this.range = new NumberSetting("Range", 5.2, 0, 10, .1);
		this.dupHole = new BooleanSetting("CheckDup", true);
		this.blockPlace = new BooleanSetting("AllowPlace", true);
		this.autoDisable = new BooleanSetting("AutoDisable", true);
		addSetting(range, dupHole, blockPlace, autoDisable);
		super.init();
	}

	List<BlockPos> hole = new ArrayList<>();
	List<BlockPos> duphole = new ArrayList<>();

	Entity currentTarget;

	@Override
	public void onEnable() {
		tickTimer = 0;
		super.onEnable();
	}

	int tickTimer;

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			if (tickTimer%1==0) {
				Vec3i[] checkpos = new Vec3i[] {new Vec3i(0, -1, 0), new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)};
				hole = new CopyOnWriteArrayList<BlockPos>();
				duphole = new CopyOnWriteArrayList<BlockPos>();
				int checkrange = 20;
				for (int x = -checkrange; x<=checkrange; x++) {
					for (int z = -checkrange; z<=checkrange; z++) {
						for (int y = -5; y<=5; y++) {
							final BlockPos pos = new BlockPos(x+mc.player.posX, y+mc.player.posY, z+mc.player.posZ);
							if (mc.player.getDistanceSq(pos) > checkrange*checkrange) continue;
							if (!mc.world.isAirBlock(pos)) continue;
							if (!mc.world.isAirBlock(pos.offset(EnumFacing.UP))) continue;
							if (HoleUtils.isHole(pos, true, false).getType().equals(HoleType.SINGLE)) hole.add(pos);
							if (dupHole.isEnable() && HoleUtils.isHole(pos, false, false).getType().equals(HoleType.DOUBLE)) duphole.add(pos);
						}
					}
				}
			}

			try {
				mc.world.loadedEntityList.sort((a, b) -> a.getDistance(mc.player) > b.getDistance(mc.player) ? 1 : -1);
			}catch(IllegalArgumentException exception) {}

			currentTarget = mc.world.loadedEntityList.stream().filter(ent -> (
					ent instanceof EntityLivingBase &&
							ent != mc.player &&
							ent.getDistance(mc.player) <= range.value
			)).findFirst().orElse(null);

			List<Vec3d> holes = new ArrayList<Vec3d>();
			List<Vec3d> duoholes = new ArrayList<Vec3d>();

			if (currentTarget != null) {

				hole.forEach(h -> {
					if (currentTarget.getDistanceSq(h)<Math.pow(range.value-1, 2) && !h.equals(new BlockPos(mc.player))) {
						holes.add(new Vec3d(h).add(.5, 0, .5));
					}
				});

				duphole.forEach(h -> {
					if (currentTarget.getDistanceSq(h)<Math.pow(range.value-1, 2) && !h.equals(new BlockPos(mc.player))) {
						Vec3d vec = new Vec3d(h).add(.5, 0, .5);
						duoholes.add(vec);
						holes.add(vec);
					}
				});

				if (holes != null && !holes.isEmpty()) {
					if (tickTimer%2==0) {
						Collections.shuffle(holes);
						Vec3d pos = holes.get(0);
						MovementUtils.vClip(Math.min(pos.y, mc.player.posY)+8-mc.player.posY);
						MovementUtils.vClip2(0, true);
						mc.player.setPosition(pos.x, mc.player.posY, pos.z);
						MovementUtils.vClip2(0, true);
						mc.player.setPosition(pos.x, pos.y, pos.z);
						MovementUtils.vClip2(0, true);
						int slot = InventoryUtils.getSlot();
						int obsi = InventoryUtils.pickItem(49);
						if (blockPlace.isEnable() && obsi != -1) {
							BlockPos[] block = new BlockPos[] {
									new BlockPos(0, -1, 0),
									new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1),
									new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
							for(BlockPos add : block) {
								InventoryUtils.setSlot(obsi);
								BlockPos plpos = new BlockPos(mc.player);
								BlockUtils util = BlockUtils.isPlaceable(plpos.add(add), 0, true);
								if(util != null) {
									util.doPlace(false);
								}
							}
						}
						InventoryUtils.setSlot(slot);
						mc.player.motionY = 0;
						if (autoDisable.isEnable()) {
							toggle();
						}
					}
				}
			}

			++ tickTimer;
		}
		if (e instanceof EventRenderWorld) {
			for (BlockPos pos : duphole) {
				pos = pos.add(0, -1, 0);
				RenderUtils.drawBlockSolid(pos, EnumFacing.UP, ColorUtils.alpha(ThemeManager.getTheme().light(2), 0xff));
				RenderUtils.drawBlockSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN, ColorUtils.alpha(ThemeManager.getTheme().light(2), 0xff));
			}
			for (BlockPos pos : hole) {
				pos = pos.add(0, -1, 0);
				RenderUtils.drawBlockSolid(pos, EnumFacing.UP, ColorUtils.alpha(ThemeManager.getTheme().light(1), 0xff));
				RenderUtils.drawBlockSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN, ColorUtils.alpha(ThemeManager.getTheme().light(1), 0xff));
			}
		}
		super.onEvent(e);
	}

}