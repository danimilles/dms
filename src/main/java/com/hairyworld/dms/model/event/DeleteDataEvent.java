package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.entity.EntityType;

public class DeleteDataEvent extends UpdateDataEvent {

    public DeleteDataEvent(final Object source) {
        super(source);
    }

    public DeleteDataEvent(final Object source, final Long entityId, final EntityType entityType) {
        super(source, entityId, entityType);
    }
}
