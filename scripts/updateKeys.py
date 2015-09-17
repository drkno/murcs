__author__ = 'jayha_000'

# the path to the folder with everything in it
path = "..\\src\\main\\resources\\sws\\murcs\\"

import os
keys = dict()

def replace(replace_text, lines):
	for i, line in enumerate(lines):
		if replace_text in line:
			start = line.index(replace_text) + len(replace_text)
			print("Start:", start, "Length:", len(line), "Line:", line)
			end = line.index('"', start)
			text = line[start:end]

			# Make sure we don't already have this.
			if len(text) > 0 and text[0] != "%":
				key = cleanText(text)
				value = text

				key = addEntry(key, value)
				newText = "%" + key

				if key is not None:
					line = line[:start] + newText + line[end:]

		lines[i] = line

def extractKeys(fxmlPath):
    f = open(fxmlPath, 'r')
    lines = f.readlines()
    replace('text="', lines)
    replace('promptText="', lines)

    f.close()
    f = open(fxmlPath, "w")
    for line in lines:
    	f.write(line)

def addEntry(prefKey, value):
    """Adds a key/value pair to our list. If it already exists (and the value is different)
    then we change the key"""

    global keys
    while prefKey in keys and not keys[prefKey] == value:
    	prefKey += "_"

    prefKey = prefKey.strip()
    if len(prefKey) == 0:
    	return None

    keys[prefKey] = value
    return prefKey


def cleanText(text):
	text = text.replace(" ", "")
	result = ""
	for char in text:
		if char.lower() in "abcdefghijklmnopqrstuvwxyz01234567890":
			result += char

	return result

def readKeys(resourcePath):
	global keys
	file = open(resourcePath, "r")
	lines = file.readlines()
	for line in lines:
		parts = line.split(" = ")
		keys.update({parts[0].strip() : parts[1].strip()})

def extractAll(outputFile):
    global keys
    readKeys(outputFile)

    files = []
    for file in os.listdir(path + "reporting\\"):
        if file.endswith(".fxml"):
            files.append(file)
    
    for file in files:
    	extractKeys(path + "reporting\\" + file)

    file = open(outputFile, "w")

    for key in sorted(keys):
    	file.write(key + " = " + keys[key] + "\n")


extractAll(path + "languages\\words_en_EN.properties")