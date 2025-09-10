#!/usr/bin/env bash

gsed -i "/^(def version/c\
(def version \"$1\")" src/hkimjp/wil2/view.clj

