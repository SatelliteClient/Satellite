package satellite.event.listeners;

import satellite.event.Event;

public class EventPlayerInput extends Event<EventPlayerInput>{
	
	boolean forward, back, left, right, jump, sneak;

	public EventPlayerInput(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak) {
		super();
		this.forward = forward;
		this.back = back;
		this.left = left;
		this.right = right;
		this.jump = jump;
		this.sneak = sneak;
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isBack() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public boolean isSneak() {
		return sneak;
	}

	public void setSneak(boolean sneak) {
		this.sneak = sneak;
	}
}
