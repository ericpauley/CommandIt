import requests
from cStringIO import StringIO
import string
import random
import re
import os

def randstring(size=24, chars=string.ascii_uppercase + string.digits):
    return ''.join(random.choice(chars) for x in range(size))
 
headers = {
	'User-Agent': 'CurseForge Uploader Script/1.0',
        'X-API-Key': os.environ['BUKKITDEV_KEY']
}
 
versions = requests.get('http://dev.bukkit.org/game-versions.json').json()
version = 0
for i in versions.keys():
    if(int(i) > version):
        version = int(i)
 
files = {'file': ('CommandIt.jar', open('bin/CommandIt.jar', 'r'))}
data = {
	'name': 'CommandIt v@SIMPLE_VERSION@',
	'game_versions': str(version),
	'file_type': 'r',
	'change_log': open('CHANGES.md', 'r').read(),
	'change_markup_type': 'markdown',
	'known_caveats': open('CAVEATS.md', 'r').read(),
	'caveats_markup_type': 'markdown',
}
 
r = requests.post('http://dev.bukkit.org/server-mods/cmdit/upload-file.json', data=data, headers=headers, files=files)
