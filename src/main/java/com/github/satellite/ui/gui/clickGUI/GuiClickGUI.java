package com.github.satellite.ui.gui.clickGUI;

import com.github.satellite.Satellite;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.features.module.render.ClickGUI;
import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.ui.element.elements.RectPanel;
import com.github.satellite.ui.element.elements.TextPanel;
import com.github.satellite.ui.gui.clickGUI.element.Category;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.easing.Color;
import com.github.satellite.utils.render.easing.Value;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.AnimationUtil.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiClickGUI extends GuiScreen {

	public static CopyOnWriteArrayList<Category> panels;

	public static boolean isCollided;

	public static ElementManager gui = new ElementManager();
	public static ElementManager MAP_GUI = new ElementManager();
	public static ElementManager SETTING_GUI = new ElementManager();
	public static RectPanel SETTING_PANEL;
	public static RectPanel SETTING_PANEL_RESIZER;

	public static RectPanel currentScreen;

	public static RectPanel menu;

	public static List<Panel> menuElements = new ArrayList<Panel>();

	public static int lastScreen = 0;

	public Value scrolly;

	public GuiClickGUI(int screen) {
		//setup map
		MAP_GUI = new ElementManager();
		MAP_GUI.addPanel(new RectPanel(MAP_GUI, 75, 25, 500, 500, ColorUtils.alpha(ThemeManager.getTheme().dark(1), 0xf0), true));

		//Setting GUI
		SETTING_GUI = new ElementManager();
		double heightGround = 300;
		double widthGround = (int) (heightGround*1.6180339887);
		SETTING_PANEL = new RectPanel(SETTING_GUI, width/2-widthGround/2, height/2-heightGround/2, widthGround, heightGround, ThemeManager.getTheme().dark(0), true);
		float rPos= SETTING_PANEL.x.value + SETTING_PANEL.width.value;
		float dPos= SETTING_PANEL.y.value + SETTING_PANEL.height.value;
		SETTING_PANEL_RESIZER = (RectPanel) new RectPanel(SETTING_GUI, rPos-10, dPos-10, 10, 10, ThemeManager.getTheme().light(0), true).setVisible(false);
		SETTING_GUI.addPanel(SETTING_PANEL, SETTING_PANEL_RESIZER);

		//GL11.glScaled(1/size, 1/size, 1);
		GuiClickGUI.gui.panels = new CopyOnWriteArrayList<>();

		String[] els = new String[] {"ClickGUI", "Map", "Satellite Settings", "Player", "Team", "Waypoints"};
		int y=0;
		int h=50;

		GuiClickGUI.menu = new RectPanel(gui, 0, 0, 20, 0, ThemeManager.getTheme().dark(0), true);
		GuiClickGUI.currentScreen = new RectPanel(gui, 0, screen*h, 25, h, ThemeManager.getTheme().dark(3), false);

		gui.addPanel(menu);
		gui.addValue(this.scrolly = new Value(0, Mode.EASEOUT));
		menu.setEaseType(Mode.EASEOUT);

		GuiClickGUI.gui.addPanel(menu);

		for(@SuppressWarnings("unused") String str : els) {
			RectPanel p = new RectPanel(gui, 0, y, 20, h, ThemeManager.getTheme().dark(1), false);
			p.setEaseType(Mode.EASEOUT);
			gui.addPanel(p);
			menuElements.add(p);
			y+=h;
		}

		gui.addPanel(currentScreen);
		currentScreen.setEaseType(Mode.EASEOUT);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

		y=0;
		for(String str : els) {
			TextPanel p = new TextPanel(gui, 54, y+25, 0, 0, false, fr, str, new Color(255, 255, 255, 0, Mode.EASEOUT));
			p.setEaseType(Mode.EASEOUT);
			gui.addPanel(p);
			menuElements.add(p);
			y+=h;
		}
	}

	public static void loadModules() {
		panels = new CopyOnWriteArrayList<>();

		int x=0;
		for(Module.Category c : Module.Category.values())
		{
			List<Module> mods = ModuleManager.getModulesbyCategory(c);

			Collections.sort(mods, new Comparator<Module>() {
				public int compare(Module one, Module other) {
					return one.name.compareTo(other.name);
				}
			});

			if(mods.isEmpty())
				continue;
			x+=120;
			panels.add(new Category(c.name, x, 50, mods));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (((ClickGUI)ModuleManager.getModulebyClass(ClickGUI.class)).autoGuiScale.enable) {
			float size = (float)Display.getWidth()/2048;
			if (mc.gameSettings.guiScale == 3) {
				size /= 1.5;
			}
			mouseX /= size;
			mouseY /= size;
			height /= size;
			width /= size;
			GL11.glScalef(size, size, 1);
		}
		GL11.glTranslated(0, scrolly.value, 0);
		mouseY -= scrolly.value;
		scrolly.easeTo(Math.min(0, scrolly.easeTo+Mouse.getDWheel()*.2f), 50, true);
		Satellite.themeManager.setTheme(((ClickGUI) ModuleManager.getModulebyClass(ClickGUI.class)).theme.getMode());

		isCollided=false;

		menu.setColor(ThemeManager.getTheme().dark(0));
		menu.height.easeTo = height;

		//gui.updateCollision(mouseX, mouseY);

		menu.width.easeTo(menu.isHover()?150:50, 100, true);

		currentScreen.y.easeTo(mouseX < menu.width.value ? (int)(mouseY/50)*50 : currentScreen.y.value, 50, true);
		currentScreen.width = menu.width;
		for(Panel p : menuElements) {
			p.width = menu.width;
			if(p instanceof TextPanel) {
				TextPanel panel = (TextPanel) p;
				panel.alpha.easeTo(menu.isHover()?255:0, 50, true);
			}
		}
		int screenIndex = (int)((currentScreen.y.value+25)/50);
		isCollided = gui.isCollided;
		switch (screenIndex) {
			case 0:
				List<Category> drawPanel = (List<Category>) panels.clone();
				for(Category c : drawPanel) {
					c.update(mouseX, mouseY);
				}
				Collections.reverse(drawPanel);
				for(Category c : drawPanel) {
					c.draw(mouseX, mouseY, partialTicks);
				}
				Collections.reverse(drawPanel);
				Collections.reverse(drawPanel);
				break;
			case 1:
				RectPanel mapGround = (RectPanel) MAP_GUI.getPanels().get(0);
				mapGround.color.easeTo(255, 255, 255, 0, 50, true);
				int size = 4;
				for (int x = 0; x<((int)((width-100)/size)); x++) {
					for (int y = 0; y<(int)((height-50)/size); y++) {
						if (mc.world.getHeight((int)mc.player.posX+(x-(((int)((width-100)/size))/2))*4, (int)mc.player.posZ+(y-(((int)((height-50)/size))/2))*4)>mc.player.posY);
						else continue;
						java.awt.Color color = ThemeManager.getTheme().light(0);
						color = ColorUtils.alpha(color, 0x80);
						new RectPanel(MAP_GUI, 75+x*size, 25+y*size, size, size, color, false).draw(mouseX, mouseY, partialTicks);
					}
				}
				for (Entity ent : mc.world.loadedEntityList) {
					if (ent == mc.player) continue;
					int dx = (int) (ent.posX - mc.player.posX)+((int)((width-100)/size))/2
							,dz = (int) (ent.posZ - mc.player.posZ)+((int)((height-50)/size))/2;
					new RectPanel(MAP_GUI, 75+dx*size, 25+dz*size, size, size, ThemeManager.getTheme().dark(0), false).draw(mouseX, mouseY, partialTicks);
				}
				mapGround.width.easeTo(((int)((width-100)/size)*size), 50, true);
				mapGround.height.easeTo(((int)((height-50)/size)*size), 50, true);
				MAP_GUI.updateEasing();
				MAP_GUI.updateCollision(mouseX, mouseY);
				MAP_GUI.draw(mouseX, mouseY, partialTicks);
				break;
			case 2:
				float rPos= SETTING_PANEL.x.value + SETTING_PANEL.width.value;
				float dPos= SETTING_PANEL.y.value + SETTING_PANEL.height.value;
				SETTING_PANEL.x.easeTo(width/2 - SETTING_PANEL.width.value/2, 1, false);
				SETTING_PANEL.y.easeTo(height/2 - SETTING_PANEL.height.value/2, 1, false);
				if (Mouse.isButtonDown(0) && (SETTING_GUI.clickedPanel(SETTING_PANEL_RESIZER))) {
					SETTING_PANEL.width.easeTo(SETTING_PANEL.width.value+Mouse.getDX(), 1, false);
					SETTING_PANEL.height.easeTo(SETTING_PANEL.height.value-Mouse.getDY(), 1, false);
					if (SETTING_PANEL.width.easeTo < 200) {
						SETTING_PANEL.width.easeTo(200, 1, true);
					}
					if (SETTING_PANEL.height.easeTo < 200) {
						SETTING_PANEL.height.easeTo(200, 1, true);
					}
				}
				SETTING_GUI.updateCollision(mouseX, mouseY);
				SETTING_GUI.updateEasing();
				SETTING_GUI.draw(mouseX, mouseY, partialTicks);
				//SETTING_PANEL_RESIZER = new RectPanel(SETTING_GUI, rPos-10, dPos-10, 10, 10, ThemeManager.getTheme().light(0), true);
				rPos= SETTING_PANEL.x.value + SETTING_PANEL.width.value;
				dPos= SETTING_PANEL.y.value + SETTING_PANEL.height.value;
				SETTING_PANEL_RESIZER.x = new Value(rPos - 10, null);
				SETTING_PANEL_RESIZER.y = new Value(dPos - 10, null);
				SETTING_PANEL_RESIZER.draw(mouseX, mouseY, partialTicks);
				new RectPanel(SETTING_GUI, SETTING_PANEL.x.value, SETTING_PANEL.y.value, 75, SETTING_PANEL.height.value, ThemeManager.getTheme().dark(1), true).draw(mouseX, mouseY, partialTicks);
				new RectPanel(SETTING_GUI, SETTING_PANEL.x.value, SETTING_PANEL.y.value, 75, 40, ThemeManager.getTheme().dark(2), true).draw(mouseX, mouseY, partialTicks);
		}

		lastScreen = screenIndex;
		/*for(int i=0; i<panels.size(); i++) {
			panels.get(i).update(mouseX, mouseY);
		}

		for(int i=1; i<=panels.size(); i++) {
			panels.get(panels.size()-i).draw(mouseX, mouseY, partialTicks);
		}*/
		gui.updateEasing();
		//gui.draw(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for(Category p : panels) {
			p.onKeyDown(keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		SETTING_GUI.onMousePressed(mouseX, mouseY, mouseButton);
		for(Category p : panels) {
			p.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		SETTING_GUI.onMouseReleased(mouseX, mouseY);
		for(Category p : panels) {
			p.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	public void onGuiClosed() {
		ModuleManager.toggle(ClickGUI.class);
	}

}
