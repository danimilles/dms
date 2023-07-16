package com.hairyworld.dms.model.event;

import com.hairyworld.dms.model.view.SearchTableRow;
import org.springframework.context.ApplicationEvent;

public class OpenSearchTableEvent extends ApplicationEvent {

    private final SearchTableRow entity;

    public OpenSearchTableEvent(final Object source) {
        super(source);
        this.entity = null;
    }

    public OpenSearchTableEvent(final Object source, final SearchTableRow entity) {
        super(source);
        this.entity = entity;
    }

    public SearchTableRow getEntity() {
        return entity;
    }
}
