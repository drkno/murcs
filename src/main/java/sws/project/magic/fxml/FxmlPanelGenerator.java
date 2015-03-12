package sws.project.magic.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.project.magic.EditFormGenerator;
import sws.project.magic.EditPaneGenerator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Generates a panel for a field based on some FXML. This FXML
 * must specify a controller which must implement the 'EditController'
 * interface
 */
public class FxmlPanelGenerator implements EditPaneGenerator {
    private String fxmlPath;

    @Override
    public Class[] supportedTypes() {
        return new Class[] {String.class, int.class, Integer.class,
                Double.class, double.class, Float.class, float.class,
                Boolean.class, boolean.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Object from) {
        if (fxmlPath == null || fxmlPath.isEmpty())
            throw new IllegalArgumentException("The FXML path must be set. You can do this when you specify the field is editable like this: @Editable(FxmlPanelGenerator.class, argument=\"pathToFxml\"");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root;

        String title = EditFormGenerator.getEditable(field).friendlyName();
        if (title == null || title.isEmpty())
            title = EditFormGenerator.getFriendlyName(field);

        try{
            root = loader.load();
        }
        catch (IOException e){
            throw new UnsupportedOperationException("The FXML file could not be found/opened! Check it exists");
        }

        if (loader.getController() == null)
            throw new UnsupportedOperationException("The FXML must specify a controller!");

        if (!(loader.getController() instanceof EditController))
            throw new UnsupportedOperationException("The controller must implement the EditFormController interface");

        EditController controller = (EditController)loader.getController();

        //Check if the field is actually supported by the controller, otherwise we'd best throw an exception
        boolean supported = false;
        for (Class clazz : controller.supportedTypes()){
            if (field.getType().isAssignableFrom(clazz)){
                supported = true;
                break;
            }
        }

        //If the field isn't supported, throw an exceptions
        if (!supported)
            throw new UnsupportedOperationException("The FXML you supplied was valid, but it's controller doesn't support " + field.getType());

        try{
            controller.setTitle(title);

            Object value = getter.invoke(from);
            controller.setValue(value);
        }catch (Exception e){
            System.err.println("Unable to get " + field.getName() + " on " + from);
        }


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
