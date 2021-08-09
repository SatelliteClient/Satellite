package com.github.satellite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.github.satellite.Satellite;
import com.github.satellite.features.module.combat.PistonAura3.PA;

public class TargetUtils {

	protected static Minecraft mc = Satellite.mc;

	public static Entity currentTarget;

	public static boolean Player = true;
	public static boolean Animal = false;

	public static boolean findTarget(double range) {
		mc.world.loadedEntityList.sort(new Comparator<Entity>() {
			@Override
			public int compare(Entity a, Entity b) {
				if (a == null && b == null)
					return 0;

				if (a.getDistance(mc.player)<b.getDistance(mc.player))
					return -1;

				if (a.getDistance(mc.player)>b.getDistance(mc.player))
					return 1;

				return 0;
			}
		});
		for (Entity e : mc.world.loadedEntityList) {
			if (e == mc.player) continue;
			if (e.getDistance(mc.player)>range) continue;
			if (!(e instanceof EntityLivingBase)) continue;
			if (e instanceof EntityPlayer && !Player) continue;
			if (e instanceof EntityAnimal && !Animal) continue;
			if (!(e instanceof EntityPlayer)) continue;
			currentTarget = e;
			return true;
		}
		return false;
	}

	public static double getDistance(@Nullable Entity target) {
		return currentTarget==null?0:currentTarget.getDistance(target==null?mc.player:target);
	}

	public static boolean canAttack(Vec3d vec, Vec3d pos) {
		boolean flag = mc.world.rayTraceBlocks(vec, pos, false, true, false) == null;
		double d0 = 36.0D;

		if (!flag)
		{
			d0 = 9.0D;
		}
		if (vec.squareDistanceTo(pos) < d0) {
			return true;
		}
		return false;
	}
	//height * 0.85F
}
