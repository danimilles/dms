package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.DeleteDataEvent;
import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.event.UpdateDataEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class DogViewController extends AbstractController implements ApplicationListener<UpdateDataEvent> {
    private static final Logger LOGGER = LogManager.getLogger(DogViewController.class);

    private final SearchViewController searchViewController;

    @FXML
    private VBox imageVbox;


    private Image dogImage;
    @FXML
    private Circle imageCircle;
    @FXML
    private Button addImageButton;
    @FXML
    private Button deleteImageButton;


    @FXML
    private TableView<ClientViewData> dogViewClientTable;
    @FXML
    private TableColumn<ClientViewData, String> dogViewClientNameTableColumn;
    @FXML
    private TableColumn<ClientViewData, String> dogViewClientDniTableColumn;
    @FXML
    private TableColumn<ClientViewData, String> dogViewClientPhoneTableColumn;
    @FXML
    private TableView<DateViewData> dogViewDateTable;
    @FXML
    private TableColumn<DateViewData, String> dogViewDateDateStartTableColumn;
    @FXML
    private TableColumn<DateViewData, String> dogViewDateDateEndTableColumn;
    @FXML
    private TableColumn<DateViewData, String> dogViewDateClientNameTableColumn;
    @FXML
    private TableColumn<DateViewData, String> dogViewDateServiceTableColumn;
    @FXML
    private TableColumn<DateViewData, String> dogViewDateDescriptionTableColumn;
    @FXML
    private TextField dogViewName;
    @FXML
    private TextField dogViewRace;
    @FXML
    private TextArea dogViewObservations;
    @FXML
    private TextArea dogViewMaintainment;
    @FXML
    private TextField dogViewNextDate;
    @FXML
    private Button addClientToDogButton;
    @FXML
    private Label nextDateDogLabel;
    @FXML
    private Button submitDogButton;
    @FXML
    private Button deleteDogButton;
    @FXML
    private GridPane root;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;

    private Scene scene;
    private Stage stage;
    private DogViewData dogViewData;


    public DogViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context,
                             final SearchViewController searchViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
        this.searchViewController = searchViewController;
    }

    @FXML
    private void initialize() {

        dogViewData = DogViewData.builder()
                .clients(new ArrayList<>()).dates(new ArrayList<>()).build();

        createValidations();
        bindClientTable();
        bindDateTable();
        createClientContextMenu();
        createDateContextMenu();
        createTableResponsiveness(dogViewClientTable);
        createTableResponsiveness(dogViewDateTable);
        imageCircle.setOnMouseClicked(e -> showImagePopup());
        addClientToDogButton.setOnAction(e -> openSearchView());

        addImageButton.setOnAction(e -> showImageChooser());
        deleteImageButton.setOnAction(e -> deleteImage());
        dogViewNextDate.setDisable(true);

        scene = new Scene(root);
        stage = new Stage();
        stage.getIcons().add(getIcon());
        stage.setScene(scene);
    }

    private void createDateContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem deleteMenuItem = new MenuItem("Borrar cita");
        deleteMenuItem.setOnAction(event -> {
            final DateViewData dateViewData = dogViewDateTable.getSelectionModel().getSelectedItem();
            dmsCommunicationFacadeImpl.deleteDate(dateViewData);
            context.publishEvent(new DeleteDataEvent(stage, dateViewData.getId(), DataType.DATE));
        });
        contextMenu.getItems().add(deleteMenuItem);

        dogViewDateTable.setRowFactory(tf -> {
            final TableRow<DateViewData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void createClientContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem deleteMenuItem = new MenuItem("Quitar cliente");
        deleteMenuItem.setOnAction(event -> {
            final ClientViewData clientViewData = dogViewClientTable.getSelectionModel().getSelectedItem();
            dmsCommunicationFacadeImpl.deleteClientDog(clientViewData.getId(), dogViewData.getId());
            context.publishEvent(new NewDataEvent(stage, dogViewData.getId(), DataType.DOG));
        });
        contextMenu.getItems().add(deleteMenuItem);

        dogViewClientTable.setRowFactory(tf -> {
            final TableRow<ClientViewData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void openSearchView() {
        final SearchTableRow searchTableRow = searchViewController.showView(stage, dogViewData);
        if (searchTableRow != null) {
            dmsCommunicationFacadeImpl.saveClientDog(Long.parseLong(searchTableRow.getIdString()), dogViewData.getId());
            context.publishEvent(new NewDataEvent(stage, dogViewData.getId(), DataType.DOG));
        }
    }

    private Validator createValidations() {
        final Validator validator = new Validator();

        validator.createCheck()
                .dependsOn("dogViewName", dogViewName.textProperty())
                .withMethod(c -> {
                    final String name = c.get("dogViewName");
                    if (name == null || name.isEmpty()) {
                        c.error("El nombre no puede estar vacio");
                    }
                })
                .decorates(dogViewName)
                .immediate();

        validator.createCheck()
                .dependsOn("dogViewRace", dogViewRace.textProperty())
                .withMethod(c -> {
                    final String name = c.get("dogViewRace");
                    if (name == null || name.isEmpty()) {
                        c.error("La raza no puede estar vacia");
                    }
                })
                .decorates(dogViewRace)
                .immediate();

        validator.createCheck()
                .dependsOn("imageCircle", imageCircle.fillProperty())
                .withMethod(c -> {
                    if (dogViewData.getImage() != null && dogViewData.getImage().length > 4194304) {
                        c.error("La imagen no puede pesar mas de 4MB");
                    }
                })
                .decorates(imageCircle)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long clientId, final Long dogId) {
        chargeDogViewData(clientId, dogId);
        createSubmitButtonAction();
        createDeleteButtonAction();
        if (stage.getOwner() == null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(source);
        }
        stage.show();
    }

    private void chargeDogViewData(final Long clientId, final Long dogId) {
        if (dogId != null) {
            fill(dogId);
        } else {
            clean(clientId);
        }
    }

    private void createSubmitButtonAction() {
        submitDogButton.setOnAction(event -> {
            if (createValidations().validate()) {
                dogViewData = DogViewData.builder()
                        .id(dogViewData.getId())
                        .name(dogViewName.getText())
                        .race(dogViewRace.getText())
                        .observations(dogViewObservations.getText())
                        .maintainment(dogViewMaintainment.getText())
                        .clients(dogViewData.getClients())
                        .dates(dogViewData.getDates())
                        .image(dogViewData.getImage())
                        .build();

                dmsCommunicationFacadeImpl.saveDog(dogViewData);
                dogViewData.getClients().forEach(x -> dmsCommunicationFacadeImpl.saveClientDog(x.getId(), dogViewData.getId()));
                context.publishEvent(new NewDataEvent(event.getSource(), dogViewData.getId(), DataType.DOG));
                stage.close();
            }
        });
    }

    private void createDeleteButtonAction() {
        deleteDogButton.setOnAction(event -> {
            if (dogViewData.getId() != null) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                        .add(getIcon());
                alert.setTitle("Borrar mascota");
                alert.setContentText("¿Estas seguro de que quieres borrar la mascota? " +
                        "Se elimiran sus datos de todos los clientes y citas asociados a el.");

                final Optional<ButtonType> action = alert.showAndWait();

                if (ButtonType.OK.equals(action.orElse(null))) {
                    alert.close();
                    dmsCommunicationFacadeImpl.deleteDog(dogViewData.getId());
                    context.publishEvent(new DeleteDataEvent(event.getSource(), dogViewData.getId(), DataType.DOG));
                    stage.close();
                } else {
                    alert.close();
                }
            }
        });
    }

    private void showImagePopup() {
        if (dogViewData.getImage() != null) {
            final Stage popup = new Stage();
            final GridPane popupRoot = new GridPane();
            final Scene popupScene = new Scene(popupRoot, 400, 400);

            final ImageView popupImageView = new ImageView(dogViewData.getImage() != null ?
                    new Image(new ByteArrayInputStream(dogViewData.getImage())) : null);
            popupImageView.setPreserveRatio(true);
            popupImageView.setSmooth(true);
            popupRoot.setAlignment(Pos.CENTER);

            popup.widthProperty().addListener(
                    (observable, oldWidth, newWidth) -> popupImageView.setFitWidth(newWidth.doubleValue()));
            popup.heightProperty().addListener(
                    (observable, oldHeight, newHeight) -> popupImageView.setFitHeight(newHeight.doubleValue()));

            popupRoot.getChildren().add(popupImageView);
            popup.getIcons().add(getIcon());
            popup.setScene(popupScene);
            popup.setTitle("Ver imagen");
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initOwner(stage);
            popup.show();
        }
    }

    private void bindDateTable() {
        dogViewDateDateStartTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimestart())));
        dogViewDateDateEndTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DmsUtils.dateToString(cellData.getValue().getDatetimeend())));
        dogViewDateDateStartTableColumn.setComparator(Comparator.comparing(DmsUtils::parseDate, Comparator.nullsLast(Comparator.naturalOrder())));
        dogViewDateClientNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient() != null ? cellData.getValue().getClient().getName() : null));
        dogViewDateServiceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService() != null ?
                cellData.getValue().getService().getDescription() : null));
        dogViewDateDescriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void bindClientTable() {
        dogViewClientPhoneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        dogViewClientNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        dogViewClientDniTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
    }

    private void showImageChooser() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Selecciona una imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        final File selectedFile = fileChooser.showOpenDialog(this.stage);

        if (selectedFile != null && selectedFile.length() < 4194304) {
            try {
                dogViewData.setImage(Files.readAllBytes(selectedFile.toPath()));
                dogImage = new Image(new ByteArrayInputStream(dogViewData.getImage()));
                imageCircle.setFill(new ImagePattern(dogImage));
                deleteImageButton.setDisable(false);
                addImageButton.setDisable(true);
            } catch (final IOException e) {
                LOGGER.error("Error reading image", e);
            }

        } else if (selectedFile != null) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                    .add(getIcon());
            alert.setTitle("Error");
            alert.setContentText("La imagen no puede ser mayor de 4MB");
            alert.showAndWait();
        }
    }

    private void deleteImage() {
        dogViewData.setImage(null);
        imageCircle.setFill(null);
        dogImage = null;
        deleteImageButton.setDisable(true);
        addImageButton.setDisable(false);
    }

    private void fill(final Long dogId) {
        dogViewData = dmsCommunicationFacadeImpl.getDogViewData(dogId);

        stage.setTitle("Vista de mascota");
        stage.setHeight(root.getPrefHeight());
        stage.setWidth(root.getPrefWidth());

        dogViewName.setText(dogViewData.getName());

        if (dogViewData.getImage() != null) {
            dogImage = new Image(new ByteArrayInputStream(dogViewData.getImage()));
            imageCircle.setFill(new ImagePattern(dogImage));
            deleteImageButton.setDisable(false);
            addImageButton.setDisable(true);
        } else {
            imageCircle.setFill(null);
            deleteImageButton.setDisable(true);
            addImageButton.setDisable(false);
        }

        dogViewRace.setText(String.valueOf(dogViewData.getRace()));
        dogViewObservations.setText(dogViewData.getObservations());
        dogViewNextDate.setText(DmsUtils.dateToString(dogViewData.getNextDate()));
        dogViewMaintainment.setText(dogViewData.getMaintainment());

        nextDateDogLabel.setVisible(true);
        dogViewNextDate.setVisible(true);
        deleteDogButton.setDisable(false);
        addClientToDogButton.setDisable(false);
        dogViewClientTable.getParent().getParent().setVisible(true);

        dogViewClientTable.setItems(FXCollections.observableArrayList(dogViewData.getClients()));
        dogViewDateTable.setItems(FXCollections.observableArrayList(dogViewData.getDates()));
    }

    private void clean(final Long clientId) {
        final List<ClientViewData> clients = new ArrayList<>();

        if (clientId != null) {
            final ClientViewData clientViewData = dmsCommunicationFacadeImpl.getClientViewData(clientId);
            clients.add(clientViewData);
        }

        dogViewData = DogViewData.builder()
                .clients(clients).dates(new ArrayList<>()).build();

        stage.setTitle("Crear mascota");
        stage.setHeight(450);
        stage.setWidth(root.getPrefWidth());

        dogViewName.clear();
        dogViewRace.clear();
        dogImage = null;
        imageCircle.setFill(null);
        dogViewObservations.clear();
        dogViewNextDate.clear();
        dogViewMaintainment.clear();

        dogViewClientTable.getParent().getParent().setVisible(false);
        nextDateDogLabel.setVisible(false);
        dogViewNextDate.setVisible(false);
        deleteDogButton.setDisable(true);
        addClientToDogButton.setDisable(true);
        deleteImageButton.setDisable(true);
        addImageButton.setDisable(false);
    }

    @Override
    public void onApplicationEvent(final UpdateDataEvent event) {
        if (this.dogViewData != null
        && (event.getDataType().equals(DataType.DOG) && Objects.equals(event.getId(), dogViewData.getId())
        || (event.getDataType().equals(DataType.DATE)
                && dogViewData.getDates().stream().anyMatch(x -> x.getId().equals(event.getId()))))) {
            dogViewData = dmsCommunicationFacadeImpl.getDogViewData(dogViewData.getId());
            if (dogViewData != null) {
                dogViewClientTable.setItems(FXCollections.observableArrayList(dogViewData.getClients()));
                dogViewDateTable.setItems(FXCollections.observableArrayList(dogViewData.getDates()));
                dogViewNextDate.setText(DmsUtils.dateToString(dogViewData.getNextDate()));
            }
        }
    }
}
