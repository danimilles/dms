package com.hairyworld.dms.model.event;

public class DeleteEntityEvent extends  EntityUpdateEvent {

    public DeleteEntityEvent(final Object source, final Long entityId) {
        super(source, entityId);
    }
}
