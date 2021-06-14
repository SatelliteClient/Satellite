package com.github.satellite.utils.render.easing;

import com.github.satellite.utils.TimeHelper;
import com.github.satellite.utils.render.AnimationUtil;
import com.github.satellite.utils.render.AnimationUtil.Mode;

import javax.annotation.Nullable;

public class Color extends EaseValue {

	public float red;
	public float green;
	public float blue;
	public float alpha;
	
	public float lastRed;
	public float lastGreen;
	public float lastBlue;
	public float lastAlpha;
	
	public float easeToRed;
	public float easeToGreen;
	public float easeToBlue;
	public float easeToAlpha;
	
	public Color(float red, float green, float blue, float alpha, @Nullable Mode easeMode) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		
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
		float time = this.timer.getCurrentMS() - this.timer.getLastMS();
		this.red = lastRed + AnimationUtil.easing(easeMode, time / duration, easeToRed - lastRed);
		this.green = lastGreen + AnimationUtil.easing(easeMode, time / duration, easeToGreen - lastGreen);
		this.blue = lastBlue + AnimationUtil.easing(easeMode, time / duration, easeToBlue - lastBlue);
		this.alpha = lastAlpha + AnimationUtil.easing(easeMode, time / duration, easeToAlpha - lastAlpha);
	}
	
	public void easeTo(float red, float green, float blue, float alpha, float duration, boolean reset) {
		if(!(this.easeToRed == red && this.easeToGreen == green && this.easeToBlue == blue && this.easeToAlpha == alpha)) {
			timer.reset();
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

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getLastRed() {
		return lastRed;
	}

	public void setLastRed(float lastRed) {
		this.lastRed = lastRed;
	}

	public float getLastGreen() {
		return lastGreen;
	}

	public void setLastGreen(float lastGreen) {
		this.lastGreen = lastGreen;
	}

	public float getLastBlue() {
		return lastBlue;
	}

	public void setLastBlue(float lastBlue) {
		this.lastBlue = lastBlue;
	}

	public float getLastAlpha() {
		return lastAlpha;
	}

	public void setLastAlpha(float lastAlpha) {
		this.lastAlpha = lastAlpha;
	}

	public float getEaseToRed() {
		return easeToRed;
	}

	public void setEaseToRed(float easeToRed) {
		this.easeToRed = easeToRed;
	}

	public float getEaseToGreen() {
		return easeToGreen;
	}

	public void setEaseToGreen(float easeToGreen) {
		this.easeToGreen = easeToGreen;
	}

	public float getEaseToBlue() {
		return easeToBlue;
	}

	public void setEaseToBlue(float easeToBlue) {
		this.easeToBlue = easeToBlue;
	}

	public float getEaseToAlpha() {
		return easeToAlpha;
	}

	public void setEaseToAlpha(float easeToAlpha) {
		this.easeToAlpha = easeToAlpha;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public Time getTime() {
		return timer;
	}

	public void setTime(Time time) {
		this.timer = time;
	}

	public Mode getEaseMode() {
		return easeMode;
	}

	public void setEaseMode(Mode easeMode) {
		this.easeMode = easeMode;
	}
}
