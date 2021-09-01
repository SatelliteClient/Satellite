package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.setting.Setting;
import com.github.satellite.utils.HoleUtils;
import com.github.satellite.utils.HoleUtils.HoleType;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Anchor extends Module {

	public Anchor() {
		super("Anchor", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	NumberSetting range;
	BooleanSetting autoDisable, onStop, onSneak;

	@Override
	public void init() {
		addSetting(this.range = new NumberSetting("Range", null, 5.2, 0, 10, .1));
		addSetting(this.autoDisable = new BooleanSetting("AutoDisable", null, true));
		addSetting(new Setting("Triggers", null, null));
		addSetting(this.onStop = new BooleanSetting("OnStop", null, true));
		addSetting(this.onSneak = new BooleanSetting("OnSneak", null, true));
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
		if (e instanceof EventMotion) {
			if (e.isPre()) return;
			EventMotion event = (EventMotion)e;
			if (((
					!mc.player.movementInput.forwardKeyDown && !mc.player.movementInput.backKeyDown && !mc.player.movementInput.leftKeyDown && !mc.player.movementInput.rightKeyDown && MovementUtils.InputY() == 0 && onStop.enable) ||
					mc.player.movementInput.sneak && onSneak.enable
			) && HoleUtils.isHole(new BlockPos(mc.player), true, true).getType() != HoleType.SINGLE) {
				List<BlockPos> poss = new ArrayList<BlockPos>();
				BlockPos vec = new BlockPos(mc.player);
				for (double dx = -range.value; dx<=range.value; dx++) {
					for (double dy = -range.value; dy<=range.value; dy++) {
						for (double dz = -range.value; dz<=range.value; dz++) {
							BlockPos pos = vec.add(dx, dy, dz);
							if (!mc.world.isAirBlock(pos)) continue;
							if (!mc.world.isAirBlock(pos.offset(EnumFacing.UP))) continue;
							if (mc.world.isAirBlock(pos.offset(EnumFacing.DOWN))) continue;
							if (!HoleUtils.isHole(pos, true, true).getType().equals(HoleType.SINGLE)) continue;
							if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().shrink(0.0625D)).isEmpty()) continue;
							RayTraceResult res = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(pos), false, false, true);
							RayTraceResult res1 = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(pos).add(0, 1.8, 0), false, false, true);
							if (mc.player.getEntityBoundingBox().maxY > pos.getY()+1.8 && !mc.world.getCollisionBoxes(null, new AxisAlignedBB(pos).offset(0, 1.8, 0)).isEmpty()) continue;
							if (mc.player.getEntityBoundingBox().minY < pos.getY()) continue;
							if (mc.playerController.gameIsSurvivalOrAdventure()) {
								if (mc.player.getEntityBoundingBox().minY < pos.getY()+1) continue;
								if (res == null || res.typeOfHit == Type.BLOCK || res1 == null || res1.typeOfHit == Type.BLOCK) continue;
							}else {
								if ((res == null || res.typeOfHit == Type.BLOCK) && (res1 == null || res1.typeOfHit == Type.BLOCK)) continue;
							}
							poss.add(pos);
						}
					}
				}
				poss.sort((a, b) -> mc.player.getDistanceSqToCenter(a)>mc.player.getDistanceSqToCenter(b)?1:-1);
				if (!poss.isEmpty()) {
					BlockPos pos = poss.get(0);
					if (HoleUtils.isHole(pos, true, true).getType().equals(HoleType.SINGLE)) {
						MovementUtils.setPosition(pos.getX()+.5, mc.player.posY, pos.getZ()+.5);
						MovementUtils.vClip2(0, true);
						MovementUtils.setPosition(pos);
						MovementUtils.vClip2(0, true);
						MovementUtils.freeze();

						if (autoDisable.enable) {
							toggle();
						}
					}
				}
			}
		}
		super.onEvent(e);
	}

}