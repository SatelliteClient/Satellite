package satellite.ui;

import java.util.Collections;
import java.util.Comparator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import satellite.Client;
import satellite.event.listeners.EventRenderGUI;
import satellite.futures.module.Module;
import satellite.utils.ColorUtil;
import satellite.utils.RenderUtil;

public class HUD {

	public Minecraft mc = Minecraft.getMinecraft();
	
	public HUD() {
		
	}
	
	public static class ModuleComparator implements Comparator<Module> {

		@Override
		public int compare(Module o1, Module o2) {
			if(Minecraft.getMinecraft().fontRendererObj.getStringWidth(o1.getName()) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(o2.getName()))
				return -1;
			if(Minecraft.getMinecraft().fontRendererObj.getStringWidth(o1.getName()) < Minecraft.getMinecraft().fontRendererObj.getStringWidth(o2.getName()))
				return 1;
			return 0;
		}
		
	}
	
	public void draw() {
		EventRenderGUI e = new EventRenderGUI();
		Client.onEvent(e);

		GL11.glPushMatrix();

		float hue = (System.currentTimeMillis() % 4000) / 4000f;
	    
		ScaledResolution sr = new ScaledResolution(mc);
		
		FontRenderer fr = mc.fontRendererObj;
		
		Collections.sort(Client.modules, new ModuleComparator());

		int i=0;
		for(Module m : Client.modules) {
			if(!m.isEnable())
				continue;
			
			int offset = (fr.FONT_HEIGHT + 4) * i;

			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth(), offset + fr.FONT_HEIGHT + 4, 0x90000000);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 6, offset, sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset + fr.FONT_HEIGHT + 4, ColorUtil.HSBtoRGB((float) (hue+(offset*0.00314159265359)), 0.8F, 0.97F));
			fr.drawString(m.getName(), sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 1.5F, offset + 2.5F, 0xffffffff, false);

			i++;
		}
		
	    GL11.glPopMatrix();
	}
}
