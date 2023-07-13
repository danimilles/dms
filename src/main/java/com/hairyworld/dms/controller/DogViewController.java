package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteEntityEvent;
import com.hairyworld.dms.model.event.NewEntityEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateClientViewData;
import com.hairyworld.dms.model.view.DogClientViewData;
import com.hairyworld.dms.model.view.PaymentClientViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class DogViewController extends AbstractController {

    @FXML
    private TableView dogViewFormClientTable;
    @FXML
    private TableColumn dogViewClientNameTableColum;
    @FXML
    private TableColumn dogViewClientPhoneTableColum;
    @FXML
    private TableView dogViewFormDateTable;
    @FXML
    private TableColumn dogViewFormDateDateStartTableColumn;
    @FXML
    private TableColumn dogViewFormDateDateEndTableColumn;
    @FXML
    private TableColumn dogViewFormDateClientNameTableColumn;
    @FXML
    private TableColumn dogViewFormDateServiceTableColumn;
    @FXML
    private TableColumn dogViewFormDateDescriptionTableColumn;
    @FXML
    private ImageView dogImage;
    @FXML
    private TextField dogViewFormName;
    @FXML
    private TextField dogViewFormRace;
    @FXML
    private TextField dogViewFormObservations;
    @FXML
    private TextField dogViewFormMaintainment;
    @FXML
    private TextField dogViewFormNextDate;
    @FXML
    private Button addClientToDogButton;
    @FXML
    private GridPane root;

    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateDateStartTableColumn;
    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateDateEndTableColumn;
    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateServiceTableColumn;
    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateDogTableColumn;
    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateDescriptionTableColumn;
    @FXML
    private TableColumn<DateClientViewData, String> clientViewDateActionsTableColumn;
    @FXML
    private TableView<DateClientViewData> clientViewDateTable;
    @FXML
    private Button addDateButton;

    @FXML
    private TableView<PaymentClientViewData> clientViewPaymentTable;
    @FXML
    private TableColumn<PaymentClientViewData, Number> clientViewPaymentAmountTableColumn;
    @FXML
    private TableColumn<PaymentClientViewData, String> clientViewPaymentServiceTableColumn;
    @FXML
    private TableColumn<PaymentClientViewData, String> clientViewPaymentDescriptionTableColumn;
    @FXML
    private TableColumn<PaymentClientViewData, String> clientViewPaymentDateTableColumn;
    @FXML
    private TableColumn<PaymentClientViewData, String> clientViewPaymentActionsTableColumn;
    @FXML
    private Button addPaymentButton;

    @FXML
    private TableView<DogClientViewData> clientViewFormDogTable;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewFormDogRaceTableColumn;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewFormDogNameTableColumn;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewFormDogObservationsTableColumn;
    @FXML
    private Button addDogButton;

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
    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

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
        clientViewData = ClientViewData.builder()
                .dogs(new ArrayList<>()).dates(new ArrayList<>()).payments(new ArrayList<>()).build();

        createFormValidations();
        bindDogTable();
        bindPaymentTable();
        bindDateTable();
        createTableResponsiveness(clientViewPaymentTable);
        createTableResponsiveness(clientViewFormDogTable);
        createTableResponsiveness(clientViewDateTable);

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.onCloseRequestProperty().setValue(e -> {
            clientViewDateTab.getTabPane().getSelectionModel().selectFirst();
        });
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

    public void showView(final Long clientId, final Long dogId) {
        chargeDogViewData(dogId);
        createSubmitButtonAction();
        createDeleteButtonAction();
        stage.show();
    }

    private void chargeDogViewData(final Long clientId) {
        if (clientId != null) {
            fillForm(clientId);
        } else {
            cleanForm();
        }
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createFormValidations().validate()) {
                clientViewData = ClientViewData.builder()
                        .id(clientViewData.getId())
                        .name(clientViewFormName.getText())
                        .phone(clientViewFormPhone.getText())
                        .observations(clientViewFormObservations.getText())
                        .dogs(clientViewData.getDogs())
                        .build();

                dmsCommunicationFacadeImpl.saveClient(clientViewData);
                context.publishEvent(new NewEntityEvent(event.getSource(), clientViewData.getId()));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteButton.setOnAction(event -> {
            if (clientViewData.getId() != null) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setTitle("Borrar cliente");
                alert.setContentText("¿Estas seguro de que quieres borrar el cliente? " +
                        "Se borraran las citas del cliente, los perros que no tengan otro dueño y" +
                        " se eliminaran los datos del cliente de los pagos.");
                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deleteClient(clientViewData.getId());
                    context.publishEvent(new DeleteEntityEvent(event.getSource(), clientViewData.getId()));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }

    private void bindDogTable() {
        clientViewFormDogNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientViewFormDogRaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRace()));
        clientViewFormDogObservationsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObservations()));
        clientViewFormDogTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            }
        });
    }

    private void bindDateTable() {
        clientViewDateDateStartTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimestart())));
        clientViewDateDateEndTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimeend())));
        clientViewDateServiceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService() != null ?
                cellData.getValue().getService().getDescription() : null));
        clientViewDateDescriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        clientViewDateDogTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDogName()));
    }

    private void bindPaymentTable() {
        clientViewPaymentDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetime())));
        clientViewPaymentAmountTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount().doubleValue()));
        clientViewPaymentServiceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService() != null ?
                cellData.getValue().getService().getDescription() : null));
        clientViewPaymentDescriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void fillForm(final Long clientId) {
        clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientId);

        stage.setTitle("Vista de cliente");
        stage.setHeight(root.getPrefHeight());
        stage.setWidth(root.getPrefWidth());

        clientViewFormName.setText(clientViewData.getName());
        clientViewFormPhone.setText(String.valueOf(clientViewData.getPhone()));
        clientViewFormObservations.setText(clientViewData.getObservations());
        clientViewFormNextDate.setText(DmsUtils.dateToString(clientViewData.getNextDate()));

        clientViewPaymentTab.setDisable(false);
        clientViewDateTab.setDisable(false);
        nextDateLabel.setVisible(true);
        clientViewFormNextDate.setVisible(true);
        clientViewFormNextDate.setDisable(true);
        deleteButton.setDisable(false);
        addDogButton.setDisable(false);
        clientViewFormDogTable.getParent().setVisible(true);

        clientViewFormDogTable.setItems(FXCollections.observableArrayList(clientViewData.getDogs()));
        clientViewDateTable.setItems(FXCollections.observableArrayList(clientViewData.getDates()));
        clientViewPaymentTable.setItems(FXCollections.observableArrayList(clientViewData.getPayments()));
    }

    private void cleanForm() {
        clientViewData = ClientViewData.builder()
                .dogs(new ArrayList<>()).dates(new ArrayList<>()).payments(new ArrayList<>()).build();

        stage.setTitle("Crear cliente");
        stage.setHeight(300);
        stage.setWidth(root.getPrefWidth());

        clientViewFormName.clear();
        clientViewFormPhone.clear();
        clientViewFormObservations.clear();
        clientViewFormNextDate.clear();

        nextDateLabel.setVisible(false);
        clientViewFormNextDate.setVisible(false);
        deleteButton.setDisable(true);
        clientViewPaymentTab.setDisable(true);
        clientViewDateTab.setDisable(true);
        addDogButton.setDisable(true);
        clientViewFormDogTable.getParent().setVisible(false);

        clientViewFormDogTable.setItems(FXCollections.observableArrayList(clientViewData.getDogs()));
        clientViewDateTable.setItems(FXCollections.observableArrayList(clientViewData.getDates()));
        clientViewPaymentTable.setItems(FXCollections.observableArrayList(clientViewData.getPayments()));
    }
}
