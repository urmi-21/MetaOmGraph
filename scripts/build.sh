#!/usr/bin/env bash

export BUILD_MODE=2

## Bash framework
## ===================== ##
source "$(realpath "$(dirname "$0")")/bootstrap.sh"
source "${DIR_BOOT}/libraries.sh"

ok "Build system is READY, building in three seconds."
sleep 3

## Build
## ===================== ##
important "Building app..."
mvn package

[[ -z "$CI_COMMIT_SHORT_SHA" ]] \
  && CI_COMMIT_SHORT_SHA=$(git log -n1 --format=format:"%h")

version="1.0+$CI_COMMIT_SHORT_SHA"

# Make sure dist directory exists.
if [[ ! -d "$DIR_ROOT/dist" ]]; then
  mkdir "$DIR_ROOT/dist"
fi

tag="jvm-$version"

# 1. Export as zip archive
cd "$DIR_ROOT/build/dpkg/"
source "$DIR_ROOT/build/dpkg/target.sh"

# 2. Export as deb (linux)
cd "$DIR_ROOT/build/zip/"
source "$DIR_ROOT/build/zip/target.sh"


# 3. Export as package (macos)
# TODO

cd "$DIR_ROOT"
