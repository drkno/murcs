package sws.murcs.internationalization;

import java.net.URL;
import javafx.fxml.FXMLLoader;

/**
 * Automagically loads a language into a FXML
 * file that specifies the keys. Note: This class
 * relies heavily on the InternationalizationHelper
 * so make sure you've specified a valid language.
 */
public class AutoLanguageFXMLLoader extends FXMLLoader {

    /**
     * Loads the given url location of the fxml and sets it's resources to be the current languag.
     * @param url the url of the fxml file
     */
    public AutoLanguageFXMLLoader(final URL url) {
        super(url);
        setResources(InternationalizationHelper.getCurrentResources());
    }

    /**
     * Sets up the FXML loader with the current language resources.
     */
    public AutoLanguageFXMLLoader() {
        setResources(InternationalizationHelper.getCurrentResources());
    }
}
