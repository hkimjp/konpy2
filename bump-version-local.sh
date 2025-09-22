#!/usr/bin/env bash

gsed -i "/^(def version/c\
(def version \"$1\")" src/hkimjp/konpy2/response.clj


# gsed -i.bak "/^version/c\
# version = \"$1\"" pyproject.toml
