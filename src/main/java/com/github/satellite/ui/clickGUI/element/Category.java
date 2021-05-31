package com.github.satellite.ui.clickGUI.element;

import com.github.satellite.features.module.Module;
import com.github.satellite.ui.clickGUI.GuiClickGUI;
import com.github.satellite.ui.theme.Theme;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Category {
	
	protected FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
	
	public float x, y;
	public ArrayList<Panel> panels;
	public boolean extended;
	public String title;

	public boolean lastMouseClicked;
	public int lastMouseX, lastMouseY;

	public boolean isHover, isLastHover;

	public static class ModuleComparator implements Comparator<Module> {

		@Override
		public int compare(Module o1, Module o2) {
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getName()))
				return -1;
			if(Minecraft.getMinecraft().fontRenderer.getStringWidth(o1.getName()) < Minecraft.getMinecraft().fontRenderer.getStringWidth(o2.getName()))
				return 1;
			return 0;
		}

	}

	public Category(String title, float x, float y, List<Module> module) {
		this.x = x;
		this.y = y;
		this.title = title;

		this.panels = new ArrayList<Panel>();

		Collections.sort(module, new ModuleComparator());

		int elementY=0;
		for(Module m : module) {
			this.panels.add(new Panel(x, y+elementY, m));
			elementY += 20;
		}

	}

	public void update(int mouseX, int mouseY) {

		isHover =
				!GuiClickGUI.isCollided&&
				mouseX>x&&
				mouseX<x+100&&
				mouseY>=y-20&&
				mouseY<y;

		isLastHover =
				!GuiClickGUI.isCollided&&
				lastMouseX>x&&
				lastMouseX<x+100&&
				lastMouseY>=y-20&&
				lastMouseY<y;

		if(isHover||isLastHover) {
			GuiClickGUI.isCollided=true;
		}

		if(extended) {
			for(Panel p : panels) {
				p.update(mouseX, mouseY);
			}
		}
	}

	public void draw(int mouseX, int mouseY, float partialTicks) {
		Theme theme = ThemeManager.getTheme();

		if((isHover || isLastHover) && Mouse.isButtonDown(0)) {
			x+=mouseX-lastMouseX;
			y+=mouseY-lastMouseY;
			for(Panel p : panels) {
				p.x+=mouseX-lastMouseX;
				p.y+=mouseY-lastMouseY;
			}
			GuiClickGUI.panels.remove(this);
			GuiClickGUI.panels.add(0, this);
		}

		RenderUtils.drawRect(x-1, y-21, x+101, y+1, theme.dark(3).getRGB());
		RenderUtils.drawRect(x, y-20, x+100, y, ColorUtils.alpha(theme.light(2), isHover?0xff:0x40).getRGB());

		RenderUtils.glColor(255, 255, 255, 255);
		fontRendererObj.drawString(title, (int)x+fontRendererObj.getStringWidth(title)/2, (int)y-12, 0x40ffffff);

		lastMouseX = mouseX;
		lastMouseY = mouseY;

		if(extended) {
			for(Panel p : panels) {
				p.draw(mouseX, mouseY, partialTicks);
			}
		}
	}

	public void onKeyDown(int keyCode) {
		if(extended) {
			for(Panel p : panels) {
				p.onKeyDown(keyCode);
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(isHover && mouseButton == 1)
			extended=!extended;
		lastMouseClicked=true;

		if(extended) {
			for(Panel p : panels) {
				p.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		lastMouseClicked=false;

		if(extended) {
			for(Panel p : panels) {
				p.mouseReleased(mouseX, mouseY, state);
			}
		}
	}
	
}
