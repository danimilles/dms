package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.view.DataType;
import org.springframework.context.ApplicationEvent;

public abstract class UpdateDataEvent extends ApplicationEvent {
    private final Long entityId;
    private final DataType dataType;
    
    protected UpdateDataEvent(final Object source) {
        super(source);
        dataType = null;
        entityId = null;
    }
    
    protected UpdateDataEvent(final Object source, final Long entityId, final DataType dataType) {
        super(source);
        this.entityId = entityId;
        this.dataType = dataType;
    }

    public Long getId() {
        return entityId;
    }

    public DataType getDataType() {
        return dataType;
    }
}
