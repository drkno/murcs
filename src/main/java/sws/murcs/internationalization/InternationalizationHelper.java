package sws.murcs.internationalization;

import java.util.HashMap;
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
//    private static ResourceBundle latinBundle = ResourceBundle.getBundle("sws.murcs.languages.words", latin);

    private static ResourceBundle currentLocale = englishBundle;

    public static void setLanguage(Language language) {
        switch (language) {
            case English:
                currentLocale = englishBundle;
                break;
            case Latin:
                //currentLocale = latinBundle;
                break;
        }
    }

    public static ResourceBundle getCurrentResources() {
        return currentLocale;
    }

    private static HashMap<String, String> languages = new HashMap<String, String>()
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
        put("Galician", "gl");
        put("Georgian", "ka");
        put("German", "de");
        put("Greek", "el");
        put("Gujarati", "gu");
        put("Haitian Creole", "ht");
        put("Hausa", "ha");
        put("Hebrew", "iw");
        put("Hindi", "hi");
        put("Hmong", "hmn");
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
