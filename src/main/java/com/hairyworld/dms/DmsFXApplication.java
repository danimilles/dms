package com.hairyworld.dms;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hairyworld.dms.util.Path.MAIN_VIEW;
import static com.hairyworld.dms.util.Path.getViews;

public class DmsFXApplication extends Application {
	private static final Logger LOGGER = LogManager.getLogger(DmsFXApplication.class);

	private ConfigurableApplicationContext applicationContext;

	@Override
	public void start(final Stage primaryStage) {
		try {
			final Scene scene = new Scene(loadViews().get(MAIN_VIEW));
			primaryStage.setTitle("Sistema de Gestion de citas de peluqueria canina");
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			LOGGER.error("Error loading Main view: ", e);
		}
	}

	private Map<String, Parent> loadViews() {
		return getViews().stream().collect(Collectors.toMap(Function.identity(), value -> {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getClassLoader().getResource(value));
			loader.setControllerFactory(applicationContext::getBean);
			try {
				return loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
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
