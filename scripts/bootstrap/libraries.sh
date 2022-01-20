#!/usr/bin/env bash

dnc() {
  FILE_EXT="$(echo "$2" | grep -oE "\.\w*$")"
  FILE_INFO="$(file $1)"
  FILE_LOCATION="$(dirname $1)"
  FILE_NAME="$(basename $1)"

  if [[ "$FILE_EXT" =~ 'zip' ]]; then
    ok "Extracting as zip! $FILE_LOCATION/$FILE_NAME"
    mv "$FILE_LOCATION/$FILE_NAME"{,.zip} \
      && unzip -od "$DIR_PROJECT/lib/" "$FILE_LOCATION/$FILE_NAME.zip" &>/dev/null

  elif [[ "$FILE_EXT" =~ 'gz' ]]; then
    ok "Extracting as gzip! $FILE_LOCATION/$FILE_NAME"
    mv "$FILE_LOCATION/$FILE_NAME"{,.gz} \
      && gzip -d "$FILE_LOCATION/$FILE_NAME.gz" &>/dev/null

  else
    error "Could not decode unknown archive type at \"$(link_file $FILE_DEST)\" (ext: "$FILE_EXT")."
    return 1
  fi

  return 0;
}

download_lib() {
  info "Attempting to download library..."
  FILE_DEST="$DIR_PROJECT/lib/$2"

  if [[ -f "$FILE_DEST" ]]; then
    ok "Aborting. Already installed at $(link_file $FILE_DEST)"
    return 0;
  fi

  wget --quiet "$1" -O"$FILE_DEST" 

  return 0;
}


download_libz() {
  FILE_DEST="$DIR_PROJECT/lib/$2"
  download_lib "$1" "$2"

  dnc "$FILE_DEST" "$1"

  return 0;
}


