package com.hairyworld.dms.handler;

import com.hairyworld.dms.event.StageReadyEvent;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    @Override
    public void onApplicationEvent(final StageReadyEvent event) {
        final Stage stage = event.getStage();
        System.out.println("Conseguido!");
    }
}
