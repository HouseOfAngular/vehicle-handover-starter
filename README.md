# Dealership

Spring Boot backend and Angular frontend.

## Recruitment task

This repository is the starting point of a recruitment task. The task itself (in Polish)
is in [BRIEF.md](BRIEF.md): what to deliver, the API contract to keep, how to hand the work
in, and the transcript of the client meeting the requirements come from.

1. Check the requirements below and run `./test.sh`. It should pass before you change anything.
2. Read `BRIEF.md` in full, including the meeting transcript at the end.
3. Look at the vehicle module (`backend/.../vehicle/`, `frontend/src/app/vehicles/`). It shows
   how code is written in this project, and your work should follow it.
4. Do the task in the order of the deliverables listed in the brief, committing as you go.
5. Before handing in, make sure `./test.sh` passes.

Keep your solution private: do not push it to a public repository or a public fork.

## Requirements

- JDK 21
- Node.js 22.22.3 or newer 22.x (or 24.15+), with npm

`./run.sh` and `./test.sh` check the Node.js version first and stop with an error if it is
not supported. With nvm, `nvm install && nvm use` in the repository root picks the version
from `.nvmrc`.

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
(`npm test` in `frontend/`). Both parts always run and a summary of both is printed at the
end. The script exits with a non-zero code if any test fails.
