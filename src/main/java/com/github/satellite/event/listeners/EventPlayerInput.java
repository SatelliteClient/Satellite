package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;

public class EventPlayerInput extends Event<EventPlayerInput> {
	
	public boolean forward, back, left, right, jump, sneak;
	private boolean lastForward, lastBack, lastLeft, lastRight, lastJump, lastSneak;

	public EventPlayerInput(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak) {
		super();
		this.forward = forward;
		this.back = back;
		this.left = left;
		this.right = right;
		this.jump = jump;
		this.sneak = sneak;

		this.lastForward = forward;
		this.lastBack = back;
		this.lastLeft = left;
		this.lastRight = right;
		this.lastJump = jump;
		this.lastSneak = sneak;
	}

	public boolean isModded() {
		return lastForward != forward || lastBack != back || lastLeft != left || lastRight != right || lastJump != jump || lastSneak != sneak;
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
