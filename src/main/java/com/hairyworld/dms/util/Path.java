package com.hairyworld.dms.util;

import java.util.List;

public class Path {

    private Path() {
    }

    public static final String FXML_ROOT = "fxml/";
    public static final String MAIN_VIEW = FXML_ROOT + "MainView.fxml";
    public static final String CLIENT_VIEW = FXML_ROOT + "ClientView.fxml";
    public static final String DOG_VIEW = FXML_ROOT + "DogView.fxml";

    public static List<String> getViews() {
        return List.of(CLIENT_VIEW, MAIN_VIEW);
    }
}
