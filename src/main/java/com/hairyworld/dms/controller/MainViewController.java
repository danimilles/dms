package com.hairyworld.dms.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.hairyworld.dms.model.event.UpdateEntityEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DateViewDataEntryWrapper;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.TableFilter;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.util.Strings;
import org.controlsfx.control.PopOver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MainViewController extends AbstractController implements ApplicationListener<UpdateEntityEvent> {

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ClientViewController clientViewController;
    private final ApplicationContext context;
    private final SearchViewController searchViewController;

    private ObservableList<ClientViewData> clientTableData;

    @FXML
    private GridPane calendarPane;

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
    private TableView<ClientViewData> clientTable;

    @FXML
    private TableColumn<ClientViewData, String> clientNameColumn;
    @FXML
    private TableColumn<ClientViewData, String> clientPhoneColumn;
    @FXML
    private TableColumn<ClientViewData, String> clientDogsColumn;
    @FXML
    private TableColumn<ClientViewData, String> clientMaintainmentColumn;
    @FXML
    private TableColumn<ClientViewData, String> clientNextDateColumn;
    @FXML
    private TableColumn<ClientViewData, String> clientDniColumn;

    @FXML
    private GridPane root;

    private CalendarView calendarView;
    private DateViewData dateViewData;
    private Calendar<DateViewData> calendar;

    public MainViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl,
                              final ClientViewController clientViewController,
                              final ApplicationContext context,
                              final SearchViewController searchViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.clientViewController = clientViewController;
        this.context = context;
        this.searchViewController = searchViewController;
    }

    @FXML
    private void initialize() {
        createCalendar();
        scheduleUpdateCalendarTime();
        initClientTable();
        createTableResponsiveness(clientTable);
        addClientButton.setOnAction(event -> showClientView(null));
    }

    private void initClientTable() {
        clientTableData = FXCollections.observableList(dmsCommunicationFacadeImpl.getClientTableData());
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        clientNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        clientDogsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDogsString()));
        clientDniColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        clientPhoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
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

        clientSearchField.setItems(FXCollections.observableArrayList(TableFilter.NO_FILTER,
                TableFilter.MANTAINMENT, TableFilter.DOG_NAME,
                TableFilter.DNI, TableFilter.CLIENT_NAME, TableFilter.PHONE, TableFilter.NEXT_DATE));
        clientSearchField.setValue(TableFilter.NO_FILTER);
        clientSearchText.setDisable(true);

        createClientSearchFieldListener();
        createClientSearchTextListener();
        createClientSearchDatePickerListener();


        clientTable.setRowFactory(tf -> {
            final TableRow<ClientViewData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    showClientView(clientTable.getSelectionModel().getSelectedItem().getId());
                }
            });
            return row;
        });
    }

    private void showClientView(final Long selectedItem) {
        clientViewController.showView((Stage) root.getScene().getWindow(), selectedItem);
    }

    private void createClientSearchDatePickerListener() {
        clientSearchDatePicker.setOnAction(event ->
                clientTable.setItems(clientTableData.filtered(clientData -> {
                    if (clientData.getNextDate() != null) {
                        return DmsUtils.dateToString(clientData.getNextDate().toLocalDate())
                                .contains(DmsUtils.dateToString(clientSearchDatePicker.getValue()));
                    } else {
                        return false;
                    }
                }))
        );
    }

    private void createClientSearchTextListener() {
        clientSearchText.setOnKeyTyped(event -> {
            if (clientSearchField.getValue() != null) {
                switch (clientSearchField.getValue()) {
                    case CLIENT_NAME -> clientTable.setItems(clientTableData.filtered(clientData ->
                            toLower(clientData.getName()).contains(toLower(clientSearchText.getText()))));

                    case DNI -> clientTable.setItems(clientTableData.filtered(clientData ->
                            toLower(clientData.getDni()).contains(toLower(clientSearchText.getText()))));

                    case DOG_NAME -> clientTable.setItems(clientTableData.filtered(clientData ->
                            toLower(clientData.getDogsString()).contains(toLower(clientSearchText.getText()))));

                    case PHONE -> clientTable.setItems(clientTableData.filtered(clientData ->
                            toLower(clientData.getPhone()).contains(toLower(clientSearchText.getText()))));

                    case MANTAINMENT -> clientTable.setItems(clientTableData.filtered(clientData ->
                            toLower(clientData.getMantainment()).contains(toLower(clientSearchText.getText()))));

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
                    }), 0, 1, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void onApplicationEvent(final UpdateEntityEvent event) {
        clientTableData = FXCollections.observableList(dmsCommunicationFacadeImpl.getClientTableData());
        clientTable.setItems(clientTableData);
    }

    // dates
    private void createCalendar() {
        calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        calendarView.setRequestedTime(LocalTime.now());
        calendarPane.add(calendarView, 0, 0);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSourceTrayButton(false);


        calendar = new Calendar<>("Calendario");
        calendar.addEntries(dmsCommunicationFacadeImpl.getDateCalendarData().stream()
                .map(this::mapDateViewDataToCalendarEntry).collect(Collectors.toList()));
        calendarView.setDefaultCalendarProvider(param -> calendar);

        final EventHandler<CalendarEvent> calendarE = param -> {
            if (param.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED) &&
                    ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getId().get() != null) {
                dmsCommunicationFacadeImpl.deleteDate(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).toDateViewData());
            } else {
                final Entry<DateViewDataEntryWrapper> entry = (Entry<DateViewDataEntryWrapper>) param.getEntry();
                if (entry.getUserObject().getInterval().get() != null) {
                    entry.getUserObject().getId().set(dmsCommunicationFacadeImpl.saveDate(entry.getUserObject().toDateViewData()));
                }
            }
        };

        calendar.addEventHandler(calendarE);


        final CalendarSource calendarSource = new CalendarSource("Citas");
        calendarSource.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(calendarSource);
        calendarView.setDefaultCalendarProvider(param -> calendarSource.getCalendars().get(0));
        calendarView.setEntryDetailsPopOverContentCallback(param -> createPopOver(param).getContentNode());
        calendarView.setEntryFactory(this::createEntryFactory);
    }


    private Entry<DateViewDataEntryWrapper> createEntryFactory(final DateControl.CreateEntryParameter param) {
        final Entry<DateViewDataEntryWrapper> entry = new Entry<>();
        final DateViewDataEntryWrapper dateViewDataEntryWrapper = new DateViewDataEntryWrapper();
        entry.setUserObject(dateViewDataEntryWrapper);
        entry.setInterval(param.getZonedDateTime());
        entry.intervalProperty().bindBidirectional(dateViewDataEntryWrapper.getInterval());
        dateViewDataEntryWrapper.getDescription().set("Nueva cita");
        entry.titleProperty().bindBidirectional(dateViewDataEntryWrapper.getDescription(), createTitleEntryConverter(dateViewDataEntryWrapper));
        return entry;
    }

    private Entry<DateViewDataEntryWrapper> mapDateViewDataToCalendarEntry(final DateViewData dateCalendarData) {
        final Entry<DateViewDataEntryWrapper> entry = new Entry<>();
        final DateViewDataEntryWrapper dateViewDataEntryWrapper = new DateViewDataEntryWrapper(dateCalendarData);
        entry.setUserObject(dateViewDataEntryWrapper);
        entry.intervalProperty().bindBidirectional(dateViewDataEntryWrapper.getInterval());
        entry.titleProperty().bindBidirectional(dateViewDataEntryWrapper.getDescription(), createTitleEntryConverter(dateViewDataEntryWrapper));
        return entry;
    }

    private PopOver createPopOver(final DateControl.EntryDetailsPopOverContentParameter param) {
        // Create a GridPane to hold the form elements
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        // Create labels and text fields for each data field
        Label startLabel = new Label("Start Date/Time:");
        TextField startTextField = new TextField();
        startTextField.setText(param.getEntry().getInterval().getStartTime().toString());
        startTextField.textProperty().bindBidirectional(param.getEntry().intervalProperty(), createStartDateStringConverter(param.getEntry().intervalProperty()));

        Label endLabel = new Label("End Date/Time:");
        TextField endTextField = new TextField();
        endTextField.textProperty().bindBidirectional(param.getEntry().intervalProperty(), createEndDateStringConverter(param.getEntry().intervalProperty()));

        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();
        descriptionTextField.textProperty().bindBidirectional(
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDescription());

        Label dogLabel = new Label("Dog:");
        ChoiceBox<DogViewData> dogTextField = new ChoiceBox<>();
        dogTextField.setMaxWidth(Double.MAX_VALUE);
        dogTextField.setConverter(new StringConverter<>() {
            @Override
            public String toString(final DogViewData object) {
                return object != null ? object.getName() : null;
            }

            @Override
            public DogViewData fromString(final String string) {
                return null;
            }
        });
        dogTextField.setOnMouseClicked(event -> {
            dogTextField.setValue((DogViewData) searchViewController.showView(gridPane.getScene().getWindow(), ClientViewData.builder().build()));
        });

        dogTextField.valueProperty().bindBidirectional(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDog());


        Label clientLabel = new Label("Client:");
        TextField clientTextField = new TextField();

        Label serviceLabel = new Label("Service:");
        TextField serviceTextField = new TextField();

        // Add the labels and text fields to the grid pane
        gridPane.add(startLabel, 0, 0);
        gridPane.add(startTextField, 1, 0);

        gridPane.add(endLabel, 0, 1);
        gridPane.add(endTextField, 1, 1);

        gridPane.add(descriptionLabel, 0, 2);
        gridPane.add(descriptionTextField, 1, 2);

        gridPane.add(dogLabel, 0, 3);
        gridPane.add(dogTextField, 1, 3);

        gridPane.add(clientLabel, 0, 4);
        gridPane.add(clientTextField, 1, 4);

        gridPane.add(serviceLabel, 0, 5);
        gridPane.add(serviceTextField, 1, 5);

        // Create a PopOver instance
        PopOver popOver = new PopOver(gridPane);
        popOver.setCloseButtonEnabled(true);
        popOver.setDetachable(false);
        popOver.setUserData(param.getEntry().getUserObject());
        return popOver;
    }

    private StringConverter<String> createTitleEntryConverter(final DateViewDataEntryWrapper dateViewDataEntryWrapper) {
        return new StringConverter<>() {
            @Override
            public String toString(final String object) {
                return dateViewDataEntryWrapper.getEntryTile();
            }

            @Override
            public String fromString(final String string) {
                return string;
            }
        };
    }

    private StringConverter<Interval> createEndDateStringConverter(ObjectProperty<Interval> intervalProperty) {
        return new StringConverter<>() {
            @Override
            public String toString(final Interval object) {
                return object.getEndDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            }

            @Override
            public Interval fromString(final String string) {
                return new Interval()
                        .withEndDateTime(LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                        .withStartDateTime(intervalProperty.get().getStartDateTime());
            }
        };
    }

    private StringConverter<Interval> createStartDateStringConverter(ObjectProperty<Interval> intervalProperty) {
        return new StringConverter<>() {
            @Override
            public String toString(final Interval object) {
                return object.getStartDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            }

            @Override
            public Interval fromString(final String string) {
                return new Interval()
                        .withEndDateTime(intervalProperty.get().getEndDateTime())
                        .withStartDateTime(LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            }
        };
    }
}
