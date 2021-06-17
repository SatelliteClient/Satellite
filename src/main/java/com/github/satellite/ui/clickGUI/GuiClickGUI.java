package com.github.satellite.ui.clickGUI;

import com.github.satellite.Satellite;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.features.module.render.ClickGUI;
import com.github.satellite.ui.clickGUI.element.Category;
import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.ui.element.elements.RectPanel;
import com.github.satellite.ui.element.elements.TextPanel;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.easing.Color;
import com.github.satellite.utils.render.AnimationUtil.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiClickGUI extends GuiScreen {

	public static CopyOnWriteArrayList<Category> panels;
	
	public static boolean isCollided;

	public static ElementManager gui = new ElementManager();
	
	public static RectPanel currentScreen;
	
	public static RectPanel menu;
	
	public static List<Panel> menuElements = new ArrayList<Panel>();
	
	public GuiClickGUI(int screen) {
		GuiClickGUI.gui.panels = new CopyOnWriteArrayList<>();
		
		String[] els = new String[] {"ClickGUI", "Map", "Satellite Settings", "Player", "Team", "Waypoints"};
		int y=0;
		int h=50;

		GuiClickGUI.menu = new RectPanel(gui, 0, 0, 20, 0, ThemeManager.getTheme().dark(0), true);
		GuiClickGUI.currentScreen = new RectPanel(gui, 0, screen*h, 25, h, ThemeManager.getTheme().dark(3), false);
		
		gui.addPanel(menu);
		menu.setEaseType(Mode.EASEIN);

		GuiClickGUI.gui.addPanel(menu);

		for(@SuppressWarnings("unused") String str : els) {
			RectPanel p = new RectPanel(gui, 0, y, 20, h, ThemeManager.getTheme().dark(1), false);
			p.setEaseType(Mode.EASEIN);
			gui.addPanel(p);
			menuElements.add(p);
			y+=h;
		}
		
		gui.addPanel(currentScreen);
		currentScreen.setEaseType(Mode.EASEIN);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

		y=0;
		for(String str : els) {
			TextPanel p = new TextPanel(gui, 54, y+25, 0, 0, false, fr, str, new Color(255, 255, 255, 0, Mode.EASEIN));
			p.setEaseType(Mode.EASEIN);
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
			if(ModuleManager.getModulesbyCategory(c).isEmpty())
				continue;
			x+=120;
			panels.add(new Category(c.name, x, 50, ModuleManager.getModulesbyCategory(c)));
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Satellite.themeManager.setTheme(((ClickGUI) ModuleManager.getModulebyClass(ClickGUI.class)).theme.getMode());

		isCollided=false;

		menu.setColor(ThemeManager.getTheme().dark(0));
		menu.height.easeTo = height;

		gui.updateCollision(mouseX, mouseY);

		menu.width.easeTo(menu.isHover()?150:50, 50, true);

		currentScreen.y.easeTo(mouseX < menu.width.value ? (int)(mouseY/50)*50 : currentScreen.y.value, 50, true);
		currentScreen.width = menu.width;
		for(Panel p : menuElements) {
			p.width = menu.width;
			if(p instanceof TextPanel) {
				TextPanel panel = (TextPanel) p;
				panel.alpha.easeTo(menu.isHover()?255:0, 50, true);
			}
		}
		
		
		
		List<Category> drawPanel = (List<Category>) panels.clone();

		isCollided = gui.isCollided;
		
		for(Category c : drawPanel) {
			c.update(mouseX, mouseY);
		}

		Collections.reverse(drawPanel);
		
		for(Category c : drawPanel) {
			c.draw(mouseX, mouseY, partialTicks);
		}
		Collections.reverse(drawPanel);
		
		Collections.reverse(drawPanel);
		
		/*for(int i=0; i<panels.size(); i++) {
			panels.get(i).update(mouseX, mouseY);
		}
		
		for(int i=1; i<=panels.size(); i++) {
			panels.get(panels.size()-i).draw(mouseX, mouseY, partialTicks);
		}*/
		gui.draw(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for(Category p : panels) {
			p.onKeyDown(keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		for(Category p : panels) {
			p.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		for(Category p : panels) {
			p.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	@Override
	public void onGuiClosed() {
		ModuleManager.toggle(ClickGUI.class);
	}
	
}
