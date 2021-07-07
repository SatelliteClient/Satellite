package com.github.satellite.ui.gui.clickGUI.element;

import com.github.satellite.event.listeners.EventSettingClicked;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.KeyBindSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.setting.Setting;
import com.github.satellite.ui.gui.clickGUI.GuiClickGUI;
import com.github.satellite.ui.theme.Theme;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Panel {

	protected FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

	public float x, y;
	public Module module;
	public boolean extended;

	public boolean isHover;
	public int currentSetting = -1;
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

	public void update(int mouseX, int mouseY) {
		isHover =
				!GuiClickGUI.isCollided&&
						mouseX>x&&
						mouseX<x+100&&
						mouseY>=y&&
						mouseY<y+fontRendererObj.FONT_HEIGHT+11;


		if(isHover) {
			GuiClickGUI.isCollided=true;
		}

		if(extended) {
			int i=0;
			int YY=0;

			hoveredSetting = -1;
			currentSetting = -1;

			for(Setting s : module.settings) {
				if(s == null)
					continue;

				boolean hover =
						!GuiClickGUI.isCollided&&
								mouseX>x+100&&
								mouseX<x+200&&
								mouseY>=YY+y&&
								mouseY<YY+y+fontRendererObj.FONT_HEIGHT+11;

				if(hover) {
					GuiClickGUI.isCollided=true;
					currentSetting = i;
				}

				i++;
				YY+=fontRendererObj.FONT_HEIGHT+11;
			}
		}
	}

	public void draw(int mouseX, int mouseY, float partialTicks) {
		Theme theme = ThemeManager.getTheme();

		RenderUtils.drawRect(x-1, y, x+100+(extended?0:1), y+fontRendererObj.FONT_HEIGHT+12, theme.dark(3).getRGB());
		RenderUtils.drawRect(x, y, x+100, y+fontRendererObj.FONT_HEIGHT+11, module.isEnable()?theme.dark(3).getRGB():theme.dark(0).getRGB());
		RenderUtils.drawRect(x, y, x+100, y+fontRendererObj.FONT_HEIGHT+11, isHover? ColorUtils.alpha(theme.dark(1), 0xff).getRGB():0);

		RenderUtils.glColor(255, 255, 255, 255);
		fontRendererObj.drawString(module.getName(), (int)x+7, (int)y+7, 0x40ffffff);

		if(extended) {
			int i=0;
			int YY=0;

			hoveredSetting=-1;

			for(Setting s : module.settings) {
				if(s == null)
					continue;

				boolean hover = currentSetting == i;

				if(selectedSetting == -1 && hover && Mouse.isButtonDown(0))
					selectedSetting = i;

				if(i==0) {
					RenderUtils.drawRect(x+100, YY+y-1, x+201, YY+y+fontRendererObj.FONT_HEIGHT+11, theme.dark(3).getRGB());
				}
				RenderUtils.drawRect(x+100, YY+y, x+201, YY+y+fontRendererObj.FONT_HEIGHT+11+(module.settings.size()==module.settings.indexOf(s)?0:1), theme.dark(3).getRGB());
				RenderUtils.drawRect(x+101, YY+y, x+200, YY+y+fontRendererObj.FONT_HEIGHT+11, hover?theme.dark(1).getRGB():theme.dark(0).getRGB());

				if(s instanceof KeyBindSetting) {
					if(hover) hoveredSetting = i;

					KeyBindSetting setting = (KeyBindSetting)s;
					RenderUtils.glColor(255, 255, 255, 255);
					fontRendererObj.drawString(setting.name+": "+(selectedSetting==i?"inputwaiting...":Keyboard.getKeyName(setting.getKeyCode())), (int)(100+x+7), (int)(YY+y+7), 0x40ffffff);
				}
				if(s instanceof ModeSetting) {
					if(hover) hoveredSetting = i;

					ModeSetting setting = (ModeSetting)s;
					RenderUtils.glColor(255, 255, 255, 255);
					fontRendererObj.drawString(setting.name+": "+setting.getMode(), (int)(100+x+7), (int)(YY+y+7), 0x40ffffff);
				}
				if(s instanceof BooleanSetting) {
					if(hover) hoveredSetting = i;

					BooleanSetting setting = (BooleanSetting)s;
					int stwidth = fontRendererObj.getStringWidth(setting.name);
					RenderUtils.drawRect(x+100+stwidth+9.5F, YY+y+3, x+101+stwidth+22, YY+y+fontRendererObj.FONT_HEIGHT+11-3, theme.light(0).getRGB());
					RenderUtils.drawRect(x+100+stwidth+10.5F, YY+y+4, x+100+stwidth+22, YY+y+fontRendererObj.FONT_HEIGHT+11-4, setting.isEnable()?theme.light(1).getRGB():0xff000f36);
					RenderUtils.glColor(255, 255, 255, 255);
					fontRendererObj.drawString(setting.name, (int)(100+x+7), (int)(YY+y+7), 0x40ffffff);
				}
				if(s instanceof NumberSetting) {
					if(hover) hoveredSetting = i;

					NumberSetting setting = (NumberSetting)s;
					int stwidth = fontRendererObj.getStringWidth(setting.name);
					RenderUtils.glColor(255, 255, 255, 255);
					double inc = setting.value/setting.maximum * 92 + 8;
					if (Mouse.isButtonDown(0) && (hover || hoveredSetting == i)) {
						double mouX = (mouseX-x-100-4)*1.1;
						double d = setting.maximum - setting.minimum;
						setting.setValue(mouX/100*d);
					}
					inc = setting.value/setting.maximum * 92 + 8;
					RenderUtils.drawRect(x+105, YY+y+fontRendererObj.FONT_HEIGHT+5, x+200-4, YY+y+fontRendererObj.FONT_HEIGHT+7, theme.light(2).getRGB());
					RenderUtils.drawRect(x+105, YY+y+fontRendererObj.FONT_HEIGHT+5, x+100+inc-4, YY+y+fontRendererObj.FONT_HEIGHT+7, theme.light(0).getRGB());
					RenderUtils.glColor(255, 255, 255, 255);
					fontRendererObj.drawString(setting.name+" : "+String.valueOf(setting.getValue()), (int)(100+x+7), (int)(YY+y+3), 0x40ffffff);
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
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(selectedSetting != -1) {
			Setting s = module.settings.get(selectedSetting);
			if(s instanceof KeyBindSetting) {
				if(mouseButton == 1) {
					module.onEvent(new EventSettingClicked(s));
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
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}

			if(module.settings.get(hoveredSetting) instanceof ModeSetting) {
				((ModeSetting)module.settings.get(hoveredSetting)).cycle();
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}

			if(module.settings.get(hoveredSetting) instanceof BooleanSetting) {
				((BooleanSetting)module.settings.get(hoveredSetting)).toggle();
				module.onEvent(new EventSettingClicked(module.settings.get(hoveredSetting)));
			}
		}

		if(!isHover)
			return;
		if(state==0) module.toggle();
		if(state==1) extended=!extended;

		lastClick=-1;
	}
}
