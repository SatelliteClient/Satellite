package com.github.satellite.ui.element.elements;

import org.lwjgl.opengl.GL11;

import com.github.satellite.ui.element.ElementManager;
import com.github.satellite.ui.element.Panel;
import com.github.satellite.utils.render.RenderUtils;
import com.github.satellite.utils.render.easing.Color;
import com.github.satellite.utils.render.easing.Value;

public class OutlinePanel extends Panel {
	
	public Color color;

	public OutlinePanel(ElementManager elementManager, Value x, Value y, Value w, Value h, Color color, boolean isCollidable) {
		super(elementManager, x, y, w, h, isCollidable);
		this.color = color;
		addValues(color);
	}
	
	public OutlinePanel(ElementManager elementManager, double x, double y, double w, double h, java.awt.Color color, boolean isCollidable) {
		this(elementManager, new Value(x, null), new Value(y, null), new Value(w, null), new Value(h, null), new Color(color, null), isCollidable);
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		drawOutlineRect(x.value, y.value, x.value+width.value, y.value+height.value, color.getColor());
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
	
	public static void drawOutlineRect(float x, float y, float x2, float y2, java.awt.Color color) {
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		RenderUtils.glColor(color);
		GL11.glLineWidth(3);
		GL11.glBegin(1);
		GL11.glVertex2f(x2, y);
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x, y2);
		GL11.glVertex2f(x, y2);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x2, y);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}
}
