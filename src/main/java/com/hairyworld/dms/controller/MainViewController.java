package com.hairyworld.dms.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.model.LoadEvent;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.hairyworld.dms.model.event.DeleteDataEvent;
import com.hairyworld.dms.model.event.NewDataEvent;
import com.hairyworld.dms.model.event.UpdateDataEvent;
import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DataType;
import com.hairyworld.dms.model.view.DateViewData;
import com.hairyworld.dms.model.view.DateViewDataEntryWrapper;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.ServiceViewData;
import com.hairyworld.dms.model.view.TableFilter;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import com.hairyworld.dms.util.DmsUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hairyworld.dms.util.DmsUtils.DATETIME_PATTERN;

@Component
public class MainViewController extends AbstractController implements ApplicationListener<UpdateDataEvent> {

    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;
    private final ClientViewController clientViewController;
    private final ApplicationContext context;
    private final SearchViewController searchViewController;
    private final ServiceViewController serviceViewController;
    private final PaymentViewController paymentViewController;

    @FXML
    private TableColumn<ServiceViewData, String> serviceTableServiceColumn;
    @FXML
    private TableView<ServiceViewData> serviceTable;

    private ObservableList<ClientViewData> clientTableData;
    private ObservableList<ServiceViewData> serviceTableData;

    @FXML
    private GridPane calendarPane;

    @FXML
    private Button addClientButton;
    @FXML
    private Button addServiceButton;
    @FXML
    private Button addPaymentButton;

    @FXML
    private HBox searchHBox;
    @FXML
    private ChoiceBox<TableFilter> clientSearchField;
    @FXML
    private ChoiceBox<TableFilter> serviceFilter;
    @FXML
    private TextField clientSearchText;
    @FXML
    private TextField serviceFilterText;
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
    private Calendar<DateViewData> calendar;

    public MainViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl,
                              final ClientViewController clientViewController,
                              final ApplicationContext context,
                              final SearchViewController searchViewController,
                              final ServiceViewController serviceViewController,
                              final PaymentViewController paymentViewController) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
        this.clientViewController = clientViewController;
        this.context = context;
        this.searchViewController = searchViewController;
        this.serviceViewController = serviceViewController;
        this.paymentViewController = paymentViewController;
    }

    @FXML
    private void initialize() {
        createCalendar();
        scheduleUpdateCalendarTime();
        initClientTable();
        initServiceTable();
        createTableResponsiveness(serviceTable);
        createTableResponsiveness(clientTable);
        addClientButton.setOnAction(event -> showClientView(null));
        addPaymentButton.setOnAction(event -> showPaymentView(null));
        addServiceButton.setOnAction(event -> showServiceView(null));
    }

    private void showPaymentView(final Long selectedId) {
        paymentViewController.showView((Stage) root.getScene().getWindow(), selectedId);
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

    private void initServiceTable() {
        serviceTableData = FXCollections.observableList(dmsCommunicationFacadeImpl.getServiceViewTableData());
        serviceTableServiceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        serviceTableServiceColumn.setSortType(TableColumn.SortType.ASCENDING);
        serviceTable.setItems(serviceTableData);
        createServiceTableControls();
    }

    private void createServiceTableControls() {
        serviceFilter.setItems(FXCollections.observableArrayList(TableFilter.NO_FILTER, TableFilter.SERVICE));
        serviceFilter.setValue(TableFilter.NO_FILTER);
        serviceFilterText.setDisable(true);

        createServiceSearchFieldListener();
        createServiceSearchTextListener();

        serviceTable.setRowFactory(tf -> {
            final TableRow<ServiceViewData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    showServiceView(serviceTable.getSelectionModel().getSelectedItem().getId());
                }
            });
            return row;
        });
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

    private void showServiceView(final Long selectedItem) {
        serviceViewController.showView((Stage) root.getScene().getWindow(), selectedItem);
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

    private void createServiceSearchTextListener() {
        serviceFilterText.setOnKeyTyped(event -> {
            if (serviceFilter.getValue() != null) {
                if (serviceFilter.getValue() == TableFilter.SERVICE) {
                    serviceTable.setItems(serviceTableData.filtered(serviceData ->
                            toLower(serviceData.getDescription()).contains(toLower(serviceFilterText.getText()))));
                } else {
                    clientTable.setItems(clientTableData);
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

    private void createServiceSearchFieldListener() {
        serviceFilter.setOnAction(event -> {
            serviceFilterText.setText(Strings.EMPTY);
            serviceTable.setItems(FXCollections.observableList(serviceTableData));

            serviceFilterText.setDisable(serviceFilter.getValue().equals(TableFilter.NO_FILTER));
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
    public void onApplicationEvent(final UpdateDataEvent event) {
        if (event.getDataType().equals(DataType.CLIENT) ||
                event.getDataType().equals(DataType.DOG) ||
                event.getDataType().equals(DataType.DATE)) {
            clientTableData = FXCollections.observableList(dmsCommunicationFacadeImpl.getClientTableData());
            clientTable.setItems(clientTableData);
        }
        if (event.getDataType().equals(DataType.SERVICE)) {
            serviceTableData = FXCollections.observableList(dmsCommunicationFacadeImpl.getServiceViewTableData());
            serviceTable.setItems(serviceTableData);
        }
        if (event instanceof DeleteDataEvent) {
            if (DataType.SERVICE.equals(event.getDataType()) ||
                    DataType.DOG.equals(event.getDataType())) {
                calendar.findEntries("").stream().filter(entry ->
                                ((DateViewDataEntryWrapper) entry.getUserObject())
                                        .toDateViewData().isRelatedTo(event.getId(), event.getDataType()))
                        .forEach(entry -> {
                            if (DataType.DOG.equals(event.getDataType())) {
                                ((DateViewDataEntryWrapper) entry.getUserObject()).getDog().set(null);
                            } else {
                                ((DateViewDataEntryWrapper) entry.getUserObject()).getService().set(null);
                            }
                            entry.setTitle(((DateViewDataEntryWrapper) entry.getUserObject()).getEntryTile());
                        });
            } else {
                calendar.findEntries("").stream().filter(entry ->
                                ((DateViewDataEntryWrapper) entry.getUserObject())
                                        .toDateViewData().isRelatedTo(event.getId(), event.getDataType()))
                        .forEach(entry -> calendar.removeEntry(entry));
            }
        }
    }

    private void createCalendar() {
        calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        calendarView.setRequestedTime(LocalTime.now());
        calendarPane.add(calendarView, 0, 0);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSourceTrayButton(false);
        calendarView.setShowPageToolBarControls(false);
        calendarView.setShowPrintButton(false);

        calendar = new Calendar<>("Calendario");
        calendar.addEntries(dmsCommunicationFacadeImpl.getDateCalendarData().stream()
                .map(this::mapDateViewDataToCalendarEntry).collect(Collectors.toList()));
        calendar.addEventHandler(getCalendarEventHandler());

        final CalendarSource calendarSource = new CalendarSource("Citas");
        calendarSource.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(calendarSource);
        calendarView.addEventHandler(LoadEvent.LOAD, evt -> evt.getCalendarSources().removeIf(x->!x.equals(calendarSource)));

        calendarView.setDefaultCalendarProvider(param -> calendar);
        calendarView.setDefaultCalendarProvider(param -> calendarSource.getCalendars().get(0));
        calendarView.setEntryDetailsPopOverContentCallback(this::createPopOverContent);
        calendarView.setEntryFactory(this::createEntryFactory);

    }

    private EventHandler<CalendarEvent> getCalendarEventHandler() {
        return param -> {
            final Entry<DateViewDataEntryWrapper> entry = (Entry<DateViewDataEntryWrapper>) param.getEntry();
            if (param.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED) &&
                    ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getId().get() != null) {
                dmsCommunicationFacadeImpl.deleteDate(
                        ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).toDateViewData());
                context.publishEvent(
                        new DeleteDataEvent(param.getSource(), entry.getUserObject().getId().get(), DataType.DATE));
            } else {
                if (entry.getUserObject().getInterval().get() != null) {
                    entry.getUserObject().getId().set(
                            dmsCommunicationFacadeImpl.saveDate(entry.getUserObject().toDateViewData()));
                    context.publishEvent(
                            new NewDataEvent(param.getSource(), entry.getUserObject().getId().get(), DataType.DATE));
                }
            }
        };
    }

    private Entry<DateViewDataEntryWrapper> createEntryFactory(final DateControl.CreateEntryParameter param) {
        final Entry<DateViewDataEntryWrapper> entry = new Entry<>();
        final DateViewDataEntryWrapper dateViewDataEntryWrapper = new DateViewDataEntryWrapper();
        entry.setInterval(param.getZonedDateTime());
        dateViewDataEntryWrapper.getDescription().set("Nueva cita");
        return getDateViewDataEntryWrapperEntry(entry, dateViewDataEntryWrapper);
    }

    private Entry<DateViewDataEntryWrapper> mapDateViewDataToCalendarEntry(final DateViewData dateCalendarData) {
        final Entry<DateViewDataEntryWrapper> entry = new Entry<>();
        final DateViewDataEntryWrapper dateViewDataEntryWrapper = new DateViewDataEntryWrapper(dateCalendarData);
        return getDateViewDataEntryWrapperEntry(entry, dateViewDataEntryWrapper);
    }

    private Entry<DateViewDataEntryWrapper> getDateViewDataEntryWrapperEntry(final Entry<DateViewDataEntryWrapper> entry,
                                                                             final DateViewDataEntryWrapper dateViewDataEntryWrapper) {
        entry.setUserObject(dateViewDataEntryWrapper);
        entry.intervalProperty().bindBidirectional(dateViewDataEntryWrapper.getInterval());
        entry.titleProperty().bindBidirectional(new SimpleStringProperty(dateViewDataEntryWrapper.getEntryTile()));

        dateViewDataEntryWrapper.getDescription().addListener((observable, oldValue, newValue) ->
            entry.setTitle(entry.getUserObject().getEntryTile())
        );
        dateViewDataEntryWrapper.getClient().addListener(saveEntryChange(entry));
        dateViewDataEntryWrapper.getDog().addListener(saveEntryChange(entry));
        dateViewDataEntryWrapper.getService().addListener(saveEntryChange(entry));
        return entry;
    }

    private ChangeListener saveEntryChange(final Entry<DateViewDataEntryWrapper> entry) {
        return (observable, oldValue, newValue) -> {
            dmsCommunicationFacadeImpl.saveDate((entry.getUserObject()).toDateViewData());
            entry.setTitle(entry.getUserObject().getEntryTile());
            context.publishEvent(new NewDataEvent(root, entry.getUserObject().getId().get(), DataType.DATE));
        };

    }

    private Node createPopOverContent(final DateControl.EntryDetailsPopOverContentParameter param) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        final Label startLabel = new Label("Fecha inicio:");
        final TextField startTextField = new TextField();
        startTextField.setText(param.getEntry().getInterval().getStartTime().toString());
        startTextField.textProperty().bindBidirectional(param.getEntry().intervalProperty(),
                createStartDateStringConverter(param.getEntry().intervalProperty()));

        final Label endLabel = new Label("Fecha fin:");
        final TextField endTextField = new TextField();
        endTextField.textProperty().bindBidirectional(param.getEntry().intervalProperty(),
                createEndDateStringConverter(param.getEntry().intervalProperty()));

        final Label descriptionLabel = new Label("Descripcion:");
        final TextField descriptionTextField = new TextField();
        descriptionTextField.textProperty().bindBidirectional(
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDescription());

        final Label dogLabel = new Label("Mascota:");
        final ChoiceBox<DogViewData> dogTextField = new ChoiceBox<>();
        createDogTextField(param, descriptionTextField, dogTextField);

        final Label clientLabel = new Label("Cliente:");
        final Button clientTextField = new Button();
        createSearchClientButton(param, clientTextField);

        final Label serviceLabel = new Label("Servicio:");
        final ChoiceBox<ServiceViewData> serviceTextField = new ChoiceBox<>();
        createServiceTextField(param, descriptionTextField, serviceTextField);

        serviceTextField.onActionProperty().setValue(event ->
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getService().set(serviceTextField.getValue()));
        // Add the labels and text fields to the grid pane
        gridPane.add(startLabel, 0, 0);
        gridPane.add(startTextField, 1, 0);

        gridPane.add(endLabel, 0, 1);
        gridPane.add(endTextField, 1, 1);

        gridPane.add(descriptionLabel, 0, 2);
        gridPane.add(descriptionTextField, 1, 2);

        gridPane.add(dogLabel, 0, 4);
        gridPane.add(dogTextField, 1, 4);

        gridPane.add(clientLabel, 0, 3);
        gridPane.add(clientTextField, 1, 3);

        gridPane.add(serviceLabel, 0, 5);
        gridPane.add(serviceTextField, 1, 5);

        return gridPane;
    }

    private void createServiceTextField(DateControl.EntryDetailsPopOverContentParameter param, TextField descriptionTextField, ChoiceBox<ServiceViewData> serviceTextField) {
        descriptionTextField.widthProperty().addListener((observable, oldValue, newValue) ->
                serviceTextField.setPrefWidth(newValue.doubleValue()));
        serviceTextField.setConverter(createServiceTextFieldStringConverter());
        serviceTextField.setValue(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getService().get());

        final List<ServiceViewData> list = new ArrayList<>();
        list.add(null);
        list.addAll(dmsCommunicationFacadeImpl.getServiceViewTableData());
        serviceTextField.setItems(FXCollections.observableList(list));
    }

    private void createSearchClientButton(DateControl.EntryDetailsPopOverContentParameter param, Button clientTextField) {
        clientTextField.setMaxWidth(Double.MAX_VALUE);
        clientTextField.setText(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get() != null ?
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get().getName() : null);

        clientTextField.setOnMouseClicked(event -> {
            ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().set(
                    (ClientViewData) searchViewController.showView(null, DogViewData.builder().build()));
            clientTextField.setText(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get() != null ?
                    ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get().getName() : null);
            param.getPopOver().show(param.getPopOver().getOwnerWindow());
        });
    }

    private static StringConverter<ServiceViewData> createServiceTextFieldStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(final ServiceViewData object) {
                return object != null ? object.getDescription() : null;
            }

            @Override
            public ServiceViewData fromString(final String string) {
                return null;
            }
        };
    }

    private void createDogTextField(final DateControl.EntryDetailsPopOverContentParameter param,
                                    final TextField descriptionTextField, final ChoiceBox<DogViewData> dogTextField) {
        dogTextField.setDisable(true);
        descriptionTextField.widthProperty().addListener((observable, oldValue, newValue) ->
                dogTextField.setPrefWidth(newValue.doubleValue()));
        dogTextField.setConverter(getDogTextFieldStringConverter());
        dogTextField.setValue(((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDog().get());
        setDogsChoicesValues(param, dogTextField);

        dogTextField.onActionProperty().setValue(event ->
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDog().set(dogTextField.getValue()));

        ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient()
                .addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getDog().set(null);
                dogTextField.setValue(null);
            }
            setDogsChoicesValues(param, dogTextField);
            dogTextField.setDisable(newValue == null);
        });

    }

    private void setDogsChoicesValues(DateControl.EntryDetailsPopOverContentParameter param, ChoiceBox<DogViewData> dogTextField) {
        if ((((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get() != null)){
            final List<DogViewData> list = new ArrayList<>();
            list.add(null);
            list.addAll(dmsCommunicationFacadeImpl.getClientViewData(
                    ((DateViewDataEntryWrapper) param.getEntry().getUserObject()).getClient().get().getId()).getDogs());

            dogTextField.setItems(FXCollections.observableList(list).sorted(Comparator.comparing(DogViewData::getName)));
            dogTextField.setDisable(false);
        }
    }

    private static StringConverter<DogViewData> getDogTextFieldStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(final DogViewData object) {
                return object != null ? object.getName() + " -> " + object.getRace() : null;
            }

            @Override
            public DogViewData fromString(final String string) {
                return null;
            }
        };
    }


    private StringConverter<Interval> createEndDateStringConverter(ObjectProperty<Interval> intervalProperty) {
        return new StringConverter<>() {
            @Override
            public String toString(final Interval object) {
                return object.getEndDateTime().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            }

            @Override
            public Interval fromString(final String string) {
                try {
                    return new Interval()
                            .withEndDateTime(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(DATETIME_PATTERN)))
                            .withStartDateTime(intervalProperty.get().getStartDateTime());
                } catch (DateTimeParseException e) {
                    return new Interval()
                            .withEndDateTime(intervalProperty.get().getEndDateTime())
                            .withStartDateTime(intervalProperty.get().getStartDateTime());
                }
            }
        };
    }

    private StringConverter<Interval> createStartDateStringConverter(ObjectProperty<Interval> intervalProperty) {
        return new StringConverter<>() {
            @Override
            public String toString(final Interval object) {
                return object.getStartDateTime().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            }

            @Override
            public Interval fromString(final String string) {
            try {
                return new Interval()
                        .withEndDateTime(intervalProperty.get().getEndDateTime())
                        .withStartDateTime(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
            } catch (DateTimeParseException e) {
                return new Interval()
                        .withEndDateTime(intervalProperty.get().getEndDateTime())
                        .withStartDateTime(intervalProperty.get().getStartDateTime());
            }
            }
        };
    }
}
