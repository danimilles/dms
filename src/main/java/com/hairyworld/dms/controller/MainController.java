package com.hairyworld.dms.controller;

import com.calendarfx.view.CalendarView;
import com.hairyworld.dms.model.view.ClientTableData;
import com.hairyworld.dms.service.ServerService;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MainController {

    @Autowired
    private ServerService serverServiceImpl;

    @FXML
    private GridPane calendar;

    @FXML
    private TableView clientTable;
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

    public MainController() {
    }

    @FXML
    private void initialize() {
        calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        calendarView.setRequestedTime(LocalTime.now());

        scheduleUpdateCalendarTime();

        populateClientTable();
        calendar.add(calendarView, 0, 0);
    }

    private void populateClientTable() {
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientDogsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDogs()));
        clientPhoneColumn.setCellValueFactory(cellData ->  new SimpleIntegerProperty(cellData.getValue().getPhone()));
        clientNextDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNextDate() != null ? cellData.getValue().getNextDate().toString() : null));
        clientMaintainmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMantainment()));

        clientTable.setItems(FXCollections.observableList(serverServiceImpl.getClientTableData()));
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
