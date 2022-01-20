#!/usr/bin/env python3

import sys
import os
import platform
import urllib.request
import zipfile as zip

class ResourceManager:
    def __init__(self):
        info = platform.uname()
        self.name = info.system
        self.arch = info.machine.lower()
        print(f"ResourceManager: {self.name} (arch {self.arch})")

    def jdk_version(self) -> str:
        return 'jdk-17.0.1'

    def jdk(self) -> str:
        jdk = {
            'Windows': {
                'x64': 'https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_windows-x64_bin.zip',
            },
            'Darwin': {
                'x64': 'https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_macos-x64_bin.tar.gz',
                'AArch64': 'https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_macos-aarch64_bin.tar.gz',
            },
            'Linux': {
                'x64': 'https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_linux-x64_bin.tar.gz',
                'AArch64': 'https://download.java.net/java/GA/jdk17.0.1/2a2082e5a09d4267845be086888add4f/12/GPL/openjdk-17.0.1_linux-aarch64_bin.tar.gz'
            }
        }

        try:
            arch = ''
            if self.arch.startswith('amd') or self.arch == 'x64':
                arch = 'x64'
            elif self.arch.startswith('arm'):
                arch = 'AArch64'

            return jdk.get(self.name).get(arch)
            
        except (TypeError):
            print('Error: Your system does not match the regular installation\'s JDK system list.')
            print('You will need to manually install OpenJDK 17.')
            
            return None
