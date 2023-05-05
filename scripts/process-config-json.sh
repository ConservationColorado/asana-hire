#!/usr/bin/env bash

# Shell script that transforms a JSON string to prepare it for envsubst:
#
# Example input JSON:
# ```
# {
#     "SERVER_BASE_URL": "http://localhost:3000",
#     "CLIENT_BASE_URL": "http://localhost:8080"
# }
# ```
#
# Example output JSON:
# ```
# {
#    "SERVER_BASE_URL": "$SERVER_BASE_URL",
#    "CLIENT_BASE_URL": "$CLIENT_BASE_URL"
# }
# ```
#
# Expects one argument: the path of the JSON file

tmp_file=/tmp/config.tmp.json

# From https://developers.redhat.com/blog/2021/03/04/making-environment-variables-accessible-in-front-end-containers
jq 'to_entries | map_values({ (.key) : ("$" + .key) }) | reduce .[] as $item ({}; . + $item)' "$1" >$tmp_file
mv $tmp_file "$1"
