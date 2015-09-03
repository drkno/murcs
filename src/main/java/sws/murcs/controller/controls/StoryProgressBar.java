package sws.murcs.controller.controls;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import sws.murcs.model.Story;

public class StoryProgressBar extends GridPane {

    public StoryProgressBar() {
        getStylesheets().add(
                getClass().getResource("/sws/murcs/styles/materialDesign/completeness.css").toExternalForm());

        setAlignment(Pos.CENTER);
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setHgrow(Priority.SOMETIMES);
        firstColumn.setHalignment(HPos.CENTER);

        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setHgrow(Priority.SOMETIMES);
        secondColumn.setHalignment(HPos.CENTER);

        ColumnConstraints thirdColumn = new ColumnConstraints();
        thirdColumn.setHgrow(Priority.SOMETIMES);
        thirdColumn.setHalignment(HPos.CENTER);

        getColumnConstraints().addAll(firstColumn, secondColumn, thirdColumn);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(10);
        rowConstraints.setPrefHeight(30);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        getRowConstraints().add(rowConstraints);

        setPadding(new Insets(10));

        Pane completePane = new Pane();
        completePane.getStyleClass().add("completePane");
        setVgrow(completePane, Priority.ALWAYS);

        Pane progressPane = new Pane();
        progressPane.getStyleClass().add("progressPane");
        setVgrow(progressPane, Priority.ALWAYS);

        Pane notStartedPane = new Pane();
        notStartedPane.getStyleClass().add("notstartedPane");
        setVgrow(notStartedPane, Priority.ALWAYS);

        add(completePane, 0, 0);
        add(progressPane, 1, 0);
        add(notStartedPane, 2, 0);
    }

    public void setStory(Story story) {

    }
}
