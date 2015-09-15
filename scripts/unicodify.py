__author__ = 'jayha_000'

path = "..\\src\\main\\resources\\sws\\murcs\\languages\\words_zhtw_ZHTW.properties"

# works but we lose newlines
def unicodify(path):
    f = open(path, 'rb')
    text = f.read()
    f.close()
    decoded = text.decode('unicode-escape').encode('latin1').decode('utf-8')

    f = open("output.properties", mode='wb')
    f.write(decoded.encode('unicode-escape'))
    f.close()

unicodify(path)
# __author__ = 'jayha_000'

# path = "..\\src\\main\\resources\\sws\\murcs\\languages\\words_zhtw_ZHTW.properties"

# # works but we lose newlines
# def unicodify(path):
#     f = open(path, 'rb')
#     lines = f.readlines()
#     f.close()
#     #decoded = text.decode('unicode-escape').encode('latin1').decode('utf-8')

#     f = open("output.properties", mode='w')
#     for line in lines:
#     	decoded = line.decode('unicode-escape').encode('latin1').decode('utf-8').strip()
#     	foo = decoded.encode('unicode-escape').splitlines()
#     	f.write(str(foo[0]).replace("\\\\", "\\").replace("'b'", ''))
#     f.close()

# unicodify(path)
