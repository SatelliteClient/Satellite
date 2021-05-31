package com.github.satellite.ui.element.elements;

import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.utils.render.easing.Value;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class FollowObj extends Panel {

	public CopyOnWriteArrayList<Panel> panels;
	public Panel followObj;

	public Value shiftX;
	public Value shiftY;
	
	public FollowObj(ElementManager elementManager, Value x, Value y, Value w, Value h, boolean isCollidable, Panel followObj, Value shiftX, Value shiftY, Panel... panels) {
		super(elementManager, x, y, w, h, isCollidable);
		this.followObj = followObj;
		this.panels = new CopyOnWriteArrayList<Panel>();
		this.panels.addAll(Arrays.asList(panels));
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		addValues(this.shiftX, this.shiftY);
	}
	
	public FollowObj(ElementManager elementManager, double x, double y, double w, double h, boolean isCollidable, Panel followObj, double shiftX, double shiftY, Panel... panels) {
		this(elementManager, new Value(x, null), new Value(y, null), new Value(w, null), new Value(h, null), isCollidable, followObj, new Value(shiftX, null), new Value(shiftY, null), panels);
	}
	
	public void addToGui() {
		elementManager.addPanel(followObj);
		elementManager.panels.addAll(panels);
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		for(Panel p : panels) {
			p.x.value = followObj.x.value + shiftX.value;
			p.y.value = followObj.y.value + shiftY.value;
		}
		super.draw(mouseX, mouseY, partialTicks);
	}
	
	enum Facing {
		LEFT,
		TOP,
		RIGHT,
		BOTTOM,
		CENTER;
	}
}
