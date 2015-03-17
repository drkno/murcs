package sws.project.controller;

import javafx.scene.Parent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sws.project.model.Model;
import sws.project.model.Person;
import sws.project.model.Project;
import sws.project.model.Team;

import java.util.concurrent.Callable;

/**
 * Provides helper methods for generating forms for editing and
 * creating new model objects
 */
public class EditorHelper {
    /**
     * Creates a new form for creating a new object of the specified type
     * @param clazz The type of object to create
     * @param success Called when the object is successully created
     */
    public static void createNew(Class<? extends Model> clazz, Callable<Void> success) throws Exception{
        ModelTypes type = ModelTypes.getModelType(clazz);

        switch (type){
            case Team:
                TeamEditor.displayWindow(success, null);
                break;
            case Project:
                ProjectEditor.displayWindow(success, null);
                break;
            case Skills:
                throw new NotImplementedException();
            case People:
                PersonEditor.displayWindow(success, null);
                break;
        }
    }

    /**
     * Creates a new form for editing an object
     * @param object The object to edit
     * @param onSaved What to do when the object is saved
     * @return The form
     */
    public static Parent getEditForm(Model object, Callable<Void> onSaved){
        ModelTypes type = ModelTypes.getModelType(object);

        Parent node = null;
        switch (type){
            case Team:
                node = TeamEditor.createFor((Team)object, onSaved);
                break;
            case Project:
                node = ProjectEditor.createFor((Project) object, onSaved);
                break;
            case Skills:
                throw new NotImplementedException();
            case People:
                node =PersonEditor.createFor((Person) object, onSaved);
                break;
        }
        return node;
    }
}
