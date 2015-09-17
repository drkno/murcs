package sws.murcs.internationalization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * A control to read Resource Bundles as UTF-8 so that they actually load properly into the application.
 */
public class UTF8Control extends ResourceBundle.Control {

    /**
     * Creates a new resource bundle in the same manner as usual but loads and reads the file as a UTF-8 file.
     * This is to makes sure that file loading actually works.
     * @param baseName the base name of the language
     * @param locale the locale of the language
     * @param format the format of the resource bundle
     * @param loader the class loader used to get the resource bundle
     * @param reload whether or not to reload the bundle
     * @return a new resource bundle for the specified language
     * @throws IllegalAccessException thrown if you illegally access the resource bundle.
     * @throws InstantiationException thrown if you fail to instantiate the resource bundle
     * @throws IOException thrown if you fail to load the resource bundle file.
     */
    public ResourceBundle newBundle
            (final String baseName, final Locale locale, final String format, final ClassLoader loader,
             final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                // Only this line is changed to make it to read properties files as UTF-8.
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
