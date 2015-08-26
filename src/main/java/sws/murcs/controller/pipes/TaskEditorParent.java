package sws.murcs.controller.pipes;

import javafx.scene.Parent;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

import java.util.List;

public interface TaskEditorParent extends FormErrors {

    void removeTask(Task task);

    void removeTaskEditor(Parent view);

    void addTask(Task task);

    List<Task> getTasks();

    Story getAssociatedStory();
}
