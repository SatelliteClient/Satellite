/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.satellite.utils;

public class Rotation {

    private float yaw;

    private float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    public Rotation add(Rotation other) {
        return new Rotation(
                this.yaw + other.yaw,
                this.pitch + other.pitch
        );
    }
    
    public Rotation subtract(Rotation other) {
        return new Rotation(
                this.yaw - other.yaw,
                this.pitch - other.pitch
        );
    }

    public Rotation clamp() {
        return new Rotation(
                this.yaw,
                clampPitch(this.pitch)
        );
    }

    public Rotation normalize() {
        return new Rotation(
                normalizeYaw(this.yaw),
                this.pitch
        );
    }

    public Rotation normalizeAndClamp() {
        return new Rotation(
                normalizeYaw(this.yaw),
                clampPitch(this.pitch)
        );
    }

    public boolean isReallyCloseTo(Rotation other) {
        return yawIsReallyClose(other) && Math.abs(this.pitch - other.pitch) < 0.01;
    }

    public boolean yawIsReallyClose(Rotation other) {
        float yawDiff = Math.abs(normalizeYaw(yaw) - normalizeYaw(other.yaw)); // you cant fool me
        return (yawDiff < 0.01 || yawDiff > 359.99);
    }

    public static float clampPitch(float pitch) {
        return Math.max(-90, Math.min(90, pitch));
    }

    public static float normalizeYaw(float yaw) {
        float newYaw = yaw % 360F;
        if (newYaw < -180F) {
            newYaw += 360F;
        }
        if (newYaw > 180F) {
            newYaw -= 360F;
        }
        return newYaw;
    }

    @Override
    public String toString() {
        return "Yaw: " + yaw + ", Pitch: " + pitch;
    }
}
