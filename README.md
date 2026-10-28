# AI-Powered Customer Support Chatbot

A backend-focused customer support chatbot built with **Java, Spring Boot, Spring Security, JWT, MySQL, and OpenRouter**. It supports authenticated users, private conversations, persistent message history, and AI-generated responses that can use recent messages as conversational context.

> **Project status:** Authentication, conversation management, message persistence, and OpenRouter chat integration have been implemented and manually tested. Automated tests and RAG are not included in the current implementation.

## Features

### Authentication and security
- User registration and login.
- Password hashing with BCrypt.
- JWT-based authentication for protected endpoints.
- Authenticated user profile endpoint.
- Conversation ownership checks to prevent users from accessing or modifying another user's conversations.
- Safe user response objects that avoid returning password hashes.

### Conversation management
- Create a conversation.
- List the authenticated user's conversations with pagination.
- Retrieve a conversation by ID, subject to ownership checks.
- Rename a conversation.
- Delete a conversation and its associated messages.
- Validate conversation titles and pagination parameters.
- Automatically generate a conversation title from the first user message when the title is still `New Conversation`.

### AI chat and message history
- Send a message to a conversation and receive an AI-generated response.
- Persist user and assistant messages in MySQL.
- Retrieve messages in chronological order.
- Include recent conversation messages in the prompt to support multi-turn context.
- Limit the context sent to the model to the latest 10 messages.
- Use a system prompt that asks the assistant to answer clearly and avoid inventing company-specific information.
- Call an OpenRouter chat-completions endpoint using a configurable model.

### Error handling and validation
- Centralized exception handling with structured JSON errors.
- Validation for blank or excessively long conversation titles and message content.
- Appropriate responses for invalid credentials, validation failures, missing conversations, and AI-provider failures.
- Ownership checks are applied before conversation operations.

## Technology stack

- **Language:** Java 21 (configured in `pom.xml`; use a compatible JDK)
- **Framework:** Spring Boot 4.1.2
- **Web:** Spring MVC
- **Security:** Spring Security, JWT (`jjwt`)
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** MySQL 8
- **Validation:** Jakarta Bean Validation
- **AI provider:** OpenRouter API
- **Build tool:** Maven
- **Developer tools:** IntelliJ IDEA, Git, Postman or another REST client

## Architecture overview

```text
REST Client
    |
    v
Spring MVC Controllers
    |
    +--> Authentication / JWT Filter
    |
    v
Services
    |
    +--> User and Conversation ownership checks
    +--> Message history and persistence
    +--> OpenRouter AI service
    |
    v
Spring Data JPA Repositories
    |
    v
MySQL Database

OpenRouter AI service ---- HTTPS ----> OpenRouter Chat Completions API
```

The application stores users, conversations, and messages in MySQL. OpenRouter generates the assistant response. Recent messages from the same conversation are sent as context; a separate RAG document-ingestion and vector-search pipeline is **not** currently part of this project.

## Prerequisites

- A compatible JDK (Java 21 is configured in the Maven project).
- MySQL 8 running locally or on a reachable database host.
- Maven, or the Maven wrapper if included in the repository.
- An OpenRouter API key and access to the configured model.
- Git (optional, for cloning and version control).

## Configuration

Create the `support_chatbot` database in MySQL if it does not already exist:

```sql
CREATE DATABASE support_chatbot;
```

Configure `src/main/resources/application.properties` to use environment variables for secrets. Example:

```properties
spring.application.name=supportchatbot

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/support_chatbot}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

openrouter.api-key=${OPENROUTER_API_KEY}
openrouter.model=${OPENROUTER_MODEL:openai/gpt-oss-20b:free}
openrouter.base-url=https://openrouter.ai/api/v1
```

These are example environment-variable placeholders. If your current `application.properties` uses different property names or defaults, keep the names expected by your existing code.

Set the environment variables in your terminal or IDE run configuration before starting the application:

- `DB_URL` — JDBC URL for MySQL (optional if using the default above).
- `DB_USERNAME` — MySQL username (optional if using the default above).
- `DB_PASSWORD` — MySQL password.
- `JWT_SECRET` — a strong, private signing secret suitable for your JWT configuration.
- `OPENROUTER_API_KEY` — your OpenRouter API key.
- `OPENROUTER_MODEL` — optional; defaults to the model shown above if your configuration supports this placeholder.

**Security notes**
- Never commit real API keys, database passwords, or JWT secrets.
- Do not paste secrets into issue reports or public repositories.
- Use a strong JWT secret compatible with the signing algorithm configured in the application.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development; use a managed schema migration strategy for production.
- Consider disabling SQL logging and enabling appropriate production logging before deployment.

## Run locally

1. Start MySQL and create the `support_chatbot` database.
2. Configure the required environment variables.
3. Open the project in IntelliJ IDEA or a terminal.
4. Run the Spring Boot application using the Maven wrapper if available:

   **Windows PowerShell**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **macOS/Linux**
   ```bash
   ./mvnw spring-boot:run
   ```

   If the Maven wrapper is not included, run:
   ```bash
   mvn spring-boot:run
   ```

5. The server normally starts on `http://localhost:8080`, unless `server.port` is configured differently.

Check the application startup logs if it cannot connect to MySQL or the OpenRouter API. Verify that environment variables are available to the process running Spring Boot.

## API overview

All protected endpoints require a valid JWT in the request header:

```http
Authorization: Bearer <your-jwt-token>
```

The endpoint list below reflects the implemented routes discussed during development. Confirm exact DTO field names against the source code if they have changed.

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a user | Public |
| `POST` | `/api/auth/login` | Log in and obtain a token | Public |
| `GET` | `/api/users/me` | Get the authenticated user's profile | Required |
| `POST` | `/api/conversations` | Create a conversation | Required |
| `GET` | `/api/conversations?page=0&size=10` | List conversations with pagination | Required |
| `GET` | `/api/conversations/{id}` | Get one owned conversation | Required |
| `PATCH` | `/api/conversations/{id}` | Rename an owned conversation | Required |
| `DELETE` | `/api/conversations/{id}` | Delete an owned conversation and its messages | Required |
| `GET` | `/api/conversations/{id}/messages` | Retrieve conversation messages | Required |
| `POST` | `/api/conversations/{id}/messages` | Send a message and get an AI response | Required |

### Typical request examples

Create a conversation:

```http
POST /api/conversations
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "title": "Order Support"
}
```

Rename a conversation:

```http
PATCH /api/conversations/4
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "title": "Password Reset Help"
}
```

Send a message:

```http
POST /api/conversations/4/messages
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "content": "How can I reset my password?"
}
```

The exact response body for authentication and message endpoints depends on the response DTOs currently in the project.

## Pagination and validation

- Conversation listing defaults to page `0` and size `10`.
- Page values must be non-negative.
- Page size must be between `1` and `50`.
- Conversation titles cannot be blank and have a maximum length of 100 characters.
- Message content cannot be blank and has a maximum length of 10,000 characters.
- A user cannot rename, retrieve, or delete a conversation owned by another user.

## Data model

### User
Stores user account information, including the name, email, password hash, and creation timestamp.

### Conversation
Stores the conversation title, creation timestamp, and owner (`user_id`).

### Message
Stores the message content, role (`USER` or `ASSISTANT`), creation timestamp, and associated conversation (`conversation_id`). The message table is named `messages`.

## AI response flow

1. The client sends a message to an authenticated conversation.
2. The backend checks that the conversation belongs to the current user.
3. The user message is persisted.
4. Recent messages from that conversation are loaded and mapped to user/assistant roles.
5. The backend calls OpenRouter's chat-completions API using the configured model and system prompt.
6. The assistant response is persisted and returned to the client.

Conversation history is currently used as context. This is not the same as Retrieval-Augmented Generation (RAG): RAG would additionally ingest knowledge sources, create embeddings, retrieve relevant document chunks, and include those chunks in the prompt.

## Error responses

The global exception handler returns structured errors for common cases. Examples observed during development include:

Validation failure:

```json
{
  "error": "VALIDATION_ERROR",
  "message": "title: Conversation title is required"
}
```

Conversation not found or not owned by the current user:

```json
{
  "error": "NOT_FOUND",
  "message": "Conversation not found"
}
```

Exact status codes and messages should be verified against the current exception-handler implementation.

## Manual verification completed

The following behaviors were manually exercised during development:

- User registration and login with JWT authentication.
- Protected user profile access.
- Creating, listing, retrieving, renaming, and deleting conversations.
- Pagination and invalid pagination input handling.
- Title validation for blank and over-100-character values.
- Persisting user and assistant messages.
- Retrieving messages in chronological order.
- Using recent messages as context in a conversation.
- Keeping context isolated between separate conversations.
- Rejecting a rename attempt when authenticated as a different user.
- Receiving a normal AI response after configuring a specific OpenRouter model rather than the generic free router.

These are manual checks, not a claim that an automated test suite currently passes.

## Current limitations and potential improvements

- Automated unit and integration tests are not yet completed.
- RAG and document upload/search are not included in the current implementation.
- Production deployment, monitoring, rate limiting, and comprehensive operational logging are not documented as completed.
- Add request rate limits and sensible limits for AI token usage.
- Consider retries/timeouts and clearer handling for provider rate limits and quota exhaustion.
- Consider a database migration tool such as Flyway or Liquibase instead of relying on Hibernate schema updates.
- Add integration tests for authentication, conversation ownership, validation, and message flows.
- Add RAG only if the product needs answers grounded in a private knowledge base.

## Development status

Implemented:
- JWT-based authentication and protected endpoints.
- User-owned conversation CRUD operations.
- Pagination and request validation.
- Persistent user/assistant message history.
- OpenRouter integration and recent-message conversational context.
- Centralized error handling.

Not yet completed:
- Automated test coverage.
- RAG/document ingestion and retrieval.
- Production deployment and production hardening.

## License

Add a license here if you intend to distribute this project publicly.
