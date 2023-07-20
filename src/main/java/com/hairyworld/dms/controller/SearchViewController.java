package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.model.view.TableFilter;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.hairyworld.dms.util.Path.ICON_IMAGE;

@Component
public class SearchViewController extends AbstractController {
    private final DmsCommunicationFacade dmsCommunicationFacadeImpl;

    private Scene scene;
    private Stage stage;

    @FXML
    private GridPane root;
    @FXML
    private HBox searchHBox;
    @FXML
    private ChoiceBox<TableFilter> searchField;
    @FXML
    private TextField searchText;

    @FXML
    private TableView<SearchTableRow> searchTable;

    private ObservableList<SearchTableRow> columnValues;
    private SearchTableRow selectedRow;

    public SearchViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
    }

    @FXML
    private void initialize() {
        scene = new Scene(root);
        createTableResponsiveness(searchTable);
        createClientSearchTextListener();
        createClientSearchFieldListener();
        searchTable.setPrefWidth(400);
        searchTable.setRowFactory(tv -> {
            final TableRow<SearchTableRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    selectedRow = row.getItem();
                    stage.close();
                    searchTable.getColumns().clear();
                }
            });
            return row ;
        });

    }

    public SearchTableRow showView(final Window source, final SearchTableRow data) {
        columnValues = FXCollections.observableArrayList(dmsCommunicationFacadeImpl.getSearchTableData(data));

        stage = new Stage();
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream(ICON_IMAGE)));
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(source);
        stage.setTitle("Buscar");


        final Optional<SearchTableRow> optional = columnValues.stream().findFirst();
        optional.ifPresent(searchTableRow -> {
            searchTableRow.getSearchColumns().entrySet().stream()
                .filter(x -> !Objects.equals(x.getKey(), "Id"))
                .forEach(entry -> {
                    final TableColumn<SearchTableRow, String> column = new TableColumn<>(entry.getKey());
                    column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSearchColumns().get(entry.getKey())));
                    column.setPrefWidth(searchTable.getWidth() * (0.99 / (searchTableRow.getSearchColumns().size() - 1)));
                    searchTable.getColumns().add(column);
                });
            searchField.setItems(FXCollections.observableArrayList(optional.get().getSearchFilters()));
            searchField.getItems().add(TableFilter.NO_FILTER);
            searchField.setValue(TableFilter.NO_FILTER);
        });

        searchTable.setItems(columnValues);

        onClose();
        stage.showAndWait();
        final SearchTableRow row = selectedRow;
        selectedRow = null;
        return row;
    }

    private void onClose() {
        stage.onCloseRequestProperty().set(event -> {
            searchTable.getColumns().clear();
        });
    }

    private void createClientSearchFieldListener() {
        searchField.setItems(FXCollections.observableArrayList(TableFilter.NO_FILTER));
        searchField.setValue(TableFilter.NO_FILTER);
        searchText.setDisable(true);

        searchField.setOnAction(event -> {
            if (searchField.getValue() != null) {
                searchText.setText(Strings.EMPTY);
                searchTable.setItems(FXCollections.observableList(columnValues));

                searchText.setDisable(searchField.getValue().equals(TableFilter.NO_FILTER));
            }
        });
    }

    private void createClientSearchTextListener() {
        searchText.setOnKeyTyped(event -> {
            if (searchField.getValue() != null) {
                switch (searchField.getValue()) {
                    case CLIENT_NAME, DOG_NAME -> searchTable.setItems(columnValues.filtered(clientData ->
                            toLower(clientData.getName()).contains(toLower(searchText.getText()))));
                    case DNI -> searchTable.setItems(columnValues.filtered(clientData ->
                            toLower(((ClientViewData) clientData).getDni()).contains(toLower(searchText.getText()))));
                    case PHONE -> searchTable.setItems(columnValues.filtered(clientData ->
                            toLower(((ClientViewData) clientData).getPhone()).contains(toLower(searchText.getText()))));

                    case OBSERVATIONS -> searchTable.setItems(columnValues.filtered(dogData ->
                            toLower(((DogViewData) dogData).getObservations()).contains(toLower(searchText.getText()))));
                    case RACE -> searchTable.setItems(columnValues.filtered(dogData ->
                            toLower(((DogViewData) dogData).getRace()).contains(toLower(searchText.getText()))));

                    default -> searchTable.setItems(columnValues);
                }
            }
        });
    }
}
