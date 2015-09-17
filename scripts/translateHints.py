__author__ = 'jayha_000'

path = "..\\src\main\\resources\\sws\\murcs\\"
helpfulHints = path + "helpfulHints\\helpfulHints.nsv"
helpfulHintKeys = path + "helpfulHints\\helpfulHintKeys.nsv"
englishWords = path + "languages\\words_en_EN.properties"

keys = dict()
hints = None

# Function for getting a key for a hint. May need some tidying
def key_for_hint(hint):
	# Check if our hint has already been added but has some funky name
	for key in keys:
		if keys[key] == hint:
			return key

	key = hint.split("-")[-1].strip() #get the thing after the last (probably the shortcut) - to be our key

	cleaned_key = ""
	for char in key:
		if char.lower() not in "abcdefghijklmnopqrstuvwxyz0123456789":
			continue
		cleaned_key += char

	# If this hint has the same key as something else, give it a different key
	while cleaned_key in keys and keys[cleaned_key] != hint:
		cleaned_key = "_" + cleaned_key

	return cleaned_key

# Read all our hints
with open(helpfulHints) as f:
	hints = f.readlines()

# Read all our keys and their english words
with open(englishWords) as f:
	lines = f.readlines()

	for line in lines:
		parts = line.strip('\r\n\t').split(" = ")
		keys[parts[0]] = parts[1]

# Add all the hints we don't already have to our english file
hint_keys = []
for hint in hints:
	key = key_for_hint(hint)
	hint_keys.append(key)
	if key not in keys:
		keys[key] = hint

# Save our new hints file
with open(englishWords, 'w') as f:
	for key in sorted(keys):
		f.write(key + " = " + keys[key].strip() + '\n')

with open(helpfulHintKeys, 'w') as f:
	for key in hint_keys:
		f.write(key.strip() + '\n')


