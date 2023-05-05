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
js_files='/usr/share/nginx/html/static/js/*.js'

formatted_envs=$(printenv | awk -F= '{print $1}' | sed 's/^/\$/g' | paste -sd,)

for file in $js_files; do
  envsubst "$formatted_envs" <"$file" | sponge "$file"
done

nginx -g 'daemon off;'
