package sws.murcs.magic.easyedit.fxml;

import javafx.scene.Node;
import sws.murcs.magic.easyedit.EditPaneGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *A class which maps fields to different FXML generators
 */
public class BasicPaneGenerator implements EditPaneGenerator {
    private final Map<Class, String> fxmlForFields = new HashMap<>();

    public BasicPaneGenerator(){
        fxmlForFields.put(Number.class, "/sws/project/Number.fxml");
        fxmlForFields.put(Integer.class, "/sws/project/Number.fxml");
        fxmlForFields.put(int.class, "/sws/project/Number.fxml");
        fxmlForFields.put(Float.class, "/sws/project/Number.fxml");
        fxmlForFields.put(float.class, "/sws/project/Number.fxml");
        fxmlForFields.put(Double.class, "/sws/project/Number.fxml");
        fxmlForFields.put(double.class, "/sws/project/Number.fxml");

        fxmlForFields.put(String.class, "/sws/project/String.fxml");

        fxmlForFields.put(Boolean.class, "/sws/project/Boolean.fxml");
        fxmlForFields.put(boolean.class, "/sws/project/Boolean.fxml");

        fxmlForFields.put(Collection.class, "/sws/project/Collection.fxml");
        fxmlForFields.put(ArrayList.class, "/sws/project/Collection.fxml");
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[] {String.class, int.class, Integer.class,
                Double.class, double.class, Float.class, float.class,
                Boolean.class, boolean.class,
                Collection.class, ArrayList.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Method validator, Object from) throws Exception {
        if (!fxmlForFields.containsKey(field.getType())) throw new Exception("Unsupported Field Type " + field.getType() + " on " + from);

        String fxmlPath = fxmlForFields.get(field.getType());

        FxmlPaneGenerator generator = new FxmlPaneGenerator();
        generator.setArgument(fxmlPath);

        return generator.generate(field, getter, setter, validator, from);
    }

    @Override
    public void setArgument(String argument) {

    }
}
