#!/usr/bin/env bash

# Adapted from https://developers.redhat.com/blog/2021/03/04/making-environment-variables-accessible-in-front-end-containers
EXISTING_VARS=$(printenv | awk -F= '{print $1}' | sed 's/^//g' | paste -sd,);
export EXISTING_VARS

for file in $JSFOLDER;
do
  sed -i 's/process\.env\./\$/g' "$file" | envsubst "$EXISTING_VARS" | tee "$file"
done

nginx -g 'daemon off;'
