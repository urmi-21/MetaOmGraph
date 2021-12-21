#!/usr/bin/env bash

# Bash framework
# -------------------------------
source "$(realpath "$(dirname "$0")")/bootstrap.sh"
source "${DIR_BOOT}/libraries.sh"


important "Checking libraries..."

pip3 install -r "$DIR_SCRIPT/build/mvnp/requirements" &>/dev/null

mvn install:install-file -Dfile="$DIR_PROJECT/lib/BrowserLauncher2-all-1_3.jar" -DgroupId='edu.iastate.metnet' -DartifactId='custombrowserlauncher' -Dversion='0.0.1' -Dpackaging=jar -DgeneratePom=true 2>&1
