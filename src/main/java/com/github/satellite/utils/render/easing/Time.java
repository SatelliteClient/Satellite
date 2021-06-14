package com.github.satellite.utils.render.easing;

import net.minecraft.client.Minecraft;

public class Time {

    private long lastMS;
    private long time = 0l;

    public void update(long deltaTime) {
        time += deltaTime;
    }

    public Time() {
        this.lastMS = 0L;
    }

    public long getCurrentMS() {
        return time;
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public boolean hasReached(float f) {
        return (float)(this.getCurrentMS() - this.lastMS) >= f;
    }

    public boolean hasReached(double f) {
        return (double)(this.getCurrentMS() - this.lastMS) >= f;
    }

    public boolean hasReached(long f) {
        return (float)(this.getCurrentMS() - this.lastMS) >= (float)f;
    }

    public void reset() {
        this.time = 0L;
        this.lastMS = 0L;
    }

    public void setLastMS(long currentMS) {
        this.lastMS = currentMS;
    }
}
