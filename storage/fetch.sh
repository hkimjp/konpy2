#!/usr/bin/env bash
mv konpy2.sqlite konpy2.sqlite-`date +%F`
scp app.melt:konpy2/storage/konpy2.sqlite .
