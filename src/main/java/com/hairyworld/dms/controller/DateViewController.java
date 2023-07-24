package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.ServiceViewData;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DateViewController extends AbstractController {

    public static final String DATE_REGEXP = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}(:\\d{2})?$";
    public static final String START_DATE_TEXT_FIELD = "startDateTextField";
    public static final String END_DATE_TEXT_FIELD = "endDateTextField";
    @FXML
    private GridPane root;

    @FXML
    private Button searchClientButton;
    @FXML
    private ChoiceBox<ServiceViewData> serviceTextField;

    @FXML
    private TextField endDateTextField;
    @FXML
    private TextField descriptionDateTextField;
    @FXML
    private ChoiceBox<DogViewData> dogTextField;
    @FXML
    private TextField startDateTextField;

    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ApplicationContext context;
    private final SearchViewController searchViewController;

    private Scene scene;
    private Stage stage;
    private DateViewData dateViewData;


    public DateViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl, final ApplicationContext context,
                              final SearchViewController searchViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.context = context;
        this.searchViewController = searchViewController;
    }

    @FXML
    private void initialize() {
        dateViewData = DateViewData.builder().build();

        createValidations();

        scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(getIcon());
    }

    private Validator createValidations() {
        final Validator validator = new Validator();

        validator.createCheck()
                .dependsOn(START_DATE_TEXT_FIELD, startDateTextField.textProperty())
                .withMethod(c -> {
                    final String value = c.get(START_DATE_TEXT_FIELD);
                    if (value == null || value.isEmpty() || DmsUtils.parseDate(value) == null ||
                                    !value.matches(DATE_REGEXP)) {
                            c.error("La fecha debe estar en formato dd/MM/yyyy HH:mm (Ej: 01/01/2020 12:00)");
                    }
                })
                .decorates(startDateTextField)
                .immediate();

        validator.createCheck()
                .dependsOn(START_DATE_TEXT_FIELD, startDateTextField.textProperty())
                .dependsOn(END_DATE_TEXT_FIELD, endDateTextField.textProperty())
                .withMethod(c -> {
                    final String startDate = c.get(START_DATE_TEXT_FIELD);
                    final String endDate = c.get(END_DATE_TEXT_FIELD);

                    if (endDate == null || endDate.isEmpty() || DmsUtils.parseDate(endDate) == null ||
                            !endDate.matches(DATE_REGEXP)) {
                            c.error("La fecha debe estar en formato dd/MM/yyyy HH:mm (Ej: 01/01/2020 12:00)");

                    } else if (startDate != null && !startDate.isEmpty() && DmsUtils.parseDate(startDate) != null &&
                                    startDate.matches(DATE_REGEXP)
                    && DmsUtils.parseDate(endDate).isBefore(DmsUtils.parseDate(startDateTextField.getText()))) {
                        c.error("La fecha de fin debe ser posterior a la de inicio");
                    }
                })
                .decorates(endDateTextField)
                .immediate();

        return validator;
    }

    public void showView(final Stage source, final Long clientId) {
        chargeDateViewData(clientId);
        createSubmitButtonAction();
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
                return object != null ? object.getDescription() : null;
            }

            @Override
            public ServiceViewData fromString(String string) {
                return null;
            }
        });
        serviceTextField.getItems().addAll(serviceViewData);
        serviceTextField.setValue(dateViewData.getService());
    }

    private void createDogTextField() {
        dogTextField.setDisable(true);
        descriptionDateTextField.widthProperty().addListener((observable, oldValue, newValue) ->
                dogTextField.setPrefWidth(newValue.doubleValue()));
        dogTextField.setConverter(new StringConverter<>() {
            @Override
            public String toString(final DogViewData object) {
                return object != null ? object.getName() + " -> " + object.getRace() : null;
            }

            @Override
            public DogViewData fromString(String string) {
                return null;
            }});

        setDogsChoicesValues(dogTextField);
    }

    private void setDogsChoicesValues(ChoiceBox<DogViewData> dogTextField) {
        if (dateViewData.getClient() != null) {
            final List<DogViewData> list = new ArrayList<>();
            list.add(null);
            list.addAll(dateViewData.getClient().getDogs());

            dogTextField.setItems(FXCollections.observableList(list).sorted(Comparator.nullsFirst
                    (Comparator.comparing(DogViewData::getName))));
            dogTextField.setDisable(false);
        }
    }

    private void createSearchClientButtonAction() {
        searchClientButton.setOnAction(event -> {
                dateViewData.setClient(
                        (ClientViewData) searchViewController.showView(stage, DogViewData.builder().build()));
                if (dateViewData.getClient() != null) {
                    searchClientButton.setText(dateViewData.getClient().getName());
                    dogTextField.setDisable(false);
                    setDogsChoicesValues(dogTextField);
                } else {
                    searchClientButton.setText(null);
                    dogTextField.setDisable(true);
                }
        });
    }

    private void chargeDateViewData(final Long clientId) {
        clean(clientId);
    }

    private void createSubmitButtonAction() {
        submitButton.setOnAction(event -> {
            if (createValidations().validate()) {
                dateViewData = DateViewData.builder()
                        .id(dateViewData.getId())
                        .dog(dogTextField.getValue())
                        .client(dateViewData.getClient())
                        .service(serviceTextField.getValue())
                        .datetimestart(DmsUtils.parseDate(startDateTextField.getText()))
                        .datetimeend(DmsUtils.parseDate(endDateTextField.getText()))
                        .description(descriptionDateTextField.getText())
                        .build();

                context.publishEvent(new NewDataEvent(new Button("dummy"),
                        dmsCommunicationFacadeImpl.saveDate(dateViewData), DataType.DATE));
                stage.close();
            }
        });
    }

    private void clean(final Long clientId) {
        dateViewData = DateViewData.builder().build();

        dateViewData.setClient(dmsCommunicationFacadeImpl.getClientViewData(clientId));
        if (dateViewData.getClient() != null) {
            searchClientButton.setText(dateViewData.getClient().getName());
            dogTextField.setDisable(false);
            searchClientButton.setDisable(true);
        } else {
            searchClientButton.setText(null);
            dogTextField.setDisable(true);
        }
        createDogTextField();

        dogTextField.setValue(null);
        serviceTextField.setValue(null);
        descriptionDateTextField.clear();
        startDateTextField.clear();
        endDateTextField.clear();

        stage.setTitle("Crear cita");
    }
}
