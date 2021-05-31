package com.github.satellite.utils;

import com.github.satellite.mixin.client.AccessorMinecraft;
import com.github.satellite.mixin.client.AccessorTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class ClientUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();
	
	public static void setTimer(float d) {
		d*=20F;
		Timer timer = ((AccessorMinecraft)mc).getTimer();
		((AccessorTimer)timer).setTickLength(1000.0F / d);
	}
	
	public static float getTimer() {
		Timer timer = ((AccessorMinecraft)mc).getTimer();
		return 1000.0F / ((AccessorTimer)timer).getTickLength() / 20;
	}
}
