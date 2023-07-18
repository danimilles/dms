package com.hairyworld.dms.controller;

import javafx.scene.control.TableView;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractController {

    protected void createTableResponsiveness(final TableView<?> table) {
        table.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            table.getColumns().forEach(column -> column.setPrefWidth(tableWidth * (0.99 / table.getColumns().size())));
        });
    }

    protected String toLower(final String string){
        return Strings.isEmpty(string) ? Strings.EMPTY : string.toLowerCase();
    }
}
