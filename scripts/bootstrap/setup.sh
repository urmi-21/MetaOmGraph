#!/usr/bin/env bash


if [[ ! -v DIR_PROJECT ]]; then #ifdef DIR_PROJECT

# CLI Framework
# -------------------------------

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

fi                              #endif
