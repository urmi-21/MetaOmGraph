#!/usr/bin/env bash

# Assume bash framework is already loaded.
# source "$(realpath "$(dirname "$0")")/bootstrap.sh"
# source "${DIR_BOOT}/libraries.sh"

if [ ! -n "$(command -v dpkg-deb)" ]; then
  error "Aborting deb build. Missing dpkg-deb dependency!!"
  return
fi


# Move app jars to opt
DIR_BUILD_DPKG="$DIR_ROOT/build/dpkg/metaomgraph4"
cp -f "$DIR_ROOT"/target/*.jar "$DIR_BUILD_DPKG/opt/MOG4/"

# Ensure permissions are set correctly otherwise dpkg will complain.
chmod 0755 "$DIR_BUILD_DPKG/DEBIAN/"

# Build archive and place in dir above.
dpkg-deb -v \
  --build "$DIR_BUILD_DPKG" \
  "$DIR_ROOT/build/metaomgraph4-$tag.deb"

ok "Debian package exported! ($DIR_ROOT/build/metaomgraph4-$tag.deb)"
