package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.event.DeleteEntityEvent;
import com.hairyworld.dms.model.event.EntityUpdateEvent;
import com.hairyworld.dms.model.event.NewEntityEvent;
import com.hairyworld.dms.model.view.clientview.ClientViewData;
import com.hairyworld.dms.model.view.clientview.DateClientViewData;
import com.hairyworld.dms.model.view.clientview.DogClientViewData;
import com.hairyworld.dms.model.view.clientview.PaymentClientViewData;
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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

import static com.hairyworld.dms.util.Path.ICON_IMAGE;

@Component
public class ClientViewController extends AbstractController implements ApplicationListener<EntityUpdateEvent> {


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
    private TableView<DogClientViewData> clientViewDogTable;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewDogRaceTableColumn;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewDogNameTableColumn;
    @FXML
    private TableColumn<DogClientViewData, String> clientViewDogMaintainmentTableColumn;
    @FXML
    private Button addDogButton;

    @FXML
    private Tab clientViewPaymentTab;
    @FXML
    private Tab clientViewDateTab;
    @FXML
    private Tab clientViewDataTab;
    @FXML
    private TextField clientViewName;
    @FXML
    private TextField clientViewPhone;
    @FXML
    private TextField clientViewObservations;
    @FXML
    private TextField clientViewNextDate;
    @FXML
    private Label nextDateLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;
    private final DogViewController dogViewController;

    private Scene scene;
    private Stage stage;
    private ClientViewData clientViewData;


    public ClientViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context,
                                final DogViewController dogViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
        this.dogViewController = dogViewController;
    }

    @FXML
    private void initialize() {
        clientViewData = ClientViewData.builder()
                .dogs(new ArrayList<>()).dates(new ArrayList<>()).payments(new ArrayList<>()).build();

        createValidations();
        bindDogTable();
        bindPaymentTable();
        bindDateTable();
        createTableResponsiveness(clientViewPaymentTable);
        createTableResponsiveness(clientViewDogTable);
        createTableResponsiveness(clientViewDateTable);

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream(ICON_IMAGE)));
        stage.onCloseRequestProperty().setValue(e -> {
            clientViewDateTab.getTabPane().getSelectionModel().selectFirst();
        });
    }

    private Validator createValidations() {
        final Validator validator = new Validator();

        validator.createCheck()
                .dependsOn("clientViewName", clientViewName.textProperty())
                .withMethod(c -> {
                    final String name = c.get("clientViewName");
                    if (name == null || name.isEmpty()) {
                        c.error("El nombre no puede estar vacio");
                    }
                })
                .decorates(clientViewName)
                .immediate();

        validator.createCheck()
                .dependsOn("clientViewPhone", clientViewPhone.textProperty())
                .withMethod(c -> {
                    final String phone = c.get("clientViewPhone");
                    if (phone == null || phone.isEmpty() || !phone.matches("^\\+?[0-9]{7,14}$")) {
                        c.error("El telefono no puede estar vacio y debe ser correcto");
                    }
                })
                .decorates(clientViewPhone)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long clientId) {
        chargeClientViewData(clientId);
        createSubmitButtonAction();
        createDeleteButtonAction();
        addDogButton.setOnAction(event -> showDogView(null));
        if (stage.getOwner() == null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(source);
        }
        stage.show();
    }

    private void chargeClientViewData(final Long clientId) {
        if (clientId != null) {
            fill(clientId);
        } else {
            clean();
        }

        clientViewDogTable.setItems(FXCollections.observableArrayList(clientViewData.getDogs()));
        clientViewDateTable.setItems(FXCollections.observableArrayList(clientViewData.getDates()));
        clientViewPaymentTable.setItems(FXCollections.observableArrayList(clientViewData.getPayments()));
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createValidations().validate()) {
                clientViewData = ClientViewData.builder()
                        .id(clientViewData.getId())
                        .name(clientViewName.getText())
                        .phone(clientViewPhone.getText())
                        .observations(clientViewObservations.getText())
                        .dogs(clientViewData.getDogs())
                        .dates(clientViewData.getDates())
                        .payments(clientViewData.getPayments())
                        .build();

                dmsCommunicationFacadeImpl.saveClient(clientViewData);
                context.publishEvent(new NewEntityEvent(event.getSource(), clientViewData.getId(), EntityType.CLIENT));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteButton.setOnAction(event -> {
            if (clientViewData.getId() != null) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                        .add(new Image(this.getClass().getClassLoader().getResourceAsStream(ICON_IMAGE)));
                alert.setTitle("Borrar cliente");
                alert.setContentText("¿Estas seguro de que quieres borrar el cliente? " +
                        "Se borraran las citas del cliente, los perros que no tengan otro dueño y" +
                        " se eliminaran los datos del cliente de los pagos.");

                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deleteClient(clientViewData.getId());
                    context.publishEvent(new DeleteEntityEvent(event.getSource(), clientViewData.getId(), EntityType.CLIENT));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }

    private void bindDogTable() {
        clientViewDogNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientViewDogRaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRace()));
        clientViewDogMaintainmentTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaintainment()));
        clientViewDogTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                showDogView(clientViewDogTable.getSelectionModel().getSelectedItem().getId());
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

    private void fill(final Long clientId) {
        clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientId);

        stage.setTitle("Vista de cliente");
        stage.setHeight(root.getPrefHeight());
        stage.setWidth(root.getPrefWidth());

        clientViewName.setText(clientViewData.getName());
        clientViewPhone.setText(String.valueOf(clientViewData.getPhone()));
        clientViewObservations.setText(clientViewData.getObservations());
        clientViewNextDate.setText(DmsUtils.dateToString(clientViewData.getNextDate()));

        clientViewPaymentTab.setDisable(false);
        clientViewDateTab.setDisable(false);
        nextDateLabel.setVisible(true);
        clientViewNextDate.setVisible(true);
        clientViewNextDate.setDisable(true);
        deleteButton.setDisable(false);
        addDogButton.setDisable(false);
        clientViewDogTable.getParent().setVisible(true);
    }

    private void clean() {
        clientViewData = ClientViewData.builder()
                .dogs(new ArrayList<>()).dates(new ArrayList<>()).payments(new ArrayList<>()).build();

        stage.setTitle("Crear cliente");
        stage.setHeight(300);
        stage.setWidth(root.getPrefWidth());

        clientViewName.clear();
        clientViewPhone.clear();
        clientViewObservations.clear();
        clientViewNextDate.clear();

        nextDateLabel.setVisible(false);
        clientViewNextDate.setVisible(false);
        deleteButton.setDisable(true);
        clientViewPaymentTab.setDisable(true);
        clientViewDateTab.setDisable(true);
        addDogButton.setDisable(true);
        clientViewDogTable.getParent().setVisible(false);
    }

    private void showDogView(final Long dogId) {
        dogViewController.showView(stage, clientViewData.getId(), dogId);
    }

    @Override
    public void onApplicationEvent(final EntityUpdateEvent event) {
        if (EntityType.DOG.equals(event.getEntityType())) {
            clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientViewData.getId());
            clientViewDogTable.setItems(FXCollections.observableArrayList(clientViewData.getDogs()));
            clientViewDateTable.setItems(FXCollections.observableArrayList(clientViewData.getDates()));
            clientViewPaymentTable.setItems(FXCollections.observableArrayList(clientViewData.getPayments()));
        }
    }
}
