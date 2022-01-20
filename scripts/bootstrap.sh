#!/usr/bin/env bash

if [[ ! -v DIR_PROJECT ]]; then #ifdef DIR_PROJECT

## CLI Framework
## ===================== ##

function rel_pwd() {
	echo $(realpath "$(dirname "$0")$1")

	return 0;
}

# Build system modes:
# 1. Minimum mode (helpers + dependencies ONLY)
# 2. Building mode (everything)
if [[ ! -v BUILD_MODE ]]; then
	export BUILD_MODE=1
fi
export MODE_MIN=1
export MODE_FULL=2

export DIR_PROJECT="$(rel_pwd "/../src")"
export DIR_ROOT="$(rel_pwd "/../")"
export DIR_SCRIPT="$(rel_pwd "/.")"
export DIR_BOOT="$(rel_pwd "/bootstrap")"

source "${DIR_BOOT}/fun.sh"
source "${DIR_BOOT}/cli.sh"

fi                              #endif

# Globstar is super nice!
shopt -s globstar

## Pre-flight
## ===================== ##
if [[ $BUILD_MODE > $MODE_MIN ]]; then
	JAVA_VERSION=$(java --version | head -n1 | awk -F '[^0-9]*' '$0=$2')
	if [[ -z ${OJAVA_VERSION+z} && ${JAVA_VERSION} < 17 ]]; then
		error "Using $(link_man $(which java)) (JDK ${JAVA_VERSION}); build requires ${BOLD}JDK 17${NORMAL} or higher.";
		exit 1;
	fi
fi

## Exit bootstrap
## ===================== ##

# Make sure our pwd is in project root.
cd "$DIR_ROOT"

## Final bootstrapping
## ===================== ##
if [[ $BUILD_MODE > $MODE_MIN ]]; then
  # Download dependencies
  important "Checking build sys dependencies..."
  source "${DIR_BOOT}/dependencies.sh"
  ok "All dependencies are OK."
fi
