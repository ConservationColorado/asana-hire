#!/usr/bin/env bash

# Script to replace environment variables with their actual value in React.js code.
# While 'npm build' replaces 'process.env' with the actual value of that environment variable,
# Docker images are often built without environment variables and are ideally stateless, while
# running images (a.k.a. containers) are stateful.

# Without this script, you'd need to rebuild a Docker image for each .env file you have, or
# rebuild the image each time you changed an environment variable.

# With this script, environment variables are injected into your images at runtime, rather than
# at build time. This makes your containers more portable and more safely publishable to an
# image registry.

# This script is designed to run in the execution phase of the Node.js Dockerfile, in this case, /frontend/Dockerfile
# Note: The execution image must accept all necessary environment variables using ARG and ENV!
# Adapted from https://developers.redhat.com/blog/2021/03/04/making-environment-variables-accessible-in-front-end-containers

# Assumes *.js files from builder image are already in the nginx directory
js_path='/usr/share/nginx/html/static/js'
cp "$js_path"/*.js /tmp

for file in /tmp/*.js; do
  # Environment variables in Node.js always start with 'process.env', so replace that with '$'
  # Run envsubst to substitute in the actual value of that variable
  # For example, if REACT_APP_VAR=foo, then:
  #   1. 'process.env.REACT_APP_VAR' becomes '$REACT_APP_VAR' (sed),
  #   2. This becomes 'foo' (envsubst)
  #   3. The result is written from the temp file to the final file
  sed 's/process\.env\./\$/g' "$file" | envsubst > "$js_path/$(basename "$file")"
done

nginx -g 'daemon off;'
