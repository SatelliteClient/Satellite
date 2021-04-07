package satellite.ui;

import java.util.Collections;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import satellite.Client;
import satellite.module.Module;

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
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		FontRenderer fr = mc.fontRendererObj;
		
		Collections.sort(Client.modules, new ModuleComparator());
		
		/*Client.modules.sort(Comparator.comparingInt(m -> 
			fr.getStringWidth(m.))
			.reversed()
		);*/

		int i=0;
		for(Module m : Client.modules) {
			if(!m.isEnable())
				continue;
			
			int offset = (fr.FONT_HEIGHT + 4) * i;

			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth(), offset + fr.FONT_HEIGHT + 4, 0x90000000);
			Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 4, offset, sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset + fr.FONT_HEIGHT + 4, 0x90000000);
			fr.drawString(m.getName(), sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 1.5F, offset + 2.5F, 0xffffffff, false);

			i++;
		}
	}
	
}
