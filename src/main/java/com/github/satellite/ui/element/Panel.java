package com.github.satellite.ui.element;

import com.github.satellite.Satellite;
import com.github.satellite.utils.render.easing.EaseValue;
import com.github.satellite.utils.render.easing.Value;
import com.github.satellite.utils.render.AnimationUtil.Mode;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Panel {

	protected Minecraft mc = Satellite.mc;

	public ElementManager elementManager;

	public Value x;
	public Value y;
	public Value width;
	public Value height;
	public long onHover;
	public boolean isHover;
	public boolean lastHover;
	public boolean isCollidable;

	public boolean visible = true;

	public List<EaseValue> values;

	public Panel(ElementManager elementManager, Value x, Value y, Value w, Value h, boolean isCollidable) {
		this.values = new ArrayList<EaseValue>();
		this.elementManager = elementManager;
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.isCollidable = isCollidable;
		addValues(this.x, this.width, this.height, this.y);
	}

	public Panel(ElementManager elementManager, double x, double y, double w, double h, boolean isCollidable) {
		this(elementManager, new Value((float) x, null), new Value((float)y, null), new Value((float)w, null), new Value((float)h, null), isCollidable);
	}

	public void up() {
		CopyOnWriteArrayList<Panel> panels = elementManager.panels;
		panels.remove(this);
		panels.add(this);
	}

	public void down() {
		CopyOnWriteArrayList<Panel> panels = elementManager.panels;
		panels.remove(this);
		panels.add(panels.size(), this);
	}

	public void addValues(EaseValue... values) {
		this.values.addAll(Arrays.asList(values));
	}

	public void setEaseType(Mode mode) {
		this.width.easeMode = mode;
		this.height.easeMode = mode;
		this.x.easeMode = mode;
		this.y.easeMode = mode;
	}

	public void draw(int mouseX, int mouseY, float partialTicks) {}

	public void onHover(long time) {}
	public void deHover(long time) {}
	public void onClicked(boolean isSelf, long time) {}
	public void keyTyped(char typedChar, int keyCode) {}

	public ElementManager getElementManager() {
		return elementManager;
	}

	public void setElementManager(ElementManager elementManager) {
		this.elementManager = elementManager;
	}

	public Panel setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Value getX() {
		return x;
	}

	public void setX(Value x) {
		this.x = x;
	}

	public Value getY() {
		return y;
	}

	public void setY(Value y) {
		this.y = y;
	}

	public Value getWidth() {
		return width;
	}

	public void setWidth(Value width) {
		this.width = width;
	}

	public Value getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height.value = (float) height;
	}

	public long getOnHover() {
		return onHover;
	}

	public void setOnHover(long onHover) {
		this.onHover = onHover;
	}

	public boolean isCollidable() {
		return isCollidable;
	}

	public void setCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}

	public boolean isHover() {
		return isHover;
	}

	public void setHover(boolean isHover) {
		this.isHover = isHover;
	}

	public boolean isLastHover() {
		return lastHover;
	}

	public void setLastHover(boolean lastHover) {
		this.lastHover = lastHover;
	}
}
