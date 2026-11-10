# Dealership

Spring Boot backend and Angular frontend.

## Requirements

- JDK 21
- Node.js 22.22.3 or newer 22.x (or 24.15+), with npm

Maven is not required: the backend uses the Maven wrapper.

## Running

```bash
./run.sh
```

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api (the frontend dev server proxies `/api` to it)

The database is an in-memory H2 instance, recreated and seeded on every start.
Stop both applications with `Ctrl+C`.

## Tests

```bash
./test.sh
```

Runs the backend tests (`./mvnw test` in `backend/`) and the frontend tests
(`npm test` in `frontend/`). The script exits with a non-zero code if any test fails.
