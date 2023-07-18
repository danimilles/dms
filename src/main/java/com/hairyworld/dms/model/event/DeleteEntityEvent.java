package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.entity.EntityType;

public class DeleteEntityEvent extends UpdateEntityEvent {

    public DeleteEntityEvent(final Object source) {
        super(source);
    }

    public DeleteEntityEvent(final Object source, final Long entityId, final EntityType entityType) {
        super(source, entityId, entityType);
    }
}
