package com.github.satellite.utils.render;

public class AnimationUtil {

	public static double easing(Mode mode, double t, double d, float...args) {
		return easing(mode, (float)t, (float)d);
	}
	
	public static float easing(Mode mode, float t, float d, float...args) {
		switch(mode) {
		case NONE:
			return d;
		case EASEIN:
			return (float) (d - d * Math.pow(args.length>0?args[0]:0.5f, t));
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
