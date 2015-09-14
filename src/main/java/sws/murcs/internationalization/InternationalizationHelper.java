package sws.murcs.internationalization;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Helps internationalize a node graph.
 */
public class InternationalizationHelper {
    private static InternationalizationHelper instance;

    private Locale english = new Locale("en", "EN");
    private Locale latin = new Locale("la", "LA");

    private ResourceBundle englishBundle = ResourceBundle.getBundle("sws.murcs.languages.words", english);
    private ResourceBundle currentLocale = englishBundle;

    //TODO public void setCurrentLocale(Locale)

    public void internationalize(Parent root) {
        root.getChildrenUnmodifiable().forEach(this::translate);
    }

    private void translate(Node node) {
        try {
            Method setter = node.getClass().getMethod("setText");
            Method getter = node.getClass().getMethod("getText");

            String currentText = (String) getter.invoke(node);
            if (currentText.startsWith("%")) {
                String key = currentText.substring(1);
                if (currentLocale.containsKey(key)) {
                    setter.invoke(node, currentLocale.getString(key));
                }
            }
        } catch (Exception e) {
            //We can't set text, don't translate
        } finally {
            if (node instanceof Parent) {
                internationalize((Parent) node);
            }
        }
    }

    private static void load() {
        if (instance == null) {
            instance = new InternationalizationHelper();
        }

        //LOAD DE BUNDLES!!!!
    }

    public static void main(String[] args) {
        InternationalizationHelper foo = new InternationalizationHelper();
        int fo = 7;
    }
}
