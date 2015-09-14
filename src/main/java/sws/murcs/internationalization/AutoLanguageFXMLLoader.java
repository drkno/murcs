package sws.murcs.internationalization;

import java.net.URL;
import javafx.fxml.FXMLLoader;

/**
 * Automagically loads a language into a FXML
 * file that specifies the keys. If you don't say what your key is,
 * you're going to have a bad time.
 */
public class AutoLanguageFXMLLoader extends FXMLLoader {
    public AutoLanguageFXMLLoader(URL url) {
        super(url);
        setResources(InternationalizationHelper.getCurrentResources());
    }

    public AutoLanguageFXMLLoader() {
        setResources(InternationalizationHelper.getCurrentResources());
    }
}
