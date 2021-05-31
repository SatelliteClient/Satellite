package com.github.satellite.ui.element;

import com.github.satellite.utils.render.easing.EaseValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElementManager {

	public CopyOnWriteArrayList<Panel> panels = new CopyOnWriteArrayList<>();
	public Panel currendPanel;
	public boolean isCollided;
	
	public void updateCollision(int mouseX, int mouseY) {
		Collections.reverse(panels);
		this.isCollided = false;
		for(Panel p : panels) {
			
			p.setLastHover(p.isHover);
			
			if(mouseX>=p.x.value && mouseX<p.x.value+p.width.value && mouseY>=p.y.value && mouseY<p.y.value+p.height.value) {
				this.currendPanel = p;
				if(!p.lastHover) {
					p.onHover(System.nanoTime() / 1000000L);
				}
				p.setHover(true);
				if(p.isCollidable()) {
					this.isCollided = true;
					break;
				}
			}else {
				p.setHover(false);
				if(p.lastHover) {
					p.deHover(System.nanoTime() / 1000000L);
				}
			}
		}
		Collections.reverse(panels);
	}
	
	public void draw(int mouseX, int mouseY, float partialTicks) {
		for(Panel p : panels) {
			for(EaseValue value : p.values) {
				value.updateEase();
			}
			p.draw(mouseX, mouseY, partialTicks);
		}
	}

	public CopyOnWriteArrayList<Panel> getPanels() {
		return panels;
	}

	public void setPanels(CopyOnWriteArrayList<Panel> panels) {
		this.panels = panels;
	}

	public Panel getCurrendPanel() {
		return currendPanel;
	}

	public void setCurrendPanel(Panel currendPanel) {
		this.currendPanel = currendPanel;
	}
	
	public void addPanel(Panel...panels) {
		this.panels.addAll(Arrays.asList(panels));
	}
}
