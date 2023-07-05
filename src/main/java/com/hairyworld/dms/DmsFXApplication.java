package com.hairyworld.dms;


import com.hairyworld.dms.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class DmsFXApplication extends Application {
	private ConfigurableApplicationContext applicationContext;

	@Override
	public void start(final Stage stage) {
		applicationContext.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(DmsApplication.class).run();
	}

	@Override
	public void stop() {
		applicationContext.close();
		Platform.exit();
	}
}
