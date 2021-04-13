package satellite.ui.clickGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import satellite.futures.module.Module;
import satellite.futures.module.ModuleManager;
import satellite.futures.module.movement.PacketFly;
import satellite.ui.clickGUI.element.Category;

public class ClickGUI extends GuiScreen {

	public static CopyOnWriteArrayList<Category> panels;
	
	public static boolean isCollided;
	
	@Override
	public void initGui() {
		panels = new CopyOnWriteArrayList<Category>();
		
		int x=0;
		for(Module.Category c : Module.Category.values())
		{
			if(ModuleManager.getModulesbyCategory(c).size()==0)
				continue;
			x+=120;
			panels.add(new Category(c.name, x, 50, ModuleManager.getModulesbyCategory(c)));
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		isCollided=false;
		
		for(int i=0; i<panels.size(); i++) {
			panels.get(i).update(mouseX, mouseY);
		}
		
		for(int i=1; i<=panels.size(); i++) {
			panels.get(panels.size()-i).draw(mouseX, mouseY, partialTicks);
		}
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
		ModuleManager.toggle(satellite.futures.module.render.ClickGUI.class);
	}
	
}
