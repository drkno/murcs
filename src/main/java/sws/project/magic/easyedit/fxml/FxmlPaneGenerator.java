package sws.project.magic.easyedit.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.project.magic.easyedit.EditFormGenerator;
import sws.project.magic.easyedit.EditPaneGenerator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Generates a panel for a field based on some FXML. This FXML
 * must specify a controller which must implement the 'EditController'
 * interface
 */
public class FxmlPaneGenerator implements EditPaneGenerator {
    private String fxmlPath;

    @Override
    public Class[] supportedTypes() {
        return new Class[] {String.class, int.class, Integer.class,
                Double.class, double.class, Float.class, float.class,
                Boolean.class, boolean.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Method validator, Object from) throws Exception {
        if (fxmlPath == null || fxmlPath.isEmpty())
            throw new Exception("The FXML path must be set. You can do this when you specify the field is editable like this: @Editable(FxmlPanelGenerator.class, argument=\"pathToFxml\"");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root;

        String title = EditFormGenerator.getFriendlyName(field);

        try{
            root = loader.load();
            if (root == null)
                throw new Exception("Failed to load the FXML file (don't know why sorry :'()");
        }
        catch (Exception e){
            throw new IOException("The FXML file \"" + fxmlPath + "\" could not be found/opened! Check it exists and that your path is correct");
        }

        if (loader.getController() == null)
            throw new Exception("The FXML must specify a controller! (the controller was null)");

        if (!(loader.getController() instanceof EditController))
            throw new Exception("The controller (" + loader.getController().getClass().getName() + ") must implement the EditFormController interface (it doesn't).");

        EditController controller = loader.getController();

        //Check if the field is actually supported by the controller, otherwise we'd best throw an exception
        boolean supported = false;
        for (Class clazz : controller.supportedTypes()){
            if (field.getType().isAssignableFrom(clazz)){
                supported = true;
                break;
            }
        }

        //If the field isn't supported, throw an exception
        if (!supported)
            throw new Exception("The FXML you supplied was valid, but it's controller (" + loader.getController().getClass().getName() + ") doesn't support " + field.getType());

        //Tell the controller what its title should be
        controller.setTitle(title);

        try{
            //Get the current value of the field
            Object value = getter.invoke(from);
            //Tell the controller what its starting value should be
            controller.setValue(value);
        }catch (Exception e){
            System.err.println("Unable to get " + field.getName() + " from " + from);
            throw new Exception("Couldn't get the field to tell the controller the current value (this is bad. Check to see if the getter name is valid");
        }

        //Add a validator which ensures we're getting the correct type
        controller.addValidator(v -> field.getType().isAssignableFrom(v.getClass()));

        //If we have been passed a validator method, we should pass it as an argument to the controller
        //and trust it does the right thing with it.
        if (validator != null) controller.addValidator(v -> {
            try {
                return (Boolean) validator.invoke(from, v);
            } catch (Exception e) {
                System.err.println("Failed to invoke the validator for " + field.getName() + " on " + from + " with value " + v);
            }
            return false;
        });

        controller.addChangeListener((p, o, n) -> {
            try {
                setter.invoke(from, n);
            }catch (Exception e){
                System.err.println("Unable to set " + field.getName() + " on " + from);
            }
        });

        return root;
    }

    @Override
    public void setArgument(String argument){
        fxmlPath = argument;
    }
}
