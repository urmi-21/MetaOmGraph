#!/usr/bin/env bash

apt() {
  if [[ -z "$1" ]]; then
    return 0;
  fi
  if [[ -f /usr/bin/apt ]]; then
    important "> apt install $1"
      
    info "Detected APT package manager! Proceeding with dependency installation..."

    /usr/bin/apt "install" "-y" $1

    return 0
  else
    error "Failed to locate required dependencies ($1). Aborting build."
    return 1
  fi
}

[[ -z "$BUILD_MODE" ]] && BUILD_MODE=2
case "$BUILD_MODE" in
  "$MODE_MIN")
    deps=("docker")
    locs=("/usr/bin/docker")
    ;;

  *)
    deps=("maven" "unzip" "wget" "file" "python3-pip")
    locs=("/usr/bin/mvn" "/usr/bin/unzip" "/usr/bin/wget" "/usr/bin/file" "/usr/bin/pip3")
    ;;
esac

missing=()
for (( i = 0; i < ${#deps[@]}; i++ )); do
  if [[ ! -f "${locs[$i]}" ]]; then
    missing+=("${deps[$i]}")
  fi
done

apt $missing
