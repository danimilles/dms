package com.hairyworld.dms.model.event;

public class NewEntityEvent extends EntityUpdateEvent {

    public NewEntityEvent(final Object source, final Long entityId) {
        super(source, entityId);
    }
}
