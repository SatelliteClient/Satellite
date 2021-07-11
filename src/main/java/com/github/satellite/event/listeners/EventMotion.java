package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class EventMotion extends Event<EventMotion> {

	public double x, y, z;
	public float yaw, pitch;
	public boolean onGround;

	private double lastX, lastY, lastZ;
	public float lastYaw, lastPitch;
	public boolean lastOnGround;

	public boolean isModded() {
		return lastX != x || lastY != y || lastZ != z || lastYaw != yaw || lastPitch != pitch || lastOnGround != onGround;
	}

	public EventMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;

		this.lastX = x;
		this.lastY = y;
		this.lastZ = z;
		this.lastYaw = yaw;
		this.lastPitch = pitch;
		this.lastOnGround = onGround;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public void setPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setPosition(BlockPos pos) {
		this.x = pos.getX()+.5;
		this.y = pos.getY();
		this.z = pos.getZ()+.5;
	}

}
