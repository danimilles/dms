package com.hairyworld.dms.controller;

import javafx.scene.control.TableView;

public abstract class AbstractController {

    protected void createTableResponsiveness(final TableView<?> table) {
        table.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            table.getColumns().forEach(column -> column.setPrefWidth(tableWidth * (0.99 / (table.getColumns().size()))));
        });
    }
}
