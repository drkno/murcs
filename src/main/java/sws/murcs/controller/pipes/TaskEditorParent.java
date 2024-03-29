package sws.murcs.controller.pipes;

import sws.murcs.model.Story;
import sws.murcs.model.Task;

import java.util.List;

/**
 * The interface to be implemented by any editor that is a parent editor of a task editor in order to enable
 * communication between the two editors.
 */
public interface TaskEditorParent extends FormErrors {

    /**
     * Remove the given tasks from the the parent editor.
     * @param task the task to remove.
     */
    void removeTask(Task task);

    /**
     * Get all the tasks in the parent editor.
     * @return all the tasks in the parent editor.
     */
    List<Task> getTasks();

    /**
     * Get the story that is associated with the given task.
     * @param task the task to find the linked story for.
     * @return the linked story.
     */
    Story getAssociatedStory(Task task);

    /**
     * Called when changes have been made in the task editor that require an update in the parent editor.
     */
    void changesMade();

    /**
     * Updates all the information in the task editors associated with this task editor parent.
     */
    void updateEditors();

    /**
     * Called when the task has finished creation.
     */
    void finishedCreation();
}
