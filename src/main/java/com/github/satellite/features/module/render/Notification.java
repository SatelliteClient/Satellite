package com.github.satellite.features.module.render;

import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderGUI;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.ui.element.elements.*;
import com.github.satellite.ui.theme.ThemeManager;
import com.github.satellite.utils.render.AnimationUtil.Mode;
import com.github.satellite.utils.render.ColorUtils;
import com.github.satellite.utils.render.easing.Color;
import com.github.satellite.utils.render.easing.Value;

import net.minecraft.client.gui.ScaledResolution;

public class Notification extends Module {
	
	public Notification() {
		super("Notification", 0, Category.RENDER, true);
	}
	
	ElementManager gui = Satellite.hud.gui;
	
	CopyOnWriteArrayList<Panel> panels = new CopyOnWriteArrayList<>();
	
	public void addPanel(String str) {
		ScaledResolution sr = new ScaledResolution(mc);
		panels.add(new OutlinePanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x40), false));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0xff), false));
		panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 106, 30-2, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x10), false));
		panels.add(new TextPanel(gui, sr.getScaledWidth(), sr.getScaledHeight()-31+4, 106, 30-2, false, mc.fontRenderer, str, new Color(ColorUtils.alpha(ThemeManager.getTheme().light(0), 0x10), Mode.EASEIN)));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 4));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 3));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 2));
		Satellite.hud.gui.addPanel(panels.get(panels.size() - 1));
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRenderGUI) {
			ScaledResolution sr = new ScaledResolution(mc);
			for (Panel p : panels) {
				p.setEaseType(Mode.EASEIN);
				if (p instanceof TextPanel) {
					p.x.easeTo((float) (p.x.timer.hasReached(10) ? sr.getScaledWidth_double()-104 : sr.getScaledWidth_double()-4), 100, true);
					p.y.easeTo(sr.getScaledHeight()-31-30*(int)(panels.indexOf(p)/4)+4, 50, true);
				}else {
					p.x.easeTo((float) (p.x.timer.hasReached(10) ? sr.getScaledWidth_double()-4-104 : sr.getScaledWidth_double()-4), 100, true);
					p.y.easeTo(sr.getScaledHeight()-31-30*(int)(panels.indexOf(p)/4), 50, true);
				}
				if (p.x.timer.hasReached(5000)) {
					p.y.easeTo(sr.getScaledHeight()+4, 50, true);
					panels.remove(p);
				}
			}
		}
		super.onEvent(e);
	}
	
	@Override
	public void onEnable() {
		ScaledResolution sr = new ScaledResolution(mc);
		this.panels = new CopyOnWriteArrayList<Panel>();
		int y = 0;
		for (int i = 0; i<0; ++i) {
			panels.add(new OutlinePanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x40), false));
			panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 104, 30-4, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0xff), false));
			panels.add(new RectPanel(gui, sr.getScaledWidth()-4, sr.getScaledHeight()-31, 106, 30-2, ColorUtils.alpha(ThemeManager.getTheme().dark(0), 0x10), false));
			panels.add(new TextPanel(gui, sr.getScaledWidth(), sr.getScaledHeight()-31+4, 106, 30-2, false, mc.fontRenderer, "a", new Color(ColorUtils.alpha(ThemeManager.getTheme().light(0), 0x10), Mode.EASEIN)));
			y ++;
		}
		for (Panel p : panels) {
			Satellite.hud.gui.addPanel(p);
		}
		super.onEnable();
	}
}
