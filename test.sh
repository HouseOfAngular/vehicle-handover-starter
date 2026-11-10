#!/usr/bin/env bash
# Runs all backend and frontend tests. Exits with a non-zero code if anything fails.
# Both parts always run; a summary of both is printed at the end.
set -uo pipefail
cd "$(dirname "$0")"

source ./check-node.sh

backend_status=0
(cd backend && ./mvnw test) || backend_status=$?

frontend_status=0
(
  set -e
  cd frontend
  if [ ! -d node_modules ]; then
    npm ci
  fi
  npm test
) || frontend_status=$?

result() {
  if [ "$1" -eq 0 ]; then echo "PASSED"; else echo "FAILED (exit code $1)"; fi
}

echo
echo "================ test.sh summary ================"
echo "Backend tests:  $(result "$backend_status")"
echo "Frontend tests: $(result "$frontend_status")"
echo "================================================="

if [ "$backend_status" -ne 0 ] || [ "$frontend_status" -ne 0 ]; then
  exit 1
fi
