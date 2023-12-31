package com.hairyworld.dms.util;

import java.util.List;

public class Path {

    private Path() {
    }

    public static final String FXML_ROOT = "fxml/";
    public static final String MAIN_VIEW = FXML_ROOT + "MainView.fxml";
    public static final String CLIENT_VIEW = FXML_ROOT + "ClientView.fxml";
    public static final String DOG_VIEW = FXML_ROOT + "DogView.fxml";
    public static final String SEARCH_VIEW = FXML_ROOT + "SearchView.fxml";
    public static final String SERVICE_VIEW = FXML_ROOT + "ServiceView.fxml";
    public static final String PAYMENT_VIEW = FXML_ROOT + "PaymentView.fxml";
    public static final String DATE_VIEW = FXML_ROOT + "DateView.fxml";
    public static final String IMAGES_ROOT = "images/";
    public static final String ICON_IMAGE = IMAGES_ROOT + "icon.png";

    public static List<String> getViews() {
        return List.of(CLIENT_VIEW, DOG_VIEW, MAIN_VIEW, SEARCH_VIEW, SERVICE_VIEW, PAYMENT_VIEW, DATE_VIEW);
    }
}
