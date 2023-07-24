package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteDataEvent;
import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.PaymentViewData;
import com.hairyworld.dms.model.view.ServiceViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentViewController extends AbstractController {

    @FXML
    private GridPane root;

    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private Button searchClientButton;
    @FXML
    private TextField amountTextField;
    @FXML
    private ChoiceBox<ServiceViewData> serviceTextField;

    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;
    private final SearchViewController searchViewController;

    private Scene scene;
    private Stage stage;
    private PaymentViewData paymentViewData;


    public PaymentViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context,
                                 final SearchViewController searchViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
        this.searchViewController = searchViewController;
    }

    @FXML
    private void initialize() {
        paymentViewData = PaymentViewData.builder().build();

        createValidations();

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(getIcon());
    }

    private Validator createValidations() {
        final Validator validator = new Validator();

        validator.createCheck()
                .dependsOn("amountTextField", amountTextField.textProperty())
                .withMethod(c -> {
                    final String name = c.get("amountTextField");
                    try {
                        BigDecimal.valueOf(Double.parseDouble(name));
                    } catch (final NumberFormatException e) {
                        c.error("La cantidad no puede estar vacia y debe ser un numero (Ej: 100.00, 10, 0.5)");
                    }
                })
                .decorates(amountTextField)
                .immediate();

        validator.createCheck()
                .dependsOn("dateTextField", dateTextField.textProperty())
                .withMethod(c -> {
                    final String value = c.get("dateTextField");
                    if (value != null && !value.isEmpty() &&
                            (DmsUtils.parseDate(value) == null ||
                                    !value.matches("^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}(:\\d{2})?$"))) {
                            c.error("La fecha debe estar en formato dd/MM/yyyy HH:mm (Ej: 01/01/2020 00:00)");
                    }
                })
                .decorates(dateTextField)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long paymentId, final Long clientId) {
        chargePaymentViewData(paymentId, clientId);
        createSubmitButtonAction();
        createDeleteButtonAction();
        createSearchClientButtonAction();
        createServiceField();

        if (stage.getOwner() == null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(source);
        }
        stage.show();
    }

    private void createServiceField() {
        serviceTextField.getItems().clear();
        final List<ServiceViewData> serviceViewData = new ArrayList<>();
        serviceViewData.add(null);
        serviceViewData.addAll(dmsCommunicationFacadeImpl.getServiceTableData());
        serviceTextField.setConverter(new StringConverter<>() {
            @Override
            public String toString(final ServiceViewData object) {
                return object != null ? object.getDescription() : "";
            }

            @Override
            public ServiceViewData fromString(String string) {
                return null;
            }
        });
        serviceTextField.getItems().addAll(serviceViewData);
        serviceTextField.setValue(paymentViewData.getService());
    }

    private void createSearchClientButtonAction() {
        searchClientButton.setOnAction(event -> {
                paymentViewData.setClient(
                        (ClientViewData) searchViewController.showView(stage, DogViewData.builder().build()));
                if (paymentViewData.getClient() != null) {
                    searchClientButton.setText(paymentViewData.getClient().getName());
                } else {
                    searchClientButton.setText(null);
                }
        });
    }

    private void chargePaymentViewData(final Long paymentId, final Long clientId) {
        if (paymentId != null) {
            fill(paymentId);
        } else {
            clean(clientId);
        }
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createValidations().validate()) {
                paymentViewData = PaymentViewData.builder()
                        .id(paymentViewData.getId())
                        .description(descriptionTextField.getText())
                        .amount(BigDecimal.valueOf(Double.parseDouble(amountTextField.getText())))
                        .datetime(DmsUtils.parseDate(dateTextField.getText()))
                        .service(serviceTextField.getValue())
                        .client(paymentViewData.getClient())
                        .build();

                context.publishEvent(new NewDataEvent(event.getSource(),
                        dmsCommunicationFacadeImpl.savePayment(paymentViewData), DataType.PAYMENT));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteButton.setOnAction(event -> {
            if (paymentViewData.getId() != null) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                        .add(getIcon());
                alert.setTitle("Borrar servicio");
                alert.setContentText("¿Estas seguro de que quieres borrar el cobro? " +
                        "Se borraran su informacion de las citas y cobros asociados.");

                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deletePayment(paymentViewData);
                    context.publishEvent(new DeleteDataEvent(event.getSource(), paymentViewData.getId(), DataType.PAYMENT));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }



    private void fill(final Long serviceId) {
        paymentViewData = dmsCommunicationFacadeImpl.getPaymentViewData(serviceId);

        descriptionTextField.setText(paymentViewData.getDescription());
        dateTextField.setText(DmsUtils.dateToString(paymentViewData.getDatetime()));
        amountTextField.setText(paymentViewData.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        serviceTextField.setValue(paymentViewData.getService());
        searchClientButton.setText(paymentViewData.getClient() != null ? paymentViewData.getClient().getName()
                : null);

        stage.setTitle("Vista de cobro");

        deleteButton.setDisable(false);
    }

    private void clean(final Long clientId) {
        paymentViewData = PaymentViewData.builder().build();

        descriptionTextField.clear();
        dateTextField.clear();
        amountTextField.clear();
        serviceTextField.setValue(null);

        paymentViewData.setClient(dmsCommunicationFacadeImpl.getClientViewData(clientId));
        if (paymentViewData.getClient() != null) {
            searchClientButton.setText(paymentViewData.getClient().getName());
            searchClientButton.setDisable(true);
        } else {
            searchClientButton.setText(null);
            searchClientButton.setDisable(false);
        }

        descriptionTextField.clear();

        stage.setTitle("Crear cobro");

        deleteButton.setDisable(true);
    }
}
