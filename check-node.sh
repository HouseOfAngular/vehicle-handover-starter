#!/usr/bin/env bash
# Fails with a readable message when Node.js is missing or older than the Angular CLI needs.
# Sourced by run.sh and test.sh; the supported range matches "engines" in frontend/package.json.

if ! command -v node >/dev/null 2>&1; then
  echo "ERROR: Node.js not found. Install Node.js 22.22.3+ (22.x) or 24.15+ (see README.md, .nvmrc)." >&2
  exit 1
fi

node_version="$(node -v)"
IFS=. read -r node_major node_minor node_patch <<<"${node_version#v}"

node_supported=false
if [ "$node_major" -ge 26 ]; then
  node_supported=true
elif [ "$node_major" -eq 24 ] && [ "$node_minor" -ge 15 ]; then
  node_supported=true
elif [ "$node_major" -eq 22 ] && { [ "$node_minor" -gt 22 ] || { [ "$node_minor" -eq 22 ] && [ "$node_patch" -ge 3 ]; }; }; then
  node_supported=true
fi

if [ "$node_supported" != true ]; then
  echo "ERROR: Node.js $node_version is not supported. Use Node.js 22.22.3+ (22.x) or 24.15+." >&2
  echo "       With nvm: run 'nvm install && nvm use' in the repository root (see .nvmrc)." >&2
  exit 1
fi
