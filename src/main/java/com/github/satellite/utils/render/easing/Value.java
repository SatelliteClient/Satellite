package com.github.satellite.utils.render.easing;

import com.github.satellite.utils.TimeHelper;
import com.github.satellite.utils.render.AnimationUtil;
import com.github.satellite.utils.render.AnimationUtil.Mode;

import javax.annotation.Nullable;

public class Value extends EaseValue {

	public double value;
	public double lastValue;
	public double easeTo;
	public double duration;
	public TimeHelper timeHelper;
	public Mode easeMode;
	
	public Value(double value, @Nullable Mode easeMode) {
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
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public void updateEase() {
		double time = timeHelper.getCurrentMS() - timeHelper.getLastMS();
		this.value = lastValue + AnimationUtil.easing(easeMode, time / duration, easeTo - lastValue);
		if(Math.abs(value - easeTo) < 1) {
			this.value = easeTo;
		}
	}
	
	public void easeTo(double value, double duration, boolean reset) {
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
		
		private num(double value) {
			this.value = new Value(value, null);
		}
	}

	public double getLastValue() {
		return lastValue;
	}

	public void setLastValue(double lastValue) {
		this.lastValue = lastValue;
	}

	public double getEaseTo() {
		return easeTo;
	}

	public void setEaseTo(double easeTo) {
		this.easeTo = easeTo;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
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
