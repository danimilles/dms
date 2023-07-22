package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.entity.EntityType;

public class NewDataEvent extends UpdateDataEvent {

    public NewDataEvent(final Object source) {
        super(source);
    }

    public NewDataEvent(final Object source, final Long entityId, final EntityType entityType) {
        super(source, entityId, entityType);
    }
}