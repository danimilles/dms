package com.hairyworld.dms.model.event;

import org.springframework.context.ApplicationEvent;

public abstract class EntityUpdateEvent extends ApplicationEvent {
    private final Long entityId;
    protected EntityUpdateEvent(Object source, Long entityId) {
        super(source);
        this.entityId = entityId;
    }

    public Long getId() {
        return entityId;
    }
}
