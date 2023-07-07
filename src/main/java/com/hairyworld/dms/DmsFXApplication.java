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
import org.kordamp.bootstrapfx.BootstrapFX;
import java.io.IOException;

public class DmsFXApplication extends Application {
	private static final Logger LOGGER = LogManager.getLogger(DmsFXApplication.class);
	private ConfigurableApplicationContext applicationContext;
	private static Scene scene;

	@Override
	public void start(final Stage primaryStage) {
		try {
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			LOGGER.error("Error loading Main view: ", e);
		}
	}

	@Override
	public void init() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getClassLoader().getResource("fxml/MainView.fxml"));
		scene = new Scene(loader.load());
		scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
		applicationContext = new SpringApplicationBuilder(DmsApplication.class).run();
	}

	@Override
	public void stop() {
		applicationContext.close();
		Platform.exit();
	}
}
