package com.github.satellite.ui;

import com.github.satellite.Satellite;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Comparator;

public class HUD {

	public Minecraft mc = Minecraft.getMinecraft();
	
	public HUD() {
		
	}
	
	public static class ModuleComparator implements Comparator<Module> {

		@Override
		public int compare(Module o1, Module o2) {
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getDisplayName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getDisplayName()))
				return -1;
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getDisplayName()) < Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getDisplayName()))
				return 1;
			return 0;
		}
		
	}
	
	public void draw() {
		EventRenderGUI e = new EventRenderGUI();
		Satellite.onEvent(e);

		GL11.glPushMatrix();

		float hue = (System.currentTimeMillis() % 4000) / 4000f;
	    
		ScaledResolution sr = new ScaledResolution(mc);
		
		FontRenderer fr = mc.fontRenderer;
		
		Collections.sort(ModuleManager.modules, new ModuleComparator());

		int i=0;
		for(Module m : ModuleManager.modules) {
			if(!m.isEnable())
				continue;
			
			int offset = (fr.FONT_HEIGHT + 4) * i;

			String name = m.getDisplayName();

			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(name) - 8, offset, sr.getScaledWidth(), offset + fr.FONT_HEIGHT + 4, 0x90000000);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(name) - 6, offset, sr.getScaledWidth() - fr.getStringWidth(m.getDisplayName()) - 8, offset + fr.FONT_HEIGHT + 4, ColorUtils.HSBtoRGB((float) (hue+(offset*0.00314159265359)), 0.8F, 0.97F));
			fr.drawString(name, sr.getScaledWidth() - fr.getStringWidth(name) - 3.5F, offset + 2.5F, 0xffffffff, false);

			i++;
		}

	    GL11.glPopMatrix();
	}
}
