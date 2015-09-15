__author__ = 'james'

import requests
import re
import random
import sys

googleRequestURL = "https://translate.google.com/translate_a/single"
googleParameters = {
    "client": "t",
    "sl": "en",
    "tl": "",
    "dt": "t",
    "ie": "UTF-8",
    "oe": "UTF-8",
    "q": ""
}

bingAppIdURL = "http://www.bing.com/translator/dynamic/223578/js/LandingPage.js"
bingTranslateURL = "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2"
bingParameters = {
    "appId": "",
    "texts": "",
    "from": "en",
    "to": ""
}

languages = {
    "Afrikaans": ["af", None],
    "Albanian": ["sq", None],
    "Arabic": ["ar", "ar"],
    "Armenian": ["hy", None],
    "Azerbaijani": ["az", None],
    "Basque": ["eu", None],
    "Belarusian": ["be", None],
    "Bengali": ["bn", None],
    "Bosnian": ["bs", "bs-Latn"],
    "Bulgarian": ["bg", "bg"],
    "Catalan": ["ca", "ca"],
    "Cebuano": ["ceb", None],
    "Chichewa": ["ny", None],
    "Chinese Simplified": ["zh-CN", "zh-CHS"],
    "Chinese Traditional": ["zh-TW", "zh-CHT"],
    "Croatian": ["hr", "hr"],
    "Czech": ["cs", "cs"],
    "Danish": ["da", "da"],
    "Dutch": ["nl", "nl"],
    "Esperanto": ["eo", None],
    "Estonian": ["et", "et"],
    "Filipino": ["tl", None],
    "Finnish": ["fi", "fi"],
    "French": ["fr", "fr"],
    "Galician": ["gl", None],
    "Georgian": ["ka", None],
    "German": ["de", "de"],
    "Greek": ["el", "el"],
    "Gujarati": ["gu", None],
    "Haitian Creole": ["ht", "ht"],
    "Hausa": ["ha", None],
    "Hebrew": ["iw", "he"],
    "Hindi": ["hi", "hi"],
    "Hmong": ["hmn", "mww"],
    "Hungarian": ["hu", "hu"],
    "Icelandic": ["is", None],
    "Igbo": ["ig", None],
    "Indonesian": ["id", "id"],
    "Irish": ["ga", None],
    "Italian": ["it", "it"],
    "Japanese": ["ja", "ja"],
    "Javanese": ["jw", None],
    "Kannada": ["kn", None],
    "Kazakh": ["kk", None],
    "Khmer": ["km", None],
    "Klingon": [None, "tlh"],
    "Klingon IpIqaD": [None, "tlh-Qaak"],
    "Korean": ["ko", "ko"],
    "Lao": ["lo", None],
    "Latin": ["la", None],
    "Latvian": ["lv", "lv"],
    "Lithuanian": ["lt", "lt"],
    "Macedonian": ["mk", None],
    "Malagasy": ["mg", None],
    "Malay": ["ms", "ms"],
    "Malayalam": ["ml", None],
    "Maltese": ["mt", "mt"],
    "Maori": ["mi", None],
    "Marathi": ["mr", None],
    "Mongolian": ["mn", None],
    "Myanmar (Burmese)": ["my", None],
    "Nepali": ["ne", None],
    "Norwegian": ["no", "no"],
    "Persian": ["fa", "fa"],
    "Polish": ["pl", "pl"],
    "Portuguese": ["pt", "pt"],
    "Punjabi": ["ma", None],
    "Queretaro Otomi": [None, "otq"],
    "Romanian": ["ro", "ro"],
    "Russian": ["ru", "ru"],
    "Serbian": ["sr", "sr-Cyrl"],
    "Serbian (Latin)": [None, "sr-Latn"],
    "Sesotho": ["st", None],
    "Sinhala": ["si", None],
    "Slovak": ["sk", "sk"],
    "Slovenian": ["sl", "sl"],
    "Somali": ["so", None],
    "Spanish": ["es", "es"],
    "Sudanese": ["su", None],
    "Swahili": ["sw", None],
    "Swedish": ["sv", "sv"],
    "Tajik": ["tg", None],
    "Tamil": ["ta", None],
    "Telugu": ["te", None],
    "Thai": ["th", "th"],
    "Turkish": ["tr", "tr"],
    "Ukrainian": ["uk", "uk"],
    "Urdu": ["ur", "ur"],
    "Uzbek": ["uz", None],
    "Vietnamese": ["vi", "vi"],
    "Welsh": ["cy", "cy"],
    "Yiddish": ["yi", None],
    "Yoruba": ["yo", None],
    "Yucatec Maya": [None, "yua"],
    "Zulu": ["zu", None],
}
sillyLanguages = {
    "Hodor": ["Hodor"],
    "Foo": ["Foo", "Bar"],
    "Groot": ["I", "am", "Groot"],
    "Programmer": ["Build", "Compile", "Refactor", "Git", "Maven", "Java", "C#", "Python", "C", "C++", "Ruby", "Lisp",
                   "Fix", "Bug", "Issues", "Agilefant", "Breakpoint", "Pipes", "NullPointerException", "void",
                   "StackOverflow", "heap", "queue"]
}
keyValuesToTranslate = {}
translatedKeyValues = {}
retriedBing = False


def openProperties():
    global keyValuesToTranslate, translatedKeyValues
    f = open("..\\src\\main\\resources\\sws\\murcs\\languages\\words_en_EN.properties")
    f_lines = f.readlines()
    f.close()
    reference = open("english_reference.properties")
    reference_lines = reference.readlines()
    reference_key_values = {}
    for line in reference_lines:
        key_value = line.split(" = ")
        reference_key_values[key_value[0]] = key_value[1].replace("\n", "")
    reference.close()
    reference = open("english_reference.properties", "w")
    for line in f_lines:
        key_value = line.split(" = ")
        if key_value[0] in reference_key_values:
            if reference_key_values[key_value[0]] == key_value[1].replace("\n", ""):
                translatedKeyValues[key_value[0]] = key_value[1].replace("\n", "")
                reference.write(line)
                continue
        keyValuesToTranslate[key_value[0]] = key_value[1].replace("\n", "")
        reference.write(line)
    reference.close()


def parseGoogleResponse(content, word):
    m = re.search('\[\[\["([^"]+)"', content)
    if m is None:
        print("\nFailed to parse: " + content)
        print("Failing word: " + word)
        return word
    else:
        return m.group(1)


def parseBingResponse(content, word):
    m = re.search('"TranslatedText":"([^"]+)"', content)
    if m is None:
        print("\nFailed to parse: " + content)
        print("Failing word: " + word)
        return word
    else:
        return re.sub(r"\s+", " ", m.group(1).replace("+", "")).strip()


def googleTranslate(words, language):
    global googleRequestURL, googleParameters
    googleParameters["q"] = words
    googleParameters["tl"] = language
    r = requests.get(googleRequestURL, googleParameters)
    if str(r.status_code)[0] == 4 or str(r.status_code)[0] == 5:
        print("\nBad request for word " + words)
        print(r.content)
        return words
    return parseGoogleResponse(str(r.content), words)


def getBingAppId():
    global bingParameters, bingAppIdURL
    r = requests.get(bingAppIdURL)
    m = re.search('rttAppId:"([^"]+)"', str(r.content))
    if m is None:
        print("Failed to get Bing App ID")
        raise Exception
    else:
        bingParameters["appId"] = m.group(1)


def bingTranslate(words, language):
    global bingTranslateURL, bingParameters, retriedBing
    bingParameters["texts"] = "[\"" + re.sub(r"\s+", "+", words) + "\"]"
    bingParameters["to"] = language
    r = requests.get(bingTranslateURL, bingParameters)
    if str(r.status_code)[0] == 4 or str(r.status_code)[0] == 5:
        print("\nBad request for word " + words)
        print("Bing code: " + language)
        print(r.content)
        if not retriedBing:
            print("Trying to refresh Bing App ID")
            getBingAppId()
            retriedBing = True
            return bingTranslate(words, language)
        retriedBing = False
    return parseBingResponse(str(r.content), words)


def silly_translate(words, language):
    global sillyLanguages
    translation = ""
    for word in words.split(" "):
        translation += sillyLanguages[language][random.randint(0, len(sillyLanguages[language]) - 1)] + " "
    return translation.strip()


def merge_dict(dict1, dict2):
    merge = dict1.copy()
    merge.update(dict2)
    return merge


def translate_languages():
    global languages, keyValuesToTranslate, translatedKeyValues
    for language in languages.keys():
        print("Starting " + language)
        toFile = ""
        googleLanguageCode = languages[language][0]
        bingLanguageCode = languages[language][1]
        default = (googleLanguageCode, "Google") if googleLanguageCode != None else (bingLanguageCode, "Bing")
        toTranslate = keyValuesToTranslate.copy()
        # This bit checks for ones that are already translated before trying to translate them
        try:
            f = open("..\\src\\main\\resources\\sws\\murcs\\languages\\words_" + default[0].lower().replace("-", "") + "_" + default[0].upper().replace("-", "") + ".properties")
            for line in f.readlines():
                key_value = line.split(" = ")
                if key_value[0] in translatedKeyValues:
                    toFile += line
            f.close()
        except FileNotFoundError as e:
            print("No properties file for " + language + ". Everything will have to be translated")
            toTranslate = merge_dict(keyValuesToTranslate, translatedKeyValues)
        translationKeys = len(toTranslate)
        i = 0
        for key in toTranslate.keys():
            print(str(i / translationKeys * 100) + "% complete    ", end="\r")
            word = toTranslate[key]
            translated_word = googleTranslate(word, googleLanguageCode) if default[1] == "Google" else bingTranslate(word, bingLanguageCode)
            if translated_word == word and default[1] == "Google" and bingLanguageCode is not None:
                print("\nGoogle failed to translate " + word + ". Attempting to use Bing")
                translated_word = bingTranslate(word, bingLanguageCode)
                print("Bing translated " + word + " to " + translated_word)
            toFile += key + " = " + translated_word + "\n"
            i += 1

        f = open("..\\src\\main\\resources\\sws\\murcs\\languages\\words_" + default[0].lower().replace("-", "") + "_" + default[0].upper().replace("-", "") + ".properties", 'w')  # needs to save this to a file
        f.write(toFile)
        f.close()
        print("Finished " + language + "                     ")


def translate_silly_languages():
    global sillyLanguages, keyValuesToTranslate, translatedKeyValues
    for language in sillyLanguages.keys():
        toFile = ""
        toTranslate = keyValuesToTranslate.copy()
        # This bit checks for ones that are already translated before trying to translate them
        try:
            f = open("..\\src\\main\\resources\\sws\\murcs\\languages\\words_" + language[:2].lower() + "_" + language[:2].upper() + ".properties")
            for line in f.readlines():
                key_value = line.split(" = ")
                if key_value[0] in translatedKeyValues:
                    print("Skipped key :" + key_value[0])
                    toFile += line
            f.close()
        except FileNotFoundError as e:
            print("No properties file for " + language + ". Everything will have to be translated")
            toTranslate = merge_dict(keyValuesToTranslate, translatedKeyValues)
        translationKeys = len(toTranslate)
        i = 0
        for key in keyValuesToTranslate.keys():
            print(str(i / translationKeys * 100) + "% complete     ", end="\r")
            word = keyValuesToTranslate[key]
            translated_word = silly_translate(word, language)
            print(translated_word)
            toFile += key + " = " + translated_word + "\n"
            i += 1
        f = open("..\\src\\main\\resources\\sws\\murcs\\languages\\words_" + language[:2].lower() + "_" + language[:2].upper() + ".properties", 'w')
        f.write(toFile)
        f.close()
        print("Finished " + language + "                      ")

def main(args):
    openProperties()
    getBingAppId()
    translate_languages()
    translate_silly_languages()
    foo = input("The end")

if __name__ == "__main__":
    main(sys.argv[1:])
