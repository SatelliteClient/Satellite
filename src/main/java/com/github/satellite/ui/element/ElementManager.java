package com.github.satellite.ui.element;

import com.github.satellite.utils.TimeHelper;
import com.github.satellite.utils.render.easing.EaseValue;
import com.github.satellite.utils.render.easing.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElementManager {

	public CopyOnWriteArrayList<Panel> panels = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<EaseValue> values = new CopyOnWriteArrayList<>();
	public Panel currendPanel;
	public Panel clickedPanel;
	public boolean isCollided;
	public TimeHelper timer = new TimeHelper();

	public void updateTime() {
		long deltaTime = timer.getCurrentMS() - timer.getLastMS();
		timer.reset();
		for (EaseValue v : values) {
			v.timer.update(deltaTime);
		}
		for (Panel p : panels) {
			for (EaseValue v : p.values) {
				v.timer.update(deltaTime);
			}
		}
	}

	public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
		for(Panel p : panels) {

			p.setLastHover(p.isHover);

			if(mouseX>=p.x.value && mouseX<p.x.value+p.width.value && mouseY>=p.y.value && mouseY<p.y.value+p.height.value) {
				this.clickedPanel = p;
				p.onClicked(true, timer.getCurrentMS());
			}else {
				p.onClicked(false, timer.getCurrentMS());
			}
		}
	}

	public void onMouseReleased(int mouseX, int mouseY) {
		this.clickedPanel = null;
	}

	public void updateCollision(int mouseX, int mouseY) {
		Collections.reverse(panels);
		this.isCollided = false;
		for(Panel p : panels) {

			p.setLastHover(p.isHover);

			if(mouseX>=p.x.value && mouseX<p.x.value+p.width.value && mouseY>=p.y.value && mouseY<p.y.value+p.height.value) {
				this.currendPanel = p;
				if(!p.lastHover) {
					p.onHover(timer.getCurrentMS());
				}
				p.setHover(true);
				if(p.isCollidable()) {
					this.isCollided = true;
					break;
				}
			}else {
				p.setHover(false);
				if(p.lastHover) {
					p.deHover(timer.getCurrentMS());
				}
			}
		}
		Collections.reverse(panels);
	}

	public int RENDER_PHASE = 0;

	public void updateEasing() {
		updateTime();
		for(EaseValue value : values) {
			value.updateEase();
		}
		for(Panel p : panels) {
			for(EaseValue value : p.values) {
				value.updateEase();
			}
		}
	}

	public void draw(int mouseX, int mouseY, float partialTicks) {
		for(Panel p : panels) {
			if (p.visible)
				p.draw(mouseX, mouseY, partialTicks);
		}
	}

	public boolean clickedPanel(Panel p) {
		return clickedPanel == p;
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

	enum RENDER_PHASE {
		PRE,

	}

	public void addValue(EaseValue... values) {
		this.values.addAll(Arrays.asList(values));
	}

	public void keyTyped(char typedChar, int keyCode) {
		panels.forEach(p -> {
			p.keyTyped(typedChar, keyCode);
		});
	}
}
