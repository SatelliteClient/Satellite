package com.github.satellite.utils.render;

import java.awt.*;

public class ColorUtils {

	public static int HSBtoRGB(float h, float s, float b) {
		return Color.HSBtoRGB(h, s, b);
	}
	
	public static Color alpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
}
