#!/usr/bin/env bash

# check if we are in a terminal, if not, set our term var to screen so tput doesn't whine
export TERM=screen

# Colours
BLACK="$(tput setaf 0)"
RED="$(tput setaf 1)"
GREEN="$(tput setaf 2)"
YELLOW="$(tput setaf 3)"
BLU="$(tput setaf 4)"
PURPLE="$(tput setaf 5)"
CYAN="$(tput setaf 6)"
WHITE="$(tput setaf 7)"
RESET="$(tput sgr0)"

link() {
	echo -e "\e]8;;$2\a$1\e]8;;\a"
}

link_pro() {
	link "$3" "$1://$2" 
}

link_man() {
	link_pro "man" "$1" "$1"
}

link_file() {
	link_pro "file" "$1" "$1"
}

error() {
	echo
	echo "❌${RED}  ERR${RESET}: ${1}"
	echo
	echo
}

warn() {
	echo
	echo "⚠️${YELLOW}  WARN${RESET}: ${1}"
	echo
}

important() {
	echo
	echo "👉${PURPLE} ATTN${RESET}: ${1}"
	echo
}

ok() {
	echo "✅${GREEN} OKAY${RESET}: ${1}"
	echo
}

info() {
	echo "🔵${BLU} INFO${RESET}: ${1} "
}

debug() {
	echo "⭕${RED} DBUG${RESET}: ${1}"
}
