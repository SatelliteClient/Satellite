package satellite.event.listeners;

import satellite.event.Event;

public class EventHandleTeleport extends Event<EventHandleTeleport> {

	public double x, y, z, yaw, pitch, teleportId;
	boolean cancellTeleporting;

	public EventHandleTeleport(double x, double y, double z, double yaw, double pitch, double teleportId) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.teleportId = teleportId;
		this.cancellTeleporting=false;
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

	public double getTeleportId() {
		return teleportId;
	}

	public void setTeleportId(double teleportId) {
		this.teleportId = teleportId;
	}
}
