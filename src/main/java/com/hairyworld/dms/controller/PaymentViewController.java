package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteDataEvent;
import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.ServiceViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentViewController extends AbstractController {


    @FXML
    private GridPane root;


    @FXML
    private TextField descriptionField;

    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;

    private Scene scene;
    private Stage stage;
    private ServiceViewData serviceViewData;


    public PaymentViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
    }

    @FXML
    private void initialize() {
        serviceViewData = ServiceViewData.builder().build();

        createValidations();

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(getIcon());
    }

    private Validator createValidations() {
        final Validator validator = new Validator();

        validator.createCheck()
                .dependsOn("descriptionField", descriptionField.textProperty())
                .withMethod(c -> {
                    final String name = c.get("descriptionField");
                    if (name == null || name.isEmpty()) {
                        c.error("La descripcion no puede estar vacia");
                    }
                })
                .decorates(descriptionField)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long serviceId) {
        chargeServiceViewData(serviceId);
        createSubmitButtonAction();
        createDeleteButtonAction();

        if (stage.getOwner() == null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(source);
        }
        stage.show();
    }

    private void chargeServiceViewData(final Long clientId) {
        if (clientId != null) {
            fill(clientId);
        } else {
            clean();
        }
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createValidations().validate()) {
                serviceViewData = ServiceViewData.builder()
                        .id(serviceViewData.getId())
                        .description(descriptionField.getText())
                        .build();

                dmsCommunicationFacadeImpl.saveService(serviceViewData);
                context.publishEvent(new NewDataEvent(event.getSource(), serviceViewData.getId(), DataType.SERVICE));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteButton.setOnAction(event -> {
            if (serviceViewData.getId() != null) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                        .add(getIcon());
                alert.setTitle("Borrar servicio");
                alert.setContentText("¿Estas seguro de que quieres borrar el servicio? " +
                        "Se borraran su informacion de las citas y cobros asociados.");

                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deleteService(serviceViewData);
                    context.publishEvent(new DeleteDataEvent(event.getSource(), serviceViewData.getId(), DataType.SERVICE));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }



    private void fill(final Long serviceId) {
        serviceViewData = dmsCommunicationFacadeImpl.getServiceViewData(serviceId);

        descriptionField.setText(serviceViewData.getDescription());
        stage.setTitle("Vista de cliente");
        stage.setHeight(150);
        stage.setWidth(root.getPrefWidth());

        deleteButton.setDisable(false);
    }

    private void clean() {
        serviceViewData = ServiceViewData.builder().build();

        descriptionField.clear();
        stage.setTitle("Crear servicio");
        stage.setHeight(150);
        stage.setWidth(root.getPrefWidth());

        deleteButton.setDisable(true);
    }
}
