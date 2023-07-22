package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.view.DataType;

public class NewDataEvent extends UpdateDataEvent {

    public NewDataEvent(final Object source) {
        super(source);
    }

    public NewDataEvent(final Object source, final Long entityId, final DataType dataType) {
        super(source, entityId, dataType);
    }
}
