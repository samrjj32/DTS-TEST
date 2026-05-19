# HMCTS Dev Test Backend — Task API

Spring Boot REST API for caseworker task management.

## Prerequisites

- **Java 21** (Gradle will auto-download it on first build if missing — see `settings.gradle`)
- Do **not** use `yarn` in this folder — the backend is Java/Gradle only

## Run locally

```bash
./gradlew bootRun
```

First run may take a few minutes while Gradle downloads Java 21.

Service: http://localhost:4000

### If Gradle says Java 21 is not found

**Option A (automatic):** ensure `settings.gradle` exists (Foojay toolchain resolver) and run `./gradlew bootRun` again.

**Option B (Homebrew):**

```bash
brew install openjdk@21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew bootRun
```

## API documentation

Interactive docs: **http://localhost:4000/swagger-ui.html**

OpenAPI spec: **http://localhost:4000/v3/api-docs**

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/tasks` | Create a task |
| `GET` | `/api/tasks` | List all tasks |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `PATCH` | `/api/tasks/{id}/status` | Update task status |
| `DELETE` | `/api/tasks/{id}` | Delete a task |

### Create task body

```json
{
  "title": "Review case documents",
  "description": "Optional description",
  "status": "PENDING",
  "dueDateTime": "2026-06-15T14:00:00"
}
```

### Update status body

```json
{
  "status": "IN_PROGRESS"
}
```

## Database

Uses H2 in-memory database (`jdbc:h2:mem:tasks`). Data persists for the lifetime of the application process.

H2 console (development): http://localhost:4000/h2-console

- JDBC URL: `jdbc:h2:mem:tasks`
- Username: `sa`
- Password: *(empty)*

## Tests

```bash
./gradlew test          # unit tests
./gradlew integration   # integration tests
./gradlew build         # full build with all checks
```

## Tech stack

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- SpringDoc OpenAPI
- JUnit 5, Mockito
