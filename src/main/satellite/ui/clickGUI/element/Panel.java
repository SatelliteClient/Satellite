package satellite.ui.clickGUI.element;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import satellite.futures.module.Module;
import satellite.setting.Setting;
import satellite.setting.KeyBindSetting;
import satellite.setting.ModeSetting;
import satellite.ui.clickGUI.ClickGUI;

public class Panel {
	
	protected FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
	
	public float x, y;
	public Module module;
	public boolean extended;
	
	public boolean isHover;
	public int selectedSetting = -1;
	public int hoveredSetting = -1;
	
	public int lastClick;
	public int lastClickedX;
	public int lastClickedY;
	
	public Panel(float x, float y, Module module) {
		super();
		this.x = x;
		this.y = y;
		this.module = module;
	}
	
	public void draw(int mouseX, int mouseY, float partialTicks) {
		isHover = 
				!ClickGUI.isCollided&&
				mouseX>x&&
				mouseX<x+100&&
				mouseY>y&&
				mouseY<y+fontRendererObj.FONT_HEIGHT+11;
		

		if(isHover) {
			ClickGUI.isCollided=true;
		}
		
		Gui.drawRect(x, y, x+100, y+fontRendererObj.FONT_HEIGHT+11, isHover?0x90141414:0x90000000);
		fontRendererObj.drawString(module.getName(), x+7, y+7, 0xffffffff);
		
		if(extended) {
			int i=0;
			int YY=0;
			for(Setting s : module.settings) {
				if(s == null)
					continue;
				
				boolean hover = 
						mouseX>x+100&&
						mouseX<x+200&&
						mouseY>YY+y&&
						mouseY<YY+y+fontRendererObj.FONT_HEIGHT+11;
						
				if(selectedSetting == -1 && hover && Mouse.isButtonDown(0))
					selectedSetting = i;
				
				hoveredSetting=-1;
				
				if(s instanceof KeyBindSetting) {
					if(hover) hoveredSetting = i;
					
					KeyBindSetting setting = (KeyBindSetting)s;
					Gui.drawRect(x+100, YY+y, x+200, YY+y+fontRendererObj.FONT_HEIGHT+11, selectedSetting==i||hover?0x90141414:0x90000000);
					fontRendererObj.drawString(setting.name+": "+(selectedSetting==i?"inputwaiting...":Keyboard.getKeyName(setting.getKeyCode())), 100+x+7, YY+y+7, 0xffffffff);
				}
				if(s instanceof ModeSetting) {
					if(hover) hoveredSetting = i;
					
					ModeSetting setting = (ModeSetting)s;
					Gui.drawRect(x+100, YY+y, x+200, YY+y+fontRendererObj.FONT_HEIGHT+11, hover?0x90141414:0x90000000);
					fontRendererObj.drawString(setting.name+": "+setting.getMode(), 100+x+7, YY+y+7, 0xffffffff);
				}
				i++;
				YY+=fontRendererObj.FONT_HEIGHT+11;
			}
		}
	}
	
	public void onKeyDown(int keyCode) {
		if(selectedSetting != -1) {
			Setting s = module.settings.get(selectedSetting);
			if(s instanceof KeyBindSetting) {
				((KeyBindSetting)s).setKeyCode(keyCode);
				selectedSetting=-1;
			}
		}
		
		if(hoveredSetting != -1) {
			if(module.settings.get(hoveredSetting) instanceof ModeSetting) {
				((ModeSetting)module.settings.get(hoveredSetting)).cycle();
			}
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(selectedSetting != -1) {
			Setting s = module.settings.get(selectedSetting);
			if(s instanceof KeyBindSetting) {
				if(mouseButton == 1) {
					((KeyBindSetting)s).setKeyCode(0);
					selectedSetting=-1;
				}
			}
		}
		
		lastClick=mouseButton;
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		
		if(hoveredSetting != -1) {
			if(module.settings.get(hoveredSetting) instanceof KeyBindSetting) {
				if(state == 0) selectedSetting = hoveredSetting;
			}
			
			if(module.settings.get(hoveredSetting) instanceof ModeSetting) {
				((ModeSetting)module.settings.get(hoveredSetting)).cycle();
			}
		}
		
		if(!isHover)
			return;
		if(state==0) module.toggle();
		if(state==1) extended=!extended;
		if(state==2) extended=!extended;
		
		lastClick=-1;
	}
}
