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
	BooleanSetting autoDisable;

	@Override
	public void init() {
		this.range = new NumberSetting("Range", 5.2, Integer.MIN_VALUE, 10, .1);
		addSetting(range, autoDisable);
		super.init();
	}

	List<BlockPos> hole = new ArrayList<>();

	Entity currentTarget;

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			if (mc.player.ticksExisted%4==0) {
				Vec3i[] checkpos = new Vec3i[] {new Vec3i(0, -1, 0), new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)};
				hole = new CopyOnWriteArrayList<BlockPos>();
				int checkrange = 50;
				for (int x = -checkrange; x<=checkrange; x++) {
					for (int z = -checkrange; z<=checkrange; z++) {
						for (int y = -5; y<=5; y++) {
							final BlockPos pos = new BlockPos(x+mc.player.posX, y+mc.player.posY, z+mc.player.posZ);
							if (mc.player.getDistanceSq(pos) > checkrange*checkrange) continue;
							if (!mc.world.isAirBlock(pos)) continue;
							if (!mc.world.isAirBlock(pos.offset(EnumFacing.UP))) continue;
							if (HoleUtils.isHole(pos, true, false).getType().equals(HoleType.SINGLE)) hole.add(pos);
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

			List<BlockPos> holes = new ArrayList<BlockPos>();

			if (currentTarget != null) {

				hole.forEach(h -> {
					if (currentTarget.getDistanceSq(h)<Math.pow(range.value-1, 2) && !h.equals(mc.player.getPosition())) {
						holes.add(h);
					}
				});

				holes.sort((a, b) -> currentTarget.getDistanceSq(a) > currentTarget.getDistanceSq(b) ? 1 : -1);
				if (holes != null && !holes.isEmpty()) {
					if (mc.player.ticksExisted%1==0) {
						Collections.shuffle(holes);
						BlockPos pos = holes.get(0);
						MovementUtils.vClip(Math.min(pos.getY(), mc.player.posY)+8-mc.player.posY);
						MovementUtils.vClip2(0, true);
						mc.player.setPosition(pos.getX()+.5, mc.player.posY, pos.getZ()+.5);
						MovementUtils.vClip2(0, true);
						mc.player.setPosition(pos.getX()+.5, pos.getY(), pos.getZ()+.5);
						MovementUtils.vClip2(0, true);
						mc.player.motionY = 0;
						if (autoDisable.isEnable()) {
							toggle();
						}
					}
				}
			}
		}
		if (e instanceof EventRenderWorld) {
			for (BlockPos pos : hole) {
				pos = pos.add(0, -1, 0);
				RenderUtils.drawBlockSolid(pos, EnumFacing.UP, ColorUtils.alpha(ThemeManager.getTheme().light(1), 0xff));
				RenderUtils.drawBlockSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN, ColorUtils.alpha(ThemeManager.getTheme().light(1), 0xff));
			}
		}
		super.onEvent(e);
	}

}