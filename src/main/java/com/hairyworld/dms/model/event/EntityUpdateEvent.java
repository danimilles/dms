package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.EntityType;
import org.springframework.context.ApplicationEvent;

public abstract class EntityUpdateEvent extends ApplicationEvent {
    private final Long entityId;
    private final EntityType entityType;
    
    protected EntityUpdateEvent(final Object source) {
        super(source);
        entityType = null;
        entityId = null;
    }
    
    protected EntityUpdateEvent(final Object source, final Long entityId, final EntityType entityType) {
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
