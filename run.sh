#!/usr/bin/env bash
# Starts the backend (http://localhost:8080) and the frontend (http://localhost:4200).
set -euo pipefail
cd "$(dirname "$0")"

source ./check-node.sh

if [ ! -d frontend/node_modules ]; then
  (cd frontend && npm ci)
fi

(cd backend && exec ./mvnw spring-boot:run) &
backend_pid=$!
(cd frontend && exec node_modules/.bin/ng serve) &
frontend_pid=$!

trap 'kill "$backend_pid" "$frontend_pid" 2>/dev/null' EXIT
wait
