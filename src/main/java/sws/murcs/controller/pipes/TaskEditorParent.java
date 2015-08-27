package sws.murcs.controller.pipes;

import javafx.scene.Parent;
import sws.murcs.controller.editor.TaskEditor;
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
     * Remove the given view/editor from the parent editor.
     * @param view the view/editor to remove
     */
    void removeTaskEditor(Parent view);

    /**
     * Add a new task to the parent editor.
     * @param task the task to add.
     */
    void addTask(Task task);

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
     * @param editor the task editor that has had changes happen in it.
     */
    void changesMade(TaskEditor editor);
}
