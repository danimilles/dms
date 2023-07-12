package com.hairyworld.dms.model.event;

import org.springframework.context.ApplicationEvent;

public class NewEntityEvent extends ApplicationEvent {

    public NewEntityEvent(final Object source) {
        super(source);
    }
}
