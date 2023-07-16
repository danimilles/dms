package com.hairyworld.dms.controller;

import com.hairyworld.dms.model.view.SearchTableRow;
import com.hairyworld.dms.model.view.TableFilter;
import com.hairyworld.dms.rmi.DmsCommunicationFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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

    private List<SearchTableRow> columnValues;

    public SearchViewController(final DmsCommunicationFacade dmsCommunicationFacadeImpl) {
        this.dmsCommunicationFacadeImpl = dmsCommunicationFacadeImpl;
    }

    @FXML
    private void initialize() {scene = new Scene(root); }


    public SearchTableRow showView(final Stage source, final SearchTableRow data) {
        columnValues = dmsCommunicationFacadeImpl.getSearchTableData(data);

        stage = new Stage();
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream(ICON_IMAGE)));
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(source);
        stage.setTitle("Buscar");


        if (columnValues.stream().findFirst().isPresent()) {
            columnValues.stream().findFirst().get().getSearchColumns().entrySet().stream()
                    .filter(x -> !Objects.equals(x.getKey(), "Id"))
                    .forEach(entry -> {
                        final TableColumn<SearchTableRow, String> column = new TableColumn<>(entry.getKey());
                        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSearchColumns().get(entry.getKey())));
                        searchTable.getColumns().add(column);
                    });
        }

        searchTable.onMouseClickedProperty().set(event -> {
            if (event.getClickCount() == 2) {
                stage.close();
            }
        });

        stage.showAndWait();
        return searchTable.getSelectionModel().getSelectedItem();
    }

    private void onClose() {
        stage.onCloseRequestProperty().set(event -> {
            searchTable.getColumns().clear();
        });
    }
}
