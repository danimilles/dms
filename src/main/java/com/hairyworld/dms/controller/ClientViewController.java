package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteEntityEvent;
import com.hairyworld.dms.model.event.NewEntityEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ClientViewController {
    @FXML
    private GridPane root;
    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

    @FXML
    private Tab clientViewPaymentTab;
    @FXML
    private Tab clientViewDateTab;
    @FXML
    private Tab clientViewDataTab;
    @FXML
    private TextField clientViewFormName;
    @FXML
    private TextField clientViewFormPhone;
    @FXML
    private TextField clientViewFormObservations;
    @FXML
    private TextField clientViewFormNextDate;
    @FXML
    private Label nextDateLabel;


    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext applicationContext;

    private Scene scene;
    private Stage stage;
    private ClientViewData clientViewData;


    public ClientViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext applicationContext) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.applicationContext = applicationContext;
    }

    @FXML
    private void initialize() {
        clientViewData = ClientViewData.builder().build();
        createFormValidations();
        scene = new Scene(root);
        stage = new Stage();
        stage.setTitle("Vista de cliente");
        stage.setScene(scene);
        stage.show();
    }

    private Validator createFormValidations() {
        final Validator validator = new Validator();
        validator.createCheck()
                .dependsOn("clientViewFormName", clientViewFormName.textProperty())
                .withMethod(c -> {
                    final String name = c.get("clientViewFormName");
                    if (name == null || name.isEmpty()) {
                        c.error("El nombre no puede estar vacio");
                    }
                })
                .decorates(clientViewFormName)
                .immediate();

        validator.createCheck()
                .dependsOn("clientViewFormPhone", clientViewFormPhone.textProperty())
                .withMethod(c -> {
                    final String phone = c.get("clientViewFormPhone");
                    if (phone == null || phone.isEmpty() || !phone.matches("^\\+?[0-9]{7,14}$")) {
                        c.error("El telefono no puede estar vacio y debe ser correcto");
                    }
                })
                .decorates(clientViewFormPhone)
                .immediate();
        return validator;
    }

    public void showView(final Long clientId) {
        chargeClientViewData(clientId);
    }

    private void chargeClientViewData(final Long clientId) {
        if (clientId != null) {
            fillForm(clientId);
        } else {
            cleanForm();
        }

        createSubmitButtonAction();
        createDeleteButtonAction();
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createFormValidations().validate()) {
                clientViewData = ClientViewData.builder()
                        .id(clientViewData.getId())
                        .name(clientViewFormName.getText())
                        .phone(clientViewFormPhone.getText())
                        .observations(clientViewFormObservations.getText())
                        .build();

                dmsCommunicationFacadeImpl.saveClient(clientViewData);
                applicationContext.publishEvent(new NewEntityEvent(event.getSource()));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteButton.setOnAction(event -> {
            if (clientViewData.getId() != null) {
                dmsCommunicationFacadeImpl.deleteClient(clientViewData.getId());
                applicationContext.publishEvent(new DeleteEntityEvent(event.getSource()));
                stage.close();
            }
        });
    }

    private void fillForm(Long clientId) {
        clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientId);

        clientViewFormName.setText(clientViewData.getName());
        clientViewFormPhone.setText(String.valueOf(clientViewData.getPhone()));
        clientViewFormObservations.setText(clientViewData.getObservations());
        clientViewFormNextDate.setText(DmsUtils.dateToString(clientViewData.getNextDate()));
        clientViewPaymentTab.setDisable(false);
        clientViewDateTab.setDisable(false);
        nextDateLabel.setVisible(true);
        clientViewFormNextDate.setDisable(true);
        deleteButton.setDisable(false);
    }

    private void cleanForm() {
        clientViewData = ClientViewData.builder().build();
        clientViewFormName.clear();
        clientViewFormPhone.clear();
        clientViewFormObservations.clear();
        clientViewFormNextDate.clear();
        nextDateLabel.setVisible(false);
        clientViewFormNextDate.setVisible(false);
        deleteButton.setDisable(true);
        clientViewPaymentTab.setDisable(true);
        clientViewDateTab.setDisable(true);
    }
}
