# Personal Task Manager (Eulerity take-home)

Java **17** + **Spring Boot 3.4** REST API with **H2** (in-memory), a minimal **static UI**, and an **OpenAI**-backed coaching endpoint. No Docker or external database required.

## Prerequisites

- [JDK 17](https://adoptium.net/) (`java -version` should report 17.x)
- Internet access (first run downloads Maven dependencies)

## Run the application

If you cloned a repository that contains this app as a subfolder, **change into the `task-manager` directory first** (where `pom.xml` and `mvnw` live):

```bash
cd task-manager
```

From this directory (`task-manager`):

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
./mvnw spring-boot:run
```

Then open:

- **UI:** [http://localhost:8080](http://localhost:8080)
- **H2 console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  - JDBC URL: `jdbc:h2:mem:taskdb`  
  - User: `sa`  
  - Password: *(leave empty)*

Cold start for reviewers is exactly this command plus JDK 17 (no prior `./mvnw install` required).

## Tests

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

Includes CRUD integration tests (`MockMvc`), mocked AI controller test, and service unit tests.

## OpenAI configuration

The API calls OpenAI only for **`POST /tasks/feedback`**. Without a key, that endpoint responds with **503** and a JSON `message`; the rest of the app (CRUD + UI list/create/delete) works normally.

Set **one** of these (environment variable or optional local `.env` file):

| Variable | Notes |
|----------|--------|
| `OPEN_AI_KEY` | Checked first |
| `OPENAI_API_KEY` | Fallback |

Optional:

| Variable | Default |
|----------|---------|
| `OPENAI_MODEL` | `gpt-4o-mini` (see `application.properties`) |

Copy `.env.example` to `.env` and fill in your key. **`.env` is gitignored—do not commit keys.**

On startup, `TaskManagerApplication` loads `.env` into JVM system properties when variables are not already set in the environment.

## API overview

### Task CRUD

| Method | Path | Description |
|--------|------|--------------|
| `POST` | `/tasks` | Create task (JSON body) |
| `GET` | `/tasks` | List all tasks |
| `GET` | `/tasks/{id}` | Get one task |
| `PUT` | `/tasks/{id}` | Replace task fields |
| `DELETE` | `/tasks/{id}` | Delete task |

Task JSON fields: `title` (required), `description` (optional), `dueDate` (`yyyy-MM-DD`), `priority` (`LOW` \| `MEDIUM` \| `HIGH`), `status` (`TODO` \| `IN_PROGRESS` \| `DONE`).

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

Example **`curl`** (with key in environment):

```bash
export OPENAI_API_KEY="sk-..."
curl -s -X POST http://localhost:8080/tasks/feedback -H "Content-Type: application/json" -d "{}"
```

## Project layout (high level)

- `src/main/java/.../controller` — REST controllers  
- `src/main/java/.../service` — business logic + OpenAI coach  
- `src/main/java/.../model` — JPA entities  
- `src/main/resources/static/index.html` — reviewer UI  
- `src/main/resources/application.properties` — H2 + OpenAI placeholders (**no secrets**)  

## Design choices

- **H2 in-memory** with `ddl-auto=create-drop` per assessment constraints.  
- **Coach feedback** (`/tasks/feedback`) instead of NL→task suggestion: still satisfies “one AI endpoint returning structured JSON.”  
- **Dotenv** optional convenience for local keys; production deployment is out of scope.
