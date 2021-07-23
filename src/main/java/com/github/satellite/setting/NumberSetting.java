package com.github.satellite.setting;

import java.util.function.Supplier;

public class NumberSetting extends Setting {

	public double value, minimum, maximum, increment;

	public NumberSetting(String name, Supplier<Boolean> visibility, double value, double minimum, double maximum, double increment) {
		super(name, visibility, value);
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
	}

	public NumberSetting(String name, double value, double minimum, double maximum, double increment) {
		super(name, null, value);
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		double preci = 1 / increment;
		this.value = Math.round(Math.max(minimum, Math.min(maximum, value)) * preci) / preci;
	}
	
	public void increment(boolean positive) {
		setValue(getValue() + (positive ? 1 : -1) * increment);
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	public double getIncrement() {
		return increment;
	}

	public void setIncrement(double increment) {
		this.increment = increment;
	}
}
