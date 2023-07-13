package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.EntityType;

public class NewEntityEvent extends EntityUpdateEvent {

    public NewEntityEvent(final Object source) {
        super(source);
    }

    public NewEntityEvent(final Object source, final Long entityId, final EntityType entityType) {
        super(source, entityId, entityType);
    }
}
