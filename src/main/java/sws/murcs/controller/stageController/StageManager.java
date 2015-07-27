package sws.murcs.controller.stageController;


import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manages the stages visible to the viewer, keeps the order of what stages are on top of each other.
 */
public class StageManager {

    /**
     * Contains a list of stages on the screen.
     */
    private List<Stage> stages;

    /**
     * Creates a new stage manager.
     */
    public StageManager() {
        stages = FXCollections.observableArrayList();
    }

    /**
     * Brings the stage on top of all other stages.
     * @param stage The stage to bring to the top
     */
    public final void bringToTop(final Stage stage) {
        stages.set(0, stage);
        stage.toFront();
    }

    /**
     * Sends a stage to behind all other stages.
     * @param stage The stage to move to the back
     */
    public final void sendToBottom(final Stage stage) {
        int newIndex = stages.size() - 1;
        stages.set(newIndex, stage);
        stage.toBack();
    }

    /**
     * Sends a stage one position back.
     * @param stage The stage to move.
     */
    public final void sendBackwards(final Stage stage) {
        sendBackwards(stage, 1);
    }

    /**
     * Sends a stage back a given number of positions.
     * @param stage The stage to move.
     * @param howFar The number of positions to move the stage.
     */
    public final void sendBackwards(final Stage stage, final int howFar) {
        if (stages.indexOf(stage) + howFar > stages.size()) {
            stages.set(stages.size() - 1, stage);
        }
        else {
            stages.set(stages.indexOf(stage) + howFar, stage);
        }
        reOrderStages();
    }

    /**
     * Sends the stage forward one position.
     * @param stage The stage to move.
     */
    public final void sendForwards(final Stage stage) {
        sendForwards(stage, 1);
    }

    /**
     * Sends a stage forward a given number of positions.
     * @param stage The stage to move.
     * @param howFar The number of positions to move the stage.
     */
    public final void sendForwards(final Stage stage, final int howFar) {
        if (stages.indexOf(stage) - howFar < 0) {
            stages.set(0, stage);
        }
        else {
            stages.set(stages.indexOf(stage) - howFar, stage);
        }
        reOrderStages();
    }

    /**
     * Gets the front most stage.
     * @return The stage at the front.
     */
    public final Stage getTop() {
         return stages.get(0);
    }

    /**
     * Removes a stage from the manager.
     * @param stage The stage to remove
     */
    public final void removeStage(final Stage stage) {
        stages.remove(stage);
        stage.hide();
    }

    /**
     * Adds a stage to the top most position in stages.
     * @param stage new stage to manage.
     */
    public final void addStage(final Stage stage) {
        stages.add(0, stage);
        stage.toFront();
    }

    /**
     * Adds a stage behind all other stages.
     * @param stage The stage to add.
     */
    public final void addStageToBack(final Stage stage) {
        stages.add(stages.size(), stage);
        stage.toBack();
    }

    /**
     * Adds a stage with a given position.
     * @param stage The stage to add.
     * @param pos The position to place it.
     */
    public final void addStageWithPos(final Stage stage, final int pos) {
        stages.add(stage);
        setStagePosition(stage, pos);
    }

    /**
     * Gets the position of the given stage.
     * @param stage The Stage to get the position for.
     * @return The position.
     */
    public final int getStagePosition(final Stage stage) {
        return stages.indexOf(stage);
    }

    /**
     * Sets the position of a stage.
     * @param stage The stage to change the position of.
     * @param newPosition The new position to set the stage to.
     */
    public final void setStagePosition(final Stage stage, final int newPosition) {
        if (newPosition < 0) {
            stages.set(0, stage);
        }
        else if (newPosition < stages.size()) {
            stages.set(newPosition, stage);
        }
        else {
            stages.set(stages.size() - 1, stage);
        }
        reOrderStages();
    }

    /**
     * Reorders how the list of stages is displayed, according to the order of the list.
     * With the first index of the list being the stage shown at the top.
     */
    private void reOrderStages() {
        for (Stage stage: stages) {
            stage.toBack();
        }
    }

    /**
     * Gets a collection of unmodifiable stages.
     * @return stages.
     */
    public final Collection<Stage> getAllstages() {
        return Collections.unmodifiableCollection(stages);
    }
}
