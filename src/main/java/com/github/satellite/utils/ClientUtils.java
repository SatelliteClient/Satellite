package com.github.satellite.utils;

import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.features.module.render.Notification;
import com.github.satellite.mixin.client.AccessorMinecraft;
import com.github.satellite.mixin.client.AccessorTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class ClientUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public boolean konas() {
		try {
			Class.forName("com.konasclient.client.3");
			return true;
		} catch (ClassNotFoundException|NoClassDefFoundError classNotFoundException) {
			return false;
		}
	}

	public boolean machinelite() {
		try {
			Class.forName("com.lite.machinelite.MachineLite");
			return true;
		} catch (ClassNotFoundException|NoClassDefFoundError classNotFoundException) {
			return false;
		}
	}
	
	public static void setTimer(float d) {
		d*=20F;
		Timer timer = ((AccessorMinecraft)mc).getTimer();
		((AccessorTimer)timer).setTickLength(1000.0F / d);
	}
	
	public static float getTimer() {
		Timer timer = ((AccessorMinecraft)mc).getTimer();
		return 1000.0F / ((AccessorTimer)timer).getTickLength() / 20;
	}

	public static void addChatMsg(String str) {
		mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(str));
	}

	public static void addNotification(String str) {
		Notification gui = (Notification) ModuleManager.getModulebyClass(Notification.class);
		if (gui != null) {
			gui.addPanel(str, "");
		}
	}

	public static void addNotification(String str1, String str2) {
		Notification gui = (Notification) ModuleManager.getModulebyClass(Notification.class);
		if (gui != null) {
			gui.addPanel(str1, str2);
		}
	}
}
