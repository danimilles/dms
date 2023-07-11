package com.hairyworld.dms.controller;

import com.calendarfx.view.CalendarView;
import com.hairyworld.dms.model.mapper.TableFilter;
import com.hairyworld.dms.model.view.ClientTableData;
import com.hairyworld.dms.service.ServerService;
import com.hairyworld.dms.util.DMSUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MainController {

    private final ServerService serverServiceImpl;

    private ObservableList<ClientTableData> clientTableData;

    @FXML
    private GridPane calendar;

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

    public MainController(final ServerService serverServiceImpl) {
        this.serverServiceImpl = serverServiceImpl;
    }

    @FXML
    private void initialize() {
        calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        calendarView.setRequestedTime(LocalTime.now());
        calendar.add(calendarView, 0, 0);

        scheduleUpdateCalendarTime();
        initClientTable();
    }

    private void initClientTable() {
        clientTableData = FXCollections.observableList(serverServiceImpl.getClientTableData());
        clientIdColumn.setVisible(false);
        clientIdColumn.setCellValueFactory(cellData ->  new SimpleLongProperty(cellData.getValue().getId()));
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        clientDogsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDogs()));
        clientPhoneColumn.setCellValueFactory(cellData ->  new SimpleIntegerProperty(cellData.getValue().getPhone()));
        clientNextDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNextDate() != null ? DMSUtils.dateToString(cellData.getValue().getNextDate()) : null));
        clientNextDateColumn.setComparator(Comparator.comparing(DMSUtils::parseDate, Comparator.nullsLast(Comparator.naturalOrder())));
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
    }

    private void createClientSearchDatePickerListener() {
        clientSearchDatePicker.setOnAction(event -> {
            clientTable.setItems(clientTableData.filtered(clientTableData -> {
                    if (clientTableData.getNextDate() != null) {
                        return DMSUtils.dateToString(clientTableData.getNextDate().toLocalDate())
                                .contains(DMSUtils.dateToString(clientSearchDatePicker.getValue()));
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

}
