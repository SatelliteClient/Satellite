package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.omg.CORBA.BooleanSeqHelper;

public class HoleKicker extends Module {

	public HoleKicker() {
		super("HoleKicker", Keyboard.KEY_NONE, Category.COMBAT);
	}

	NumberSetting range;
	BooleanSetting autoDisable;
	BooleanSetting fill;

	@Override
	public void init() {
		super.init();
		this.range = new NumberSetting("Range", null, 5.2, 0, 10, .1);
		this.autoDisable = new BooleanSetting("AutoDisable", true);
		addSetting(range, autoDisable);
	}

	int progress = 0;

	EnumFacing facing;

	@Override
	public void onEnable() {
		progress = 0;
		super.onEnable();
	}

	@Override
	public void onEvent(Event<?> e) {
		if (e instanceof EventUpdate) {
			for(Entity entity : mc.world.loadedEntityList) {
				if (entity == mc.player) continue;
				if (mc.player.getDistance(entity) > range.value) continue;

				if (entity instanceof EntityPlayer) {
					int piston = InventoryUtils.pickItem(33, false);
					int power = InventoryUtils.pickItem(152, false);

					BlockPos pos = new BlockPos(entity).offset(EnumFacing.UP);

					if (piston == -1 || power == -1 || progress < 2) {
						facing = getFacing(pos);
						if (facing != null) {
							progress++;
						}else {
							progress = 0;
						}
						return;
					}
					mc.player.inventory.currentItem = piston;
					BlockUtils event = BlockUtils.isPlaceable(pos.offset(facing), 0, true);
					if (event != null) {
						if (!event.doPlace(true)) {
							return;
						}


						mc.player.inventory.currentItem = power;
						for (EnumFacing f : EnumFacing.values()) {
							pos = new BlockPos(entity).offset(EnumFacing.UP).offset(facing);
							event = BlockUtils.isPlaceable(pos.offset(f), 0, true);
							if (BlockUtils.doPlace(event, true)) {
								BlockUtils.doPlace(BlockUtils.isPlaceable(new BlockPos(entity), 0, false), false);
								toggle();
								return;
							}
						}
					}
				}
			}
		}
		if (e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;

			if (progress > 0) {
				switch (facing) {

					case NORTH:
						event.yaw = 180;
						break;
					case SOUTH:
						event.yaw = 0;
						break;
					case WEST:
						event.yaw = 90;
						break;
					case EAST:
						event.yaw = -90;
						break;

				}
				event.pitch = 0;
				progress++;
			}
		}
		super.onEvent(e);
	}

	public EnumFacing getFacing(BlockPos position) {
		for (EnumFacing f : EnumFacing.values()) {
			final BlockPos pos = new BlockPos(position);

			if (pos.offset(f).getY() != position.getY())
				continue;

			if (!mc.world.isAirBlock(pos.offset(f, -1).offset(EnumFacing.DOWN))) {
				if (mc.world.isAirBlock(pos.offset(f, -1))) {
					if (mc.world.isAirBlock(pos.offset(f))) {
						return f;
					}
				}
			}
		}
		return null;
	}
}
