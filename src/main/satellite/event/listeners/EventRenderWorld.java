package satellite.event.listeners;

import satellite.event.Event;

public class EventRenderWorld extends Event<EventRenderWorld> {

	float partialTicks;
	
	public EventRenderWorld(float partialTicks) {
		this.partialTicks=partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}
}
