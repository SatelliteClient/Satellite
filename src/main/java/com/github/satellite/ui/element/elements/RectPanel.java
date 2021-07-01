package com.github.satellite.ui.element.elements;

import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.utils.render.RenderUtils;
import com.github.satellite.utils.render.easing.Color;
import com.github.satellite.utils.render.easing.Value;

public class RectPanel extends Panel {

	public Color color;

	public RectPanel(ElementManager elementManager, Value x, Value y, Value w, Value h, Color color, boolean isCollidable) {
		super(elementManager, x, y, w, h, isCollidable);
		this.color = color;
		addValues(color);
	}

	public RectPanel(ElementManager elementManager, double x, double y, double w, double h, java.awt.Color color, boolean isCollidable) {
		this(elementManager, new Value(x, null), new Value(y, null), new Value(w, null), new Value(h, null), new Color(color, null), isCollidable);
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		RenderUtils.drawRect(x.value, y.value, x.value+width.value, y.value+height.value, color.getColor().getRGB());
		super.draw(mouseX, mouseY, partialTicks);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(java.awt.Color color) {
		this.color.setRed(color.getRed());
		this.color.setGreen(color.getGreen());
		this.color.setBlue(color.getBlue());
		this.color.setAlpha(color.getAlpha());
	}
}
