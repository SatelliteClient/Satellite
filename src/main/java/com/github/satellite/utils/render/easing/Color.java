package com.github.satellite.utils.render.easing;

import com.github.satellite.utils.TimeHelper;
import com.github.satellite.utils.render.AnimationUtil;
import com.github.satellite.utils.render.AnimationUtil.Mode;

import javax.annotation.Nullable;

public class Color extends EaseValue {

	public double red;
	public double green;
	public double blue;
	public double alpha;
	
	public double lastRed;
	public double lastGreen;
	public double lastBlue;
	public double lastAlpha;
	
	public double easeToRed;
	public double easeToGreen;
	public double easeToBlue;
	public double easeToAlpha;
	
	public double duration;
	public TimeHelper timeHelper;
	public Mode easeMode;
	
	public Color(double red, double green, double blue, double alpha, @Nullable Mode easeMode) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.timeHelper = new TimeHelper();
		
		this.lastRed = red;
		this.lastGreen = green;
		this.lastBlue = blue;
		this.lastAlpha = alpha;
		
		this.easeToRed = red;
		this.easeToGreen = green;
		this.easeToBlue = blue;
		this.easeToAlpha = alpha;
		
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null) {
			this.easeMode = Mode.NONE;
		}
	}
	
	public Color(java.awt.Color color, @Nullable Mode easeMode) {
		this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), easeMode);
	}

	@Override
	public void updateEase() {
		double time = timeHelper.getCurrentMS() - timeHelper.getLastMS();
		this.red = lastRed + AnimationUtil.easing(easeMode, time / duration, easeToRed - lastRed);
		this.green = lastGreen + AnimationUtil.easing(easeMode, time / duration, easeToGreen - lastGreen);
		this.blue = lastBlue + AnimationUtil.easing(easeMode, time / duration, easeToBlue - lastBlue);
		this.alpha = lastAlpha + AnimationUtil.easing(easeMode, time / duration, easeToAlpha - lastAlpha);
	}
	
	public void easeTo(double red, double green, double blue, double alpha, double duration, boolean reset) {
		if(!(this.easeToRed == red && this.easeToGreen == green && this.easeToBlue == blue && this.easeToAlpha == alpha)) {
			timeHelper.reset();
			this.lastRed = this.red;
			this.lastGreen = this.green;
			this.lastBlue = this.blue;
			this.lastAlpha = this.alpha;
		}
		this.easeToRed = red;
		this.easeToGreen = green;
		this.easeToBlue = blue;
		this.easeToAlpha = alpha;
		this.duration = duration;
	}
	
	public java.awt.Color getColor() {
		return new java.awt.Color((int)red, (int)green, (int)blue, (int)alpha);
	}

	public double getRed() {
		return red;
	}

	public void setRed(double red) {
		this.red = red;
	}

	public double getGreen() {
		return green;
	}

	public void setGreen(double green) {
		this.green = green;
	}

	public double getBlue() {
		return blue;
	}

	public void setBlue(double blue) {
		this.blue = blue;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getLastRed() {
		return lastRed;
	}

	public void setLastRed(double lastRed) {
		this.lastRed = lastRed;
	}

	public double getLastGreen() {
		return lastGreen;
	}

	public void setLastGreen(double lastGreen) {
		this.lastGreen = lastGreen;
	}

	public double getLastBlue() {
		return lastBlue;
	}

	public void setLastBlue(double lastBlue) {
		this.lastBlue = lastBlue;
	}

	public double getLastAlpha() {
		return lastAlpha;
	}

	public void setLastAlpha(double lastAlpha) {
		this.lastAlpha = lastAlpha;
	}

	public double getEaseToRed() {
		return easeToRed;
	}

	public void setEaseToRed(double easeToRed) {
		this.easeToRed = easeToRed;
	}

	public double getEaseToGreen() {
		return easeToGreen;
	}

	public void setEaseToGreen(double easeToGreen) {
		this.easeToGreen = easeToGreen;
	}

	public double getEaseToBlue() {
		return easeToBlue;
	}

	public void setEaseToBlue(double easeToBlue) {
		this.easeToBlue = easeToBlue;
	}

	public double getEaseToAlpha() {
		return easeToAlpha;
	}

	public void setEaseToAlpha(double easeToAlpha) {
		this.easeToAlpha = easeToAlpha;
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
