package com.github.satellite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

import com.github.satellite.Satellite;

public class TargetUtils {

	protected static Minecraft mc = Satellite.mc;
	
	public static Entity currentTarget;
	
	public static boolean Player = true;
	public static boolean Animal = false;
	
	public static boolean findTarget(double range) {
		mc.world.loadedEntityList.sort((a, b) -> a.getDistance(mc.player)>b.getDistance(mc.player)?1:-1);
		for (Entity e : mc.world.loadedEntityList) {
			if (e == mc.player) continue;
			if (e.getDistance(mc.player)>range) continue;
			if (!(e instanceof EntityLivingBase)) continue;
			if (e instanceof EntityPlayer && !Player) continue;
			if (e instanceof EntityAnimal && !Animal) continue;
			currentTarget = e;
			return true;
		}
		return false;
	}
	
	public static double getDistance(@Nullable Entity target) {
		return currentTarget==null?0:currentTarget.getDistance(target==null?mc.player:target);
	}
	
}
