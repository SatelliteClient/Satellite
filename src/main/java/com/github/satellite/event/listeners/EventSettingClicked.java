package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;
import com.github.satellite.setting.Setting;

public class EventSettingClicked extends Event<EventSettingClicked> {

	Setting setting;

    public EventSettingClicked(Setting setting) {
        this.setting = setting;
    }

    public Setting getMessage() {
        return setting;
    }

    public void setMessage(Setting setting) {
        this.setting = setting;
    }
}
