package com.hairyworld.dms.model.event;

import org.springframework.context.ApplicationEvent;

public abstract class EntityUpdateEvent extends ApplicationEvent {
    protected EntityUpdateEvent(Object source) {
        super(source);
    }
}
