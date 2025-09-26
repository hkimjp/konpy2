#!/usr/bin/env bash

set -eux

gsed -i "/^(def version/c\
(def version \"$1\")" src/hkimjp/konpy2/response.clj

if [[ ! `echo $1 | rg SNAPSHOT` ]]; then
  gsed -i.bak "/^version/c\
version = \"$1\"" pyproject.toml
fi

