package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;

public class EventChat extends Event<EventChat> {

    String message;

    public EventChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
