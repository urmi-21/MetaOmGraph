#!/usr/bin/env bash

# Assume bash framework is already loaded.
# source "$(realpath "$(dirname "$0")")/bootstrap.sh"
# source "${DIR_BOOT}/libraries.sh"


name="metaomgraph4-jvm-$version"

zip -9 "../$name.zip" "$DIR_ROOT"/target/*.jar 

ok "Zip archive exported! ($DIR_ROOT/build/$name.zip)"
