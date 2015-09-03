package sws.murcs.controller.controls;

import com.sun.javafx.css.StyleManager;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

/**
 * A progress bar control for stories.
 */
public class ModelProgressBar extends GridPane {

    /**
     * The panes that contain progress.
     */
    private Pane completePane, progressPane, notStartedPane;

    /**
     * Tooltips that will be shown on the progress bar.
     */
    private Tooltip completedTooltip, progressTooltip, notStartedTooltip;

    /**
     * Creates a new progress bar for stories.
     * @param hasTooltips sets wheather the progressbar will have tooltips.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public ModelProgressBar(final boolean hasTooltips) {
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
        rowConstraints.setMinHeight(5);
        rowConstraints.setPrefHeight(5);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        getRowConstraints().add(rowConstraints);

        completePane = new Pane();
        completePane.getStyleClass().add("completePane");
        setVgrow(completePane, Priority.ALWAYS);
        synchronized (StyleManager.getInstance()) {
            if (hasTooltips) {
                completedTooltip = new Tooltip("Completed");
                Tooltip.install(completePane, completedTooltip);
            }
        }

        progressPane = new Pane();
        progressPane.getStyleClass().add("progressPane");
        setVgrow(progressPane, Priority.ALWAYS);
        synchronized (StyleManager.getInstance()) {
            if (hasTooltips) {
                progressTooltip = new Tooltip("Completed");
                Tooltip.install(progressPane, progressTooltip);
            }
        }

        notStartedPane = new Pane();
        notStartedPane.getStyleClass().add("notstartedPane");
        setVgrow(notStartedPane, Priority.ALWAYS);
        synchronized (StyleManager.getInstance()) {
            if (hasTooltips) {
                notStartedTooltip = new Tooltip("Completed");
                Tooltip.install(notStartedPane, notStartedTooltip);
            }
        }

        add(completePane, 0, 0);
        add(progressPane, 1, 0);
        add(notStartedPane, 2, 0);
    }

    /**
     * Sets the story for the progress bar.
     * @param story the story to set.
     */
    @SuppressWarnings("checkstyle:magicnumber")
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

    /**
     * Sets the sprint for the progress bar.
     *
     * @param sprint the sprint
     */
    public void setSprint(final Sprint sprint) {
        float done = 0, inProgress = 0, notStarted = 0;

        for (Story story : sprint.getStories()) {
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
        }

        float total = done + inProgress + notStarted;
        if (total == 0) {
            total = 1;
        }
        float completedPercentage = done / total;
        float inProgressPercentage = inProgress / total;
        float notStartedPrecentage = notStarted / total;

        if (completedTooltip != null) {
            completedTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% complete.");
            progressTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% in progress.");
            notStartedTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% started.");
        }

        getColumnConstraints().get(0).setPrefWidth(Math.floor(completedPercentage * getWidth()));
        getColumnConstraints().get(1).setPrefWidth(Math.floor(inProgressPercentage * getWidth()));
        getColumnConstraints().get(2).setPrefWidth(Math.floor(notStartedPrecentage * getWidth()));
    }
}
