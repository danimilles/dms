package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteDataEvent;
import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.event.UpdateDataEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.PaymentViewData;
import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class ClientViewController extends AbstractController implements ApplicationListener<UpdateDataEvent> {


    @FXML
    private GridPane root;

    @FXML
    private TableColumn<DateViewData, String> clientViewDateDateStartTableColumn;
    @FXML
    private TableColumn<DateViewData, String> clientViewDateDateEndTableColumn;
    @FXML
    private TableColumn<DateViewData, String> clientViewDateServiceTableColumn;
    @FXML
    private TableColumn<DateViewData, String> clientViewDateDogTableColumn;
    @FXML
    private TableColumn<DateViewData, String> clientViewDateDescriptionTableColumn;
    @FXML
    private TableColumn<DateViewData, String> clientViewDateActionsTableColumn;
    @FXML
    private TableView<DateViewData> clientViewDateTable;
    @FXML
    private Button addDateButton;

    @FXML
    private TableView<PaymentViewData> clientViewPaymentTable;
    @FXML
    private TableColumn<PaymentViewData, Number> clientViewPaymentAmountTableColumn;
    @FXML
    private TableColumn<PaymentViewData, String> clientViewPaymentServiceTableColumn;
    @FXML
    private TableColumn<PaymentViewData, String> clientViewPaymentDescriptionTableColumn;
    @FXML
    private TableColumn<PaymentViewData, String> clientViewPaymentDateTableColumn;
    @FXML
    private TableColumn<PaymentViewData, String> clientViewPaymentActionsTableColumn;
    @FXML
    private Button addPaymentButton;

    @FXML
    private TableView<DogViewData> clientViewDogTable;
    @FXML
    private TableColumn<DogViewData, String> clientViewDogRaceTableColumn;
    @FXML
    private TableColumn<DogViewData, String> clientViewDogNameTableColumn;
    @FXML
    private TableColumn<DogViewData, String> clientViewDogMaintainmentTableColumn;
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
    private TextField clientViewDni;
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
    private final SearchViewController searchViewController;

    private Scene scene;
    private Stage stage;
    private ClientViewData clientViewData;
    private DateViewController dateViewController;
    private PaymentViewController paymentViewController;


    public ClientViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context,
                                final DogViewController dogViewController, final SearchViewController searchViewController,
                                final DateViewController dateViewController, final PaymentViewController paymentViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
        this.dogViewController = dogViewController;
        this.searchViewController = searchViewController;
        this.dateViewController = dateViewController;
        this.paymentViewController = paymentViewController;
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

        createDogTableMenu();
        createPaymentTableMenu();
        createDateTableMenu();
        createAddDateButton();
        createAddPaymentButton();

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(getIcon());
        stage.onCloseRequestProperty().setValue(e -> {
            clientViewDataTab.getTabPane().getSelectionModel().selectFirst();
        });
    }

    private void createPaymentTableMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem deleteMenuItem = new MenuItem("Borrar pago");
        deleteMenuItem.setOnAction(event -> {
            final PaymentViewData paymentViewData = clientViewPaymentTable.getSelectionModel().getSelectedItem();
            dmsCommunicationFacadeImpl.deletePayment(paymentViewData);
            context.publishEvent(new DeleteDataEvent(stage, paymentViewData.getId(), DataType.PAYMENT));
        });
        contextMenu.getItems().add(deleteMenuItem);

        clientViewPaymentTable.setRowFactory(tf -> {
            final TableRow<PaymentViewData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void createDateTableMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem deleteMenuItem = new MenuItem("Borrar cita");
        deleteMenuItem.setOnAction(event -> {
            final DateViewData dateViewData = clientViewDateTable.getSelectionModel().getSelectedItem();
            dmsCommunicationFacadeImpl.deleteDate(dateViewData);
            context.publishEvent(new DeleteDataEvent(stage, dateViewData.getId(), DataType.DATE));
        });
        contextMenu.getItems().add(deleteMenuItem);

        clientViewDateTable.setRowFactory(tf -> {
            final TableRow<DateViewData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void createDogTableMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem deleteMenuItem = new MenuItem("Quitar mascota");
        deleteMenuItem.setOnAction(event -> {
            final DogViewData dogViewData = clientViewDogTable.getSelectionModel().getSelectedItem();
            dmsCommunicationFacadeImpl.deleteClientDog(clientViewData.getId(), dogViewData.getId());
            context.publishEvent(new DeleteDataEvent(stage, dogViewData.getId(), DataType.DOG));
        });
        contextMenu.getItems().add(deleteMenuItem);

        clientViewDogTable.setRowFactory(tf -> {
            final TableRow<DogViewData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    showDogView(clientViewDogTable.getSelectionModel().getSelectedItem().getId());
                }
            });
            return row;
        });
    }

    private void createAddDateButton() {
        addDateButton.setOnAction(event -> {
            showDateView();
        });
    }

    private void createAddPaymentButton() {
        addPaymentButton.setOnAction(event -> {
            showPaymentView();
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

        validator.createCheck()
                .dependsOn("clientViewDni", clientViewDni.textProperty())
                .withMethod(c -> {
                    final String dni = c.get("clientViewDni");
                    if (dni != null && !dni.isEmpty() && !dni.matches("^\\d{8}[a-zA-Z]$")) {
                        c.error("El dni debe ser correcto");
                    }
                })
                .decorates(clientViewDni)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long clientId) {
        chargeClientViewData(clientId);
        createSubmitButtonAction();
        createDeleteButtonAction();
        createAddDogButtonAction();

        if (stage.getOwner() == null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(source);
        }
        stage.show();
    }

    private void createAddDogButtonAction() {
        addDogButton.setOnAction(event -> {
            if (clientViewData.getId() != null) {
                final ButtonType newDog = new ButtonType("Crear mascota", ButtonBar.ButtonData.YES);
                final ButtonType searchDog = new ButtonType("Buscar mascota", ButtonBar.ButtonData.NO);
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "¿Quieres crear una nueva mascota o relacionar una existente?",
                        newDog, searchDog);
                alert.setHeaderText(null);
                alert.setTitle("Relacionar mascota");
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                        .add(getIcon());

                final Optional<ButtonType> action = alert.showAndWait();

                if (action.orElse(searchDog).equals(searchDog)) {
                    alert.close();
                    openSearchView();
                } else {
                    alert.close();
                    showDogView(null);
                }
            }
        });
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
                        .dni(clientViewDni.getText())
                        .dogs(clientViewData.getDogs())
                        .dates(clientViewData.getDates())
                        .payments(clientViewData.getPayments())
                        .build();

                context.publishEvent(new NewDataEvent(event.getSource(),
                        dmsCommunicationFacadeImpl.saveClient(clientViewData), DataType.CLIENT));
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
                        .add(getIcon());
                alert.setTitle("Borrar cliente");
                alert.setContentText("¿Estas seguro de que quieres borrar el cliente? " +
                        "Se borraran las citas del cliente, las mascotas que no tengan otro dueño y" +
                        " se eliminaran los datos del cliente de los pagos asociados.");

                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deleteClient(clientViewData.getId());
                    context.publishEvent(new DeleteDataEvent(event.getSource(), clientViewData.getId(), DataType.CLIENT));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }


    private void openSearchView() {
        final SearchTableRow searchTableRow = searchViewController.showView(stage, clientViewData);
        if (searchTableRow != null) {
            dmsCommunicationFacadeImpl.saveClientDog(clientViewData.getId(), Long.parseLong(searchTableRow.getIdString()));
            context.publishEvent(new NewDataEvent(stage, Long.parseLong(searchTableRow.getIdString()), DataType.DOG));
        }
    }

    private void bindDogTable() {
        clientViewDogNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientViewDogRaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRace()));
        clientViewDogMaintainmentTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaintainment()));
    }

    private void bindDateTable() {
        clientViewDateDateStartTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimestart())));
        clientViewDateDateEndTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimeend())));
        clientViewDateServiceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService() != null ?
                cellData.getValue().getService().getDescription() : null));
        clientViewDateDescriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        clientViewDateDogTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDog() != null ?
                cellData.getValue().getDog().getName() : null));
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
        clientViewDni.setText(clientViewData.getDni());
        clientViewPhone.setText(clientViewData.getPhone());
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
        stage.setHeight(320);
        stage.setWidth(root.getPrefWidth());

        clientViewName.clear();
        clientViewPhone.clear();
        clientViewObservations.clear();
        clientViewNextDate.clear();
        clientViewDni.clear();

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
    private void showDateView() {
        dateViewController.showView(stage, clientViewData.getId());
    }

    private void showPaymentView() {
        paymentViewController.showView(stage, null, clientViewData.getId());
    }

    @Override
    public void onApplicationEvent(final UpdateDataEvent event) {
        if (DataType.DOG.equals(event.getDataType()) ||
                DataType.DATE.equals(event.getDataType()) ||
                DataType.PAYMENT.equals(event.getDataType())) {
            clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientViewData.getId());

            if (clientViewData != null) {
                clientViewNextDate.setText(DmsUtils.dateToString(clientViewData.getNextDate()));
                clientViewDogTable.setItems(FXCollections.observableArrayList(clientViewData.getDogs()));
                clientViewDateTable.setItems(FXCollections.observableArrayList(clientViewData.getDates()));
                clientViewPaymentTable.setItems(FXCollections.observableArrayList(clientViewData.getPayments()));
            } else {
                clientViewData = ClientViewData.builder()
                        .dogs(new ArrayList<>()).dates(new ArrayList<>()).payments(new ArrayList<>()).build();
            }
        }
    }
}
