__author__ = 'jayha_000'

path = "..\\src\\main\\resources\\sws\\murcs\\languages\\words_zhtw_ZHTW.properties"

# works but we lose newlines
def unicodify(path):
    file = open(path, 'rb')
    text = file.read() 
    file.close()
    decoded = text.decode('unicode-escape').encode('latin1').decode('utf-8')

    file = open("output.properties", mode='wb')
    file.write(decoded.encode('unicode-escape'))
    file.close()

unicodify(path)
