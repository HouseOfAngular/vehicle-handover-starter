#!/usr/bin/env bash
# Runs all backend and frontend tests. Exits with a non-zero code if anything fails.
set -euo pipefail
cd "$(dirname "$0")"

(cd backend && ./mvnw test)

if [ ! -d frontend/node_modules ]; then
  (cd frontend && npm ci)
fi
(cd frontend && npm test)
