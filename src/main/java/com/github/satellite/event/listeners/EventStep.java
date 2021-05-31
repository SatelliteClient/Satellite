package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;

public class EventStep extends Event<EventStep> {

	double height;
	boolean canStep;
	
	public EventStep(double height, boolean canStep) {
		this.height = height;
		this.canStep = canStep;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public boolean isCanStep() {
		return canStep;
	}
	
	public void setCanStep(boolean canStep) {
		this.canStep = canStep;
	}
}
