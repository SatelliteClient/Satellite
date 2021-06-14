package com.github.satellite.utils.render.easing;

import com.github.satellite.utils.render.AnimationUtil;

public abstract class EaseValue {

	public EaseValue() {
		this.timer = new Time();
	}

	public float duration;
	public AnimationUtil.Mode easeMode;
	public Time timer;

	public abstract void updateEase();

}
