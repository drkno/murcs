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

        Pane completePane = new Pane();
        setVgrow(completePane, Priority.ALWAYS);
        Pane progressPane = new Pane();
        setVgrow(progressPane, Priority.ALWAYS);
        Pane notStartedPane = new Pane();
        setVgrow(notStartedPane, Priority.ALWAYS);

        // tooltips and adding styles should be synchronised < u60
        synchronized (StyleManager.getInstance()) {
            completePane.getStyleClass().add("completePane");
            if (hasTooltips) {
                completedTooltip = new Tooltip("Completed");
                Tooltip.install(completePane, completedTooltip);
            }

            progressPane.getStyleClass().add("progressPane");
            if (hasTooltips) {
                progressTooltip = new Tooltip("Completed");
                Tooltip.install(progressPane, progressTooltip);
            }

            notStartedPane.getStyleClass().add("notstartedPane");
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

        updateDisplay(done, inProgress, notStarted);
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

        updateDisplay(done, inProgress, notStarted);
    }

    /**
     * Updates the progress bar to reflect the relative percentages of the totals.
     * @param done total done.
     * @param inProgress total in progress.
     * @param notStarted total complete.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void updateDisplay(final float done, final float inProgress, final float notStarted) {
        float total = done + inProgress + notStarted;
        if (total == 0) {   // divide by zero
            total = 1;
        }
        float completedPercentage = done / total;
        float inProgressPercentage = inProgress / total;
        float notStartedPercentage = notStarted / total;

        if (completedTooltip != null) {
            completedTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% complete.");
            progressTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% in progress.");
            notStartedTooltip.setText(Math.round(completedPercentage * 1000) / 10 + "% not started.");
        }

        getColumnConstraints().get(0).setPercentWidth(Math.floor(completedPercentage * 100));
        getColumnConstraints().get(1).setPercentWidth(Math.floor(inProgressPercentage * 100));
        getColumnConstraints().get(2).setPercentWidth(Math.floor(notStartedPercentage * 100));
    }
}
