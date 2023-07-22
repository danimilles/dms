package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.view.DataType;

public class DeleteDataEvent extends UpdateDataEvent {

    public DeleteDataEvent(final Object source) {
        super(source);
    }

    public DeleteDataEvent(final Object source, final Long entityId, final DataType dataType) {
        super(source, entityId, dataType);
    }
}
