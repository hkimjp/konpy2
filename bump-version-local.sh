#!/usr/bin/env bash

gsed -i "/^(def version/c\
(def version \"$1\")" src/hkimjp/konpy2/view.clj

