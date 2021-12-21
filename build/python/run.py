#!/usr/bin/env python3

import sys

sys.path.append('.')

import urllib.request
import zipfile as zip

from manifest import ResourceManager
from pathlib import Path

wd = sys.path[0]
resources = ResourceManager()

# Download JDK if missing.
if not Path(f"{wd}/{resources.jdk_version()}").is_dir():
    url = resources.jdk()
    if not url is None:
        print("Downloading JDK 17...")
        fileName, headers = urllib.request.urlretrieve(url)
        with zip.ZipFile(fileName, 'r') as fd:
            print("\tExtracting...")
            fd.extractall(workingDirectory)
            print("done!")
        
