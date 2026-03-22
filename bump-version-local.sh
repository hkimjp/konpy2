#!/usr/bin/env bash

set -eux

if [ -x "/run/current-system/sw/bin/sed" ]; then
    SED="/run/current-system/sw/bin/sed"
else
    SED="/usr/bin/sed"
fi

${SED} -i -E "/^\(def version/c\
(def version \"$1\")" src/hkimjp/konpy2/response.clj

if [[ ! `echo $1 | rg SNAPSHOT` ]]; then
  ${SED} -i -E "/^version/c\
version = \"$1\"" pyproject.toml
fi

${SED} -i -E "/VER :=/c\
VER := '$1'" Justfile
