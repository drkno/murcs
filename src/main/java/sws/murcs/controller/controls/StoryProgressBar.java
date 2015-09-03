package sws.murcs.controller.controls;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

public class StoryProgressBar extends GridPane {

    Pane completePane, progressPane, notStartedPane;

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

        completePane = new Pane();
        completePane.getStyleClass().add("completePane");
        setVgrow(completePane, Priority.ALWAYS);

        progressPane = new Pane();
        progressPane.getStyleClass().add("progressPane");
        setVgrow(progressPane, Priority.ALWAYS);

        notStartedPane = new Pane();
        notStartedPane.getStyleClass().add("notstartedPane");
        setVgrow(notStartedPane, Priority.ALWAYS);

        add(completePane, 0, 0);
        add(progressPane, 1, 0);
        add(notStartedPane, 2, 0);
    }

    public final void setStory(final Story story) {
        float done = 0, inProgress = 0, notStarted = 0;

        for (Task task : story.getTasks()) {
            switch (task.getState()) {
                case Done:
                    done += task.getCurrentEstimate();
                    break;
                case InProgress:
                    inProgress += task.getCurrentEstimate();
                    break;
                case NotStarted:
                    notStarted += task.getCurrentEstimate();
                    break;
                default:
                    break;
            }
        }

        float total = done + inProgress + notStarted;
        double completedWidth = (done / total) * getWidth();
        double inProgressWidth = (inProgress / total) * getWidth();
        double notStartedWidth = (notStarted / total) * getWidth();

        getColumnConstraints().get(0).setPrefWidth(completedWidth);
        getColumnConstraints().get(1).setPrefWidth(inProgressWidth);
        getColumnConstraints().get(2).setPrefWidth(notStartedWidth);
    }
}
