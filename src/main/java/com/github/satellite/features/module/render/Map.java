package com.github.satellite.features.module.render;

import com.github.satellite.features.module.ModuleGuiObject;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

public class Map extends ModuleGuiObject {
	public Map() {
		super("Map", 0, Category.RENDER, -104, -104, 100, 100, true, true);
	}
	
	@Override
	public void onEvent(Event e) {
		if(e instanceof EventRenderGUI) {
			int x=mc.displayWidth/2 - 100 - 4;
			int y=new ScaledResolution(mc).getScaledHeight()/1-100-4-(mc.currentScreen instanceof GuiChat?15:0);
			
			int line=1;
			Gui.drawRect(x-line, y-line, x+100+line, y+100+line, ThemeManager.getTheme().dark(3).getRGB());
			Gui.drawRect(x+0, y+0, x+100, y+100, ThemeManager.getTheme().dark(0).getRGB());
			
			int c1= ThemeManager.getTheme().dark(3).getRGB();
			int c2= ThemeManager.getTheme().dark(4).getRGB();
			
			for(net.minecraft.entity.Entity e1 : mc.world.loadedEntityList) {
				float dx = (int) (mc.getRenderManager().viewerPosX - e1.posX);
				float dz = (int) (mc.getRenderManager().viewerPosZ - e1.posZ);
				float dy = (int) (mc.getRenderManager().viewerPosY - e1.posY);
				
				boolean isPlayer = e1 == mc.player;
				
				double r = Math.atan2(dx, dz);
				double d = Math.sqrt(dx*dx+dz*dz);
				
				r += Math.toRadians(mc.player.rotationYaw);
				
				dx = (float) (Math.sin(r)*d);
				dz = (float) (Math.cos(r)*d);
				
				dz *= mc.getRenderManager().playerViewX/90;
				dz += dy;
				//dz /= 5;
				
				if(Math.abs(dx)>49 || Math.abs(dz)>49)
					continue;
				
				int textwidth = mc.fontRenderer.getStringWidth(e1.getName());

				double x1 = x+50+dx;
				double y1 = y+50+dz;
				RenderUtils.drawRect(x1-1, y1-1, x1+1, y1+1, isPlayer? ThemeManager.getTheme().dark(3).getRGB(): ThemeManager.getTheme().dark(4).getRGB());
			}

			double h = mc.getRenderManager().playerViewX/90*40;
			double px = 4;
			RenderUtils.drawRect(x+px-2, y+49+h, x+px, y+50+h, ThemeManager.getTheme().light(1).getRGB());
			RenderUtils.drawRect(x+px-2, y+49-h, x+px, y+50-h, ThemeManager.getTheme().light(2).getRGB());
			RenderUtils.drawRect(x+px-2, y+49+h/2, x+px, y+50+h/2, ThemeManager.getTheme().light(1).getRGB());
			RenderUtils.drawRect(x+px-2, y+49-h/2, x+px, y+50-h/2, ThemeManager.getTheme().light(2).getRGB());
			RenderUtils.drawRect(x+px-2, y+49, x+px, y+50, ThemeManager.getTheme().light(0).getRGB());
		}
		super.onEvent(e);
	}
}
