package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DogViewController extends AbstractController {

    @FXML
    private GridPane root;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;

    private Scene scene;
    private Stage stage;
    private ClientViewData clientViewData;


    public DogViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
    }

    @FXML
    private void initialize() {
        clientViewData = ClientViewData.builder().build();
        createFormValidations();

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private Validator createFormValidations() {
        final Validator validator = new Validator();
        return validator;
    }

    public void showView(final Long clientId, final Long dogId) {
        chargeClientViewData(clientId, dogId);
    }

    private void chargeClientViewData(final Long clientId, final Long dogId) {
        if (clientId != null) {
            fillForm(clientId);
        } else {
            cleanForm();
        }
    }

    private void fillForm(final Long clientId) {
        clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientId);

        stage.setTitle("Vista de cliente");
    }

    private void cleanForm() {
        clientViewData = ClientViewData.builder().build();

        stage.setTitle("Crear perro");
        stage.setHeight(300);
    }
}
