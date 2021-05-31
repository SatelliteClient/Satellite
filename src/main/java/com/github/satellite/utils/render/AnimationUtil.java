package com.github.satellite.utils.render;

public class AnimationUtil {

	public static double easing(Mode mode, double t, double d) {
		return easing(mode, (float)t, (float)d);
	}
	
	public static double easing(Mode mode, float t, float d) {
		switch(mode) {
		case NONE:
			return d;
		case EASEIN:
			return d - d * Math.pow(0.5, t);
		case EASEOUT:
			return d - d / t;
		case LINEAR:
			return d * t;
		}
		return d;
	}
	
	public enum Mode {
		NONE,
		EASEIN,
		EASEOUT,
		LINEAR;
	}
}
