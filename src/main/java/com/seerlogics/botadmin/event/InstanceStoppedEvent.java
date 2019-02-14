package com.seerlogics.botadmin.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by bkane on 1/5/19.
 */
public class InstanceStoppedEvent extends ApplicationEvent {
    private String message;

    public InstanceStoppedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
