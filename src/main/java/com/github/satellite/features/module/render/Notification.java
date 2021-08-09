package com.github.satellite.features.module.render;

import java.util.concurrent.CopyOnWriteArrayList;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.features.module.Module;
import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.ui.element.elements.*;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.AnimationUtil.Mode;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.easing.Color;

import net.minecraft.client.gui.ScaledResolution;

public class Notification extends Module {

	public Notification() {
		super("Notification", 0, Category.RENDER);
	}

	ElementManager gui = Satellite.hud.gui;

	CopyOnWriteArrayList<Panel> panels = new CopyOnWriteArrayList<>();

	public void addPanel(String str1, String str2) {
		ScaledResolution sr = new ScaledResolution(mc);
		panels.add(new OutlinePanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x40), false));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x0f), false));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 106, 30-2, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x10), false));
		panels.add(new TextPanel(gui, sr.getScaledWidth(), sr.getScaledHeight()-31+4, 106, 30-2, false, mc.fontRenderer, str1, new Color(ColorUtils.alpha(ThemeManager.getTheme().light(0), 0xff), Mode.EASEOUT)));
		panels.add(new TextPanel(gui, sr.getScaledWidth(), sr.getScaledHeight()-31+4, 106, 30-2, false, mc.fontRenderer, str1, new Color(ColorUtils.alpha(ThemeManager.getTheme().light(0), 0xff), Mode.EASEOUT)));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-8, 104, 2.0F, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0xff), false));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-8, 0, 2.0F, ColorUtils.alpha(ThemeManager.getTheme().dark(1), 0xff), false));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 7));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 6));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 5));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 4));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 3));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 2));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 1));
	}

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI) {
			int i = 0;
			ScaledResolution sr = new ScaledResolution(mc);
			for (Panel p : panels) {
				p.setEaseType(Mode.EASEOUT);
				if (p instanceof TextPanel) {
					p.x.easeTo((float) (p.x.timer.hasReached(10) ? sr.getScaledWidth_double()-104 : sr.getScaledWidth_double()-4), 100, true);
					p.y.easeTo(sr.getScaledHeight()-31-30*(int)(panels.indexOf(p)/7)+4, 50, true);
				}else {
					if (p instanceof RectPanel) {
						RectPanel panel = (RectPanel)p;
						panel.color.easeTo(panel.color.red, panel.color.green, panel.color.blue, 0xff, 50, true);
					}
					p.x.easeTo((float) (p.x.timer.hasReached(10) ? sr.getScaledWidth_double()-4-104 : sr.getScaledWidth_double()-4), 100, true);
					p.y.easeTo(sr.getScaledHeight()-31-30*(int)(panels.indexOf(p)/7), 50, true);
					if (p.height.value == 2.0F) {
						p.y.easeTo(sr.getScaledHeight()-7-30*(int)(panels.indexOf(p)/7), 50, true);
						if (i%2==1) {
							//p.width.easeMode = Mode.LINEAR;
							//p.width.easeTo(104/10, 500, true);
						}
						i++;
					}
				}
				if (p.x.timer.hasReached(5000)) {
					p.y.easeTo(sr.getScaledHeight()+4, 50, true);
					//p.x.easeTo(sr.getScaledWidth()+4, 50, true);
					panels.remove(p);
				}
			}
		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
		ScaledResolution sr = new ScaledResolution(mc);
		Satellite.hud.gui.panels = new CopyOnWriteArrayList<>();
		this.panels = new CopyOnWriteArrayList<Panel>();
		super.onEnable();
	}
}
