package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class EventHandleTeleport extends Event<EventHandleTeleport> {

	public double x, y, z, yaw, pitch;
	public int teleportId;
	boolean cancellTeleporting;

	public EventHandleTeleport(double x, double y, double z, double yaw, double pitch, int teleportId) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.teleportId = teleportId;
		this.cancellTeleporting = false;
	}

	public EventHandleTeleport(SPacketPlayerPosLook packetIn) {
		this(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getYaw(), packetIn.getPitch(), packetIn.getTeleportId());
	}

	public boolean isCancellTeleporting() {
		return cancellTeleporting;
	}

	public void setCancellTeleporting(boolean cancellTeleporting) {
		this.cancellTeleporting = cancellTeleporting;
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

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public int getTeleportId() {
		return teleportId;
	}

	public void setTeleportId(int teleportId) {
		this.teleportId = teleportId;
	}
}
