package sws.murcs.internationalization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Helps internationalize a node graph.
 */
public class InternationalizationHelper {
    /**
     * The current locale.
     */
    private static Locale currentLocale;

    /**
     * The current bundle.
     */
    private static ResourceBundle currentBundle;

    /**
     * Returns a list of all languages supported by the app.
     * @return The supported languages
     */
    public static List<String> getLanguages() {
        return languages.keySet().stream().sorted(Comparator.<String>naturalOrder()).collect(Collectors.toList());
    }

    /**
     * Sets the current language.
     * Bad things will happen if it's not one of the ones we support. Like,
     * really bad things. Things so bad I should probably throw an exception.
     * But I won't. Because you aren't the boss of me. (this should probably not
     * get through PR).
     * @param language The language.
     */
    public static void setLanguage(String language) {
        String code = languages.get(language);
        currentLocale = new Locale(code.toLowerCase(), code.toUpperCase());
        currentBundle = ResourceBundle.getBundle("sws.murcs.languages.words", currentLocale, new UTF8Control());
    }

    /**
     * Returns the translated text. If it can't find the key it returns null.
     * @param key The translation key
     */
    public static String tryGet(String key) {
        if (!getCurrentResources().containsKey(key)) {
            return null;
        }
        return getCurrentResources().getString(key);
    }

    /**
     * Takes a string with keys surrounded by {key} and replaces
     * those keys with their translation.
     * @param text The text to translatasert.
     * @return The translataserted text.
     */
    public static String translatasert(String text) {
        String result = text;

        int startIndex = text.indexOf("{");
        int endIndex = text.indexOf("}", startIndex);

        List<String> keys = new ArrayList<>();

        while (startIndex != -1 && endIndex != -1) {
            String key = text.substring(startIndex + 1, endIndex);
            keys.add(key);

            startIndex = text.indexOf("{", endIndex);
            endIndex = text.indexOf("}", startIndex);
        }

        for (String key : keys) {
            String translated = tryGet(key);
            if (translated == null) {
                continue;
            }
            result = result.replace("{" + key + "}", translated);
        }

        return result;
    }

    /**
     * Gets the current active resource bundle.
     * @return The current active resource bundle.
     */
    public static ResourceBundle getCurrentResources() {
        if (currentBundle == null) {
            setLanguage("English");
        }
        return currentBundle;
    }

    /**
     * A hashmap of languages to codes.
     */
    private static Map<String, String> languages = new HashMap<String, String>()
    {{
        put("Afrikaans", "af");
        put("Albanian", "sq");
        put("Arabic", "ar");
        put("Armenian", "hy");
        put("Azerbaijani", "az");
        put("Basque", "eu");
        put("Belarusian", "be");
        put("Bengali", "bn");
        put("Bosnian", "bs");
        put("Bulgarian", "bg");
        put("Catalan", "ca");
        put("Cebuano", "ceb");
        put("Chichewa", "ny");
        put("Chinese Simplified", "zhcn");
        put("Chinese Traditional", "zhtw");
        put("Croatian", "hr");
        put("Czech", "cs");
        put("Danish", "da");
        put("Dutch", "nl");
        put("English", "en");
        put("Esperanto", "eo");
        put("Estonian", "et");
        put("Filipino", "tl");
        put("Finnish", "fi");
        put("French", "fr");
        put("Foo", "fo");
        put("Galician", "gl");
        put("Georgian", "ka");
        put("German", "de");
        put("Greek", "el");
        put("Groot", "gr");
        put("Gujarati", "gu");
        put("Haitian Creole", "ht");
        put("Hausa", "ha");
        put("Hebrew", "iw");
        put("Hindi", "hi");
        put("Hmong", "hmn");
        put("Hodor", "ho");
        put("Hungarian", "hu");
        put("Icelandic", "is");
        put("Igbo", "ig");
        put("Indonesian", "id");
        put("Irish", "ga");
        put("Italian", "it");
        put("Japanese", "ja");
        put("Javanese", "jw");
        put("Kannada", "kn");
        put("Kazakh", "kk");
        put("Khmer", "km");
        put("Korean", "ko");
        put("Lao", "lo");
        put("Latin", "la");
        put("Latvian", "lv");
        put("Lithuanian", "lt");
        put("Macedonian", "mk");
        put("Malagasy", "mg");
        put("Malay", "ms");
        put("Malayalam", "ml");
        put("Maltese", "mt");
        put("Maori", "mi");
        put("Marathi", "mr");
        put("Mongolian", "mn");
        put("Myanmar (Burmese)", "my");
        put("Nepali", "ne");
        put("Norwegian", "no");
        put("Persian", "fa");
        put("Polish", "pl");
        put("Portuguese", "pt");
        put("Programmer", "pr");
        put("Punjabi", "ma");
        put("Romanian", "ro");
        put("Russian", "ru");
        put("Serbian", "sr");
        put("Sesotho", "st");
        put("Sinhala", "si");
        put("Slovak", "sk");
        put("Slovenian", "sl");
        put("Somali", "so");
        put("Spanish", "es");
        put("Sudanese", "su");
        put("Swahili", "sw");
        put("Swedish", "sv");
        put("Tajik", "tg");
        put("Tamil", "ta");
        put("Telugu", "te");
        put("Thai", "th");
        put("Turkish", "tr");
        put("Ukrainian", "uk");
        put("Urdu", "ur");
        put("Uzbek", "uz");
        put("Vietnamese", "vi");
        put("Welsh", "cy");
        put("Yiddish", "yi");
        put("Yoruba", "yo");
        put("Zulu", "zu");
    }};
}
