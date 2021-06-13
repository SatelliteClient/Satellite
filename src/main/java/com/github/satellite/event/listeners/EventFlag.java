package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;

public class EventFlag extends Event<EventFlag> {

    public int flag;
    public boolean set;
    public int entity;

    public EventFlag(int flag, boolean set, int entity) {
        this.flag = flag;
        this.set = set;
        this.entity = entity;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public int getEntity() {
        return entity;
    }

    public void setEntity(int entity) {
        this.entity = entity;
    }
}
