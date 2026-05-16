# Personal Task Manager (Eulerity take-home)

Java **17** + **Spring Boot 3.4** REST API with **H2** (in-memory), a minimal **static UI**, and an **OpenAI**-backed coaching endpoint. No Docker or external database required.

This repository’s **Spring Boot application** lives in the **`task-manager/`** subdirectory (`pom.xml`, `mvnw`, and source are there). Employer instructions are in **`candidate_readme.md`** at this same root.

## Prerequisites

- [JDK 17](https://adoptium.net/) (`java -version` should report 17.x)
- Internet access (first run downloads Maven dependencies)

## Clone and run (reviewer cold start)

1. Clone the repository and open a terminal at the **repository root** (the folder that contains `task-manager/` and this `README.md`).
2. Enter the app directory (where Maven Wrapper and `pom.xml` live):

```bash
cd task-manager
```

3. Start the API (single command from that directory):

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
./mvnw spring-boot:run
```

No prior `./mvnw install` is required beyond JDK 17 and network access.

Then open:

- **UI:** [http://localhost:8080](http://localhost:8080)
- **H2 console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  - JDBC URL: `jdbc:h2:mem:taskdb`  
  - User: `sa`  
  - Password: *(leave empty)*

## Tests

Run from **`task-manager/`** (same as above: `cd task-manager` from repo root):

**Windows**

```powershell
.\mvnw.cmd test
```

**macOS / Linux**

```bash
./mvnw test
```

Includes CRUD integration tests (`MockMvc`), mocked AI controller test, and service unit tests.

## OpenAI configuration

The API calls OpenAI only for **`POST /tasks/feedback`**. Without a key, that endpoint responds with **503** and a JSON `message`; the rest of the app (CRUD + UI list/create/delete) works normally.

Set **one** of these (environment variable or optional local **`.env`** file next to `pom.xml`):

| Variable | Notes |
|----------|--------|
| `OPEN_AI_KEY` | Checked first |
| `OPENAI_API_KEY` | Fallback |

Optional:

| Variable | Default |
|----------|---------|
| `OPENAI_MODEL` | `gpt-4o-mini` (see `task-manager/src/main/resources/application.properties`) |

After `cd task-manager`, copy the template and edit the new file (never commit real keys):

**macOS / Linux**

```bash
cp .env.example .env
```

**Windows (PowerShell)**

```powershell
Copy-Item .env.example .env
```

Then set `OPEN_AI_KEY` or `OPENAI_API_KEY` inside `.env`.

On startup, `TaskManagerApplication` loads **`task-manager/.env`** into JVM system properties when variables are not already set in the environment (working directory should be **`task-manager/`** when using `spring-boot:run`, which matches the steps above).

## API overview

### Task CRUD

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/tasks` | Create task (JSON body) |
| `GET` | `/tasks` | List all tasks |
| `GET` | `/tasks/{id}` | Get one task |
| `PUT` | `/tasks/{id}` | Replace task fields |
| `DELETE` | `/tasks/{id}` | Delete task |

Task JSON fields: `title` (required), `description` (optional), `dueDate` (`yyyy-MM-dd`), `priority` (`LOW` \| `MEDIUM` \| `HIGH`), `status` (`TODO` \| `IN_PROGRESS` \| `DONE`).

### AI-powered endpoint

**`POST /tasks/feedback`**

Sends the **current list of tasks** (from the database) to OpenAI and returns a short prioritization paragraph.

**Request:** empty JSON body is accepted (tasks are loaded server-side).

```http
POST /tasks/feedback HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{}
```

**Response** (`200 OK`):

```json
{
  "feedback": "Plain-language coaching paragraph based on your tasks..."
}
```

If there are no tasks yet, the server returns **`200`** with a canned encouragement message (no OpenAI call).

**Errors (JSON `message` field):**

- **503** — API key not configured  
- **502** — OpenAI HTTP error or unparsable response  

Example **`curl`** (run while the server is up; with key in environment):

```bash
export OPENAI_API_KEY="sk-..."
curl -s -X POST http://localhost:8080/tasks/feedback -H "Content-Type: application/json" -d "{}"
```

## Project layout (high level)

| Path | Role |
|------|------|
| `task-manager/pom.xml` | Maven build and dependencies |
| `task-manager/mvnw`, `task-manager/mvnw.cmd` | Maven Wrapper |
| `task-manager/src/main/java/.../controller` | REST controllers |
| `task-manager/src/main/java/.../service` | Business logic + OpenAI coach |
| `task-manager/src/main/java/.../model` | JPA entities |
| `task-manager/src/main/resources/static/index.html` | Reviewer UI |
| `task-manager/src/main/resources/application.properties` | H2 + OpenAI placeholders (**no secrets**) |
| `candidate_readme.md` | Original take-home specification |
| `transcripts/` | AI-assisted build session exports (see below) |

## AI development transcripts

Per the take-home instructions, conversation transcripts are included alongside this project. They live in the **`transcripts/`** folder at this **repository root** (sibling of **`task-manager/`**, not inside `task-manager`).

## Design choices

- **H2 in-memory** with `ddl-auto=create-drop` per assessment constraints.  
- **Coach feedback** (`/tasks/feedback`) instead of NL→task suggestion: still satisfies “one AI endpoint returning structured JSON.”  
- **Dotenv** optional convenience for local keys; production deployment is out of scope.
