package sws.murcs.internationalization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Helps internationalize a node graph.
 */
public class InternationalizationHelper {
    private static InternationalizationHelper instance;

    private static Locale english = new Locale("en", "EN");
    private static Locale latin = new Locale("la", "LA");

    private static ResourceBundle englishBundle = ResourceBundle.getBundle("sws.murcs.languages.words", english);
    private static ResourceBundle latinBundle = ResourceBundle.getBundle("sws.murcs.languages.words", latin);

    private static ResourceBundle currentLocale = englishBundle;

    public static void setLanguage(Language language) {
        switch (language) {
            case English:
                currentLocale = englishBundle;
                break;
            case Latin:
                currentLocale = latinBundle;
                break;
        }
    }

    public static ResourceBundle getCurrentResources() {
        return currentLocale;
    }
}
