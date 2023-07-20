package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.entity.EntityType;
import org.springframework.context.ApplicationEvent;

public abstract class UpdateEntityEvent extends ApplicationEvent {
    private final Long entityId;
    private final EntityType entityType;
    
    protected UpdateEntityEvent(final Object source) {
        super(source);
        entityType = null;
        entityId = null;
    }
    
    protected UpdateEntityEvent(final Object source, final Long entityId, final EntityType entityType) {
        super(source);
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public Long getId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}