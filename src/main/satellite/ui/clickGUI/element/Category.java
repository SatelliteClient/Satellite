package satellite.ui.clickGUI.element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import satellite.futures.module.Module;
import satellite.ui.clickGUI.ClickGUI;

public class Category {
	
	protected FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
	
	public float x, y;
	public ArrayList<Panel> panels;
	public boolean extended;
	public String title;
	
	public boolean lastMouseClicked;
	public int lastMouseX, lastMouseY;
	
	public boolean isHover, isLastHover;
	
	public Category(String title, float x, float y, List<Module> module) {
		this.x = x;
		this.y = y;
		this.title = title;

		this.panels = new ArrayList<Panel>();
		
		int elementY=0;
		for(Module m : module) {
			this.panels.add(new Panel(x, y+elementY, m));
			elementY += 20;
		}
		
	}
	
	public void update(int mouseX, int mouseY) {
		isHover = 
				!ClickGUI.isCollided&&
				mouseX>x&&
				mouseX<x+100&&
				mouseY>y-20&&
				mouseY<y;
		
		isLastHover = 
				!ClickGUI.isCollided&&
				lastMouseX>x&&
				lastMouseX<x+100&&
				lastMouseY>y-20&&
				lastMouseY<y;
				
		if(isHover||isLastHover) {
			ClickGUI.isCollided=true;
		}
	}
	
	public void draw(int mouseX, int mouseY, float partialTicks) {
		if((isHover || isLastHover) && Mouse.isButtonDown(0)) {
			x+=mouseX-lastMouseX;
			y+=mouseY-lastMouseY;
			for(Panel p : panels) {
				p.x+=mouseX-lastMouseX;
				p.y+=mouseY-lastMouseY;
			}
			ClickGUI.panels.remove(this);
			ClickGUI.panels.add(0, this);
		}
		

		Gui.drawRect(x, y-20, x+100, y, 0xff141414);
		fontRendererObj.drawString(title, x+fontRendererObj.getStringWidth(title)/2, y-12, 0xffffffff);
		if(extended) {
			for(Panel p : panels) {
				p.draw(mouseX, mouseY, partialTicks);
			}
		}
		
		lastMouseX = mouseX;
		lastMouseY = mouseY;
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
