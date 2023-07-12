package com.hairyworld.dms;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static com.hairyworld.dms.util.Path.MAIN_VIEW;

public class DmsFXApplication extends Application {
	private static final Logger LOGGER = LogManager.getLogger(DmsFXApplication.class);

	private ConfigurableApplicationContext applicationContext;
	private Scene scene;

	@Override
	public void start(final Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getClassLoader().getResource(MAIN_VIEW));
			loader.setControllerFactory(applicationContext::getBean);

			scene = new Scene(loader.load());
			primaryStage.setTitle("Sistema de Gestion de citas de peluqueria canina");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			LOGGER.error("Error loading Main view: ", e);
		}
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
