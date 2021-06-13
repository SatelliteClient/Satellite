package com.github.satellite.utils.render.easing;

import com.github.satellite.utils.TimeHelper;
import com.github.satellite.utils.render.AnimationUtil;
import com.github.satellite.utils.render.AnimationUtil.Mode;

import javax.annotation.Nullable;

public class Value extends EaseValue {

	public float value;
	public float lastValue;
	public float easeTo;
	public float duration;
	public TimeHelper timeHelper;
	public Mode easeMode;

	public Value(double value, @Nullable Mode easeMode) {
		this.value = (float) value;
		this.timeHelper = new TimeHelper();
		this.lastValue = (float) value;
		this.easeTo = (float) value;
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null) {
			this.easeMode = Mode.NONE;
		}
	}

	public Value(float value, @Nullable Mode easeMode) {
		this.value = value;
		this.timeHelper = new TimeHelper();
		this.lastValue = value;
		this.easeTo = value;
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null) {
			this.easeMode = Mode.NONE;
		}
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public void updateEase() {
		long time = timeHelper.getCurrentMS() - timeHelper.getLastMS();
		this.value = lastValue + AnimationUtil.easing(easeMode, time / duration, easeTo - lastValue);
		if(Math.abs(value - easeTo) < 1/duration) {
			this.value = easeTo;
		}
	}
	
	public void easeTo(float value, float duration, boolean reset) {
		if(this.easeTo != value) {
			timeHelper.reset();
			this.lastValue = this.value;
		}
		this.easeTo = value;
		this.duration = duration;
	}
	
	public enum num {
		ZERO(0),
		ONE(1),
		TEN(10);
		
		public Value value;
		
		private num(float value) {
			this.value = new Value(value, null);
		}
	}

	public float getLastValue() {
		return lastValue;
	}

	public void setLastValue(float lastValue) {
		this.lastValue = lastValue;
	}

	public float getEaseTo() {
		return easeTo;
	}

	public void setEaseTo(float easeTo) {
		this.easeTo = easeTo;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public TimeHelper getTimeHelper() {
		return timeHelper;
	}

	public void setTimeHelper(TimeHelper timeHelper) {
		this.timeHelper = timeHelper;
	}

	public Mode getEaseMode() {
		return easeMode;
	}

	public void setEaseMode(Mode easeMode) {
		this.easeMode = easeMode;
	}
}
