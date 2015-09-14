__author__ = 'james'

import requests
import re
import random

requestURL = "https://translate.google.com/translate_a/single"
parameters = {
    "client": "t",
    "sl": "en",
    "tl": "",
    "dt": "t",
    "ie": "UTF-8",
    "oe": "UTF-8",
    "q": ""
}

languages = {
    "Afrikaans": "af",
    "Albanian": "sq",
    "Arabic": "ar",
    "Armenian": "hy",
    "Azerbaijani": "az",
    "Basque": "eu",
    "Belarusian": "be",
    "Bengali": "bn",
    "Bosnian": "bs",
    "Bulgarian": "bg",
    "Catalan": "ca",
    "Cebuano": "ceb",
    "Chichewa": "ny",
    "Chinese Simplified": "zh-CN",
    "Chinese Traditional": "zh-TW",
    "Croatian": "hr",
    "Czech": "cs",
    "Danish": "da",
    "Dutch": "nl",
    "Esperanto": "eo",
    "Estonian": "et",
    "Filipino": "tl",
    "Finnish": "fi",
    "French": "fr",
    "Galician": "gl",
    "Georgian": "ka",
    "German": "de",
    "Greek": "el",
    "Gujarati": "gu",
    "Haitian Creole": "ht",
    "Hausa": "ha",
    "Hebrew": "iw",
    "Hindi": "hi",
    "Hmong": "hmn",
    "Hungarian": "hu",
    "Icelandic": "is",
    "Igbo": "ig",
    "Indonesian": "id",
    "Irish": "ga",
    "Italian": "it",
    "Japanese": "ja",
    "Javanese": "jw",
    "Kannada": "kn",
    "Kazakh": "kk",
    "Khmer": "km",
    "Korean": "ko",
    "Lao": "lo",
    "Latin": "la",
    "Latvian": "lv",
    "Lithuanian": "lt",
    "Macedonian": "mk",
    "Malagasy": "mg",
    "Malay": "ms",
    "Malayalam": "ml",
    "Maltese": "mt",
    "Maori": "mi",
    "Marathi": "mr",
    "Mongolian": "mn",
    "Myanmar (Burmese)": "my",
    "Nepali": "ne",
    "Norwegian": "no",
    "Persian": "fa",
    "Polish": "pl",
    "Portuguese": "pt",
    "Punjabi": "ma",
    "Romanian": "ro",
    "Russian": "ru",
    "Serbian": "sr",
    "Sesotho": "st",
    "Sinhala": "si",
    "Slovak": "sk",
    "Slovenian": "sl",
    "Somali": "so",
    "Spanish": "es",
    "Sudanese": "su",
    "Swahili": "sw",
    "Swedish": "sv",
    "Tajik": "tg",
    "Tamil": "ta",
    "Telugu": "te",
    "Thai": "th",
    "Turkish": "tr",
    "Ukrainian": "uk",
    "Urdu": "ur",
    "Uzbek": "uz",
    "Vietnamese": "vi",
    "Welsh": "cy",
    "Yiddish": "yi",
    "Yoruba": "yo",
    "Zulu": "zu",
}
sillyLanguages = {
    "Hodor": ["Hodor"],
    "Foo": ["Foo", "Bar"],
    "Groot": ["I", "am", "Groot"],
    "Programmer": ["Build", "Compile", "Refactor", "Git", "Maven", "Java", "C#", "Python", "C", "C++", "Ruby", "Lisp", "Fix", "Bug", "Issues", "Agilefant", "Breakpoint", "Pipes", "NullPointerException", "void", "StackOverflow", "heap", "queue"]
}
keyValues = {}


def openProperties():
    global keyValues
    f = open("src\\main\\resources\\sws\\murcs\\languages\\words_en_EN.properties")
    for line in f.readlines():
        key_value = line.split(" = ")
        keyValues[key_value[0]] = key_value[1].replace("\n", "")
    f.close()


def parseResponse(content):
    m = re.search('\[\[\["([^"]+)"', content)
    if m is None:
        print("Failed: " + content)
        return ""
    else:
        return m.group(1)


def translate(word, language):
    global requestURL, parameters
    parameters["q"] = word
    parameters["tl"] = language
    r = requests.get(requestURL, parameters)
    return parseResponse(str(r.content))

def silly_translate(words, language):
    global sillyLanguages
    translation = ""
    for word in words.split(" "):
        translation += sillyLanguages[language][random.randint(0, len(sillyLanguages[language]) - 1)] + " "
    return translation.strip()


openProperties()
for language in languages.keys():
    toFile = ""
    languageCode = languages[language]
    for key in keyValues.keys():
        word = keyValues[key]
        translated_word = translate(word, languageCode)
        toFile += key + " = " + translated_word + "\n"

    f = open(
        "src\\main\\resources\\sws\\murcs\\languages\\words_" + languageCode + "_" + languageCode.upper() + ".properties", 'w')  # needs to save this to a file
    f.write(toFile)
    f.close()
    print("Finished " + language)

for language in sillyLanguages.keys():
    toFile = ""
    for key in keyValues.keys():
        word = keyValues[key]
        translated_word = silly_translate(word, language)
        print(translated_word)
        toFile += key + " = " + translated_word + "\n"
    f = open("src\\main\\resources\\sws\\murcs\\languages\\words_" + language[:2].lower() + "_" + language[:2].upper() + ".properties", 'w')
    f.write(toFile)
    f.close()
    print("Finished " + language)

input("End")
