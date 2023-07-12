package com.hairyworld.dms.controller;

import com.calendarfx.view.CalendarView;
import com.hairyworld.dms.model.event.NewEntityEvent;
import com.hairyworld.dms.model.view.ClientTableData;
import com.hairyworld.dms.model.view.TableFilter;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hairyworld.dms.util.Path.CLIENT_VIEW;

@Component
public class MainViewController implements ApplicationListener<NewEntityEvent> {

    private final DmsCommunicationFacade DMSCommunicationFacadeImpl;
    private final ClientViewController clientViewController;
    private final ApplicationContext context;

    private ObservableList<ClientTableData> clientTableData;

    @FXML
    private GridPane calendar;

    @FXML
    private Button addClientButton;

    @FXML
    private HBox searchHBox;
    @FXML
    private ChoiceBox<TableFilter> clientSearchField;
    @FXML
    private TextField clientSearchText;
    private DatePicker clientSearchDatePicker;

    @FXML
    private TableView<ClientTableData> clientTable;
    @FXML
    private TableColumn<ClientTableData, Number> clientIdColumn;
    @FXML
    private TableColumn<ClientTableData, String> clientNameColumn;
    @FXML
    private TableColumn<ClientTableData, Number> clientPhoneColumn;
    @FXML
    private TableColumn<ClientTableData, String> clientDogsColumn;
    @FXML
    private TableColumn<ClientTableData, String> clientMaintainmentColumn;
    @FXML
    private TableColumn<ClientTableData, String> clientNextDateColumn;

    private CalendarView calendarView;

    public MainViewController(final DmsCommunicationFacade DMSCommunicationFacadeImpl, final ClientViewController clientViewController,
                              final ApplicationContext context) {
        this.DMSCommunicationFacadeImpl = DMSCommunicationFacadeImpl;
        this.clientViewController = clientViewController;
        this.context = context;
    }

    @FXML
    private void initialize() {
        calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        calendarView.setRequestedTime(LocalTime.now());
        calendar.add(calendarView, 0, 0);

        scheduleUpdateCalendarTime();
        initClientTable();
        addClientButton.setOnAction(event -> showClientView(null));
    }

    private void initClientTable() {
        clientTableData = FXCollections.observableList(DMSCommunicationFacadeImpl.getClientTableData());
        clientIdColumn.setVisible(false);
        clientIdColumn.setCellValueFactory(cellData ->  new SimpleLongProperty(cellData.getValue().getId()));
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        clientDogsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDogs()));
        clientPhoneColumn.setCellValueFactory(cellData ->  new SimpleIntegerProperty(cellData.getValue().getPhone()));
        clientNextDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNextDate() != null ? DmsUtils.dateToString(cellData.getValue().getNextDate()) : null));
        clientNextDateColumn.setComparator(Comparator.comparing(DmsUtils::parseDate, Comparator.nullsLast(Comparator.naturalOrder())));
        clientMaintainmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMantainment()));

        clientTable.setItems(clientTableData);
        createTableControls();
    }

    private void createTableControls() {
        clientSearchDatePicker = new DatePicker();
        clientSearchDatePicker.setValue(LocalDate.now());

        clientSearchField.setItems(FXCollections.observableArrayList(TableFilter.NO_FILTER, TableFilter.MANTAINMENT,
                TableFilter.NEXT_DATE, TableFilter.PHONE, TableFilter.DOG_NAME, TableFilter.CLIENT_NAME));
        clientSearchField.setValue(TableFilter.NO_FILTER);
        clientSearchText.setDisable(true);

        createClientSearchFieldListener();
        createClientSearchTextListener();
        createClientSearchDatePickerListener();

        clientTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                showClientView(clientTable.getSelectionModel().getSelectedItem().getId());
            }
        });
    }

    private void showClientView(final Long selectedItem) {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getClassLoader().getResource(CLIENT_VIEW));
        loader.setControllerFactory(context::getBean);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientViewController.showView(selectedItem);
    }

    private void createClientSearchDatePickerListener() {
        clientSearchDatePicker.setOnAction(event -> {
            clientTable.setItems(clientTableData.filtered(clientTableData -> {
                    if (clientTableData.getNextDate() != null) {
                        return DmsUtils.dateToString(clientTableData.getNextDate().toLocalDate())
                                .contains(DmsUtils.dateToString(clientSearchDatePicker.getValue()));
                    } else {
                        return false;
                    }
                }));
            }
        );
    }

    private void createClientSearchTextListener() {
        clientSearchText.setOnKeyTyped(event -> {
            if (clientSearchField.getValue() != null) {
                switch (clientSearchField.getValue()) {
                    case CLIENT_NAME -> clientTable.setItems(clientTableData.filtered(clientTableData ->
                            clientTableData.getName().contains(clientSearchText.getText())));

                    case DOG_NAME -> clientTable.setItems(clientTableData.filtered(clientTableData ->
                            clientTableData.getDogs().contains(clientSearchText.getText())));

                    case PHONE -> clientTable.setItems(clientTableData.filtered(clientTableData ->
                            clientTableData.getPhone().toString().contains(clientSearchText.getText())));

                    case MANTAINMENT -> clientTable.setItems(clientTableData.filtered(clientTableData ->
                            clientTableData.getMantainment().contains(clientSearchText.getText())));

                    default -> clientTable.setItems(clientTableData);
                }
            }
        });
    }

    private void createClientSearchFieldListener() {
        clientSearchField.setOnAction(event -> {
            clientSearchText.setText(Strings.EMPTY);
            clientTable.setItems(FXCollections.observableList(clientTableData));

            if (clientSearchField.getValue().equals(TableFilter.NO_FILTER)) {
                clientSearchText.setDisable(true);

                searchHBox.getChildren().remove(clientSearchDatePicker);

                if (!searchHBox.getChildren().contains(clientSearchText)) {
                    searchHBox.getChildren().add(clientSearchText);
                }

            } else if (clientSearchField.getValue().equals(TableFilter.NEXT_DATE)) {
                searchHBox.getChildren().remove(clientSearchText);
                clientSearchDatePicker.setValue(LocalDate.now());
                searchHBox.getChildren().add(clientSearchDatePicker);

            } else {
                clientSearchText.setDisable(false);
                if (!searchHBox.getChildren().contains(clientSearchText)) {
                    searchHBox.getChildren().add(clientSearchText);
                }
                searchHBox.getChildren().remove(clientSearchDatePicker);
            }
        });
    }

    private void scheduleUpdateCalendarTime() {
        try (final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(() ->
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    }),0, 1, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void onApplicationEvent(final NewEntityEvent event) {
        clientTable.setItems(FXCollections.observableList(DMSCommunicationFacadeImpl.getClientTableData()));
    }
}
