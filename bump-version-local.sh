#!/usr/bin/env bash

set -eux

if [ -x "/run/current-system/sw/bin/sed" ]; then
    SED="/run/current-system/sw/bin/sed -E"
else
    SED="/usr/bin/sed -E"
fi

${SED} -i "/^\(def version/c\
(def version \"$1\")" src/hkimjp/konpy2/response.clj

if [[ ! `echo $1 | rg SNAPSHOT` ]]; then
  ${SED} -i.bak "/^version/c\
version = \"$1\"" pyproject.toml
fi

${SED} -i "/VER :=/c\
VER := '$1'" Justfile
