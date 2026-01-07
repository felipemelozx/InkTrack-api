# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

InkTrack API is a book reading management backend built with Java 17 and Spring Boot 4. It follows **Hexagonal Architecture** (Ports and Adapters) principles, maintaining strict separation between core business logic and infrastructure.

## Common Commands

### Building and Running
```bash
# Run the application
./mvnw spring-boot:run

# Build without tests
./mvnw clean package -DskipTests

# Build Docker image
docker build -t inktrack-api .
docker run -p 8081:8080 --env-file .env inktrack-api
```

### Testing
```bash
# Run all tests
./mvnw test

# Run tests with coverage (JaCoCo)
./mvnw clean verify

# Run specific test class
./mvnw test -Dtest=CreateBookUseCaseImplTest

# Run specific test method
./mvnw test -Dtest=CreateBookUseCaseImplTest#testExecuteWithValidData
```

### Code Quality
```bash
# Check code style
./mvnw checkstyle:check

# Run all quality checks
./mvnw clean verify
```

## Architecture: Hexagonal (Ports & Adapters)

The codebase strictly follows hexagonal architecture patterns:

### Core Domain (`src/main/java/com/inktrack/core/`)
**Contains business logic only - framework-independent**

- **domain/**: Pure domain entities (User, Book, Category, Note, ReadingSession)
- **gateway/**: Port interfaces defining contracts (e.g., UserGateway, BookGateway)
- **usecases/**: Business logic implementations organized by domain
- **exception/**: Domain-specific exceptions
- **utils/**: Domain utilities (validation, password checking)

**Critical**: Core domain has ZERO dependencies on Spring, JPA, or any framework.

### Infrastructure Layer (`src/main/java/com/inktrack/infrastructure/`)
**Contains all technical implementations**

- **controller/**: REST API endpoints (driving adapters)
- **dtos/**: Data Transfer Objects for API contracts (organized by domain)
- **entity/**: JPA entities for persistence
- **gateway/**: Implementations of core gateway interfaces (driven adapters)
- **mapper/**: Manual mapping between DTOs and domain objects
- **persistence/**: Spring Data JPA repository interfaces
- **security/**: JWT implementation, CORS, security filters
- **beans/**: Spring configuration and dependency injection

### Key Architectural Rules

1. **Domain Isolation**: Domain entities in `core/domain` never import from `infrastructure`
2. **Gateway Pattern**: Interfaces in `core/gateway`, implementations in `infrastructure/gateway`
3. **DTO Pattern**: API layer uses DTOs, mapped to domain objects via manual mappers
4. **Use Cases**: Business logic lives in `core/usecases` organized by domain (user/, book/, note/, etc.)

### Data Flow Example
```
Controller (receives DTO)
  → Mapper (DTO → Domain)
  → Use Case (business logic with Gateway interface)
  → Gateway Implementation (persistance via JPA)
  → Repository (Spring Data JPA)
```

## Code Quality Standards

The project enforces strict code quality via Checkstyle:

- **Max line length**: 120 characters
- **Max method length**: 50 lines
- **Max cyclomatic complexity**: 10
- **Max nested if depth**: 3
- **Naming conventions**: Strict Java naming conventions enforced

Run `./mvnw checkstyle:check` before committing.

## Database Migrations

Migrations are managed with Flyway. Place new migrations in:
```
src/main/resources/db/migration/
```

Naming convention: `V{version}__{description}.sql` (e.g., `V9__add_user_preferences.sql`)

Current version: V8

## Testing Strategy

Tests mirror the source structure and maintain >80% coverage:

- **Unit tests**: Domain objects, use cases, mappers
- **Integration tests**: Controllers (MockMvc), gateways, repositories
- **Test database**: H2 for unit tests, PostgreSQL for integration tests

**When adding new features:**
1. Write domain entity tests
2. Write use case tests
3. Write mapper tests
4. Write controller integration tests
5. Ensure coverage remains above 80%

## Security Architecture

- **JWT Authentication**: Access tokens (short-lived) + Refresh tokens (long-lived)
- **Password Requirements**: Enforced via `PasswordCheck` utility (min 8 chars, uppercase, lowercase, number, special char)
- **CORS**: Configured via environment variable (`FRONT_URL`)
- **Token validation**: JWT filter intercepts requests to protected endpoints

## Environment Variables

Required (see `.env.example`):
- `DB_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`
- `API_SECRET_KEY` (generate with: `openssl rand -base64 64`)
- `FRONT_URL` (for CORS)
- `APP_PORT` (default: 8081)
- `SERVER_CONTEXT_PATH` (default: /api/v1)
- `SPRING_PROFILES_ACTIVE` (dev/prod)

## Feature Development Workflow

When adding new features:

1. **Domain Layer** (`core/`):
   - Create/update domain entities
   - Define gateway interfaces (ports)
   - Implement use cases with business logic

2. **Infrastructure Layer** (`infrastructure/`):
   - Create JPA entities for persistence
   - Implement gateway interfaces
   - Create Spring Data JPA repositories
   - Create DTOs for API contracts
   - Implement mappers (DTO ↔ Domain)
   - Create REST controllers

3. **Tests** (`test/`):
   - Mirror the structure above
   - Test at each layer independently

## Conventional Commits

Follow this format for commit messages:
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `docs:` - Documentation changes
- `chore:` - Maintenance tasks

## CI/CD Pipeline

GitHub Actions workflows:
- `.github/workflows/test.yaml` - Automated tests
- `.github/workflows/sonarQube.yaml` - Quality analysis
- `.github/workflows/deploy.yaml` - Full CI/CD to production
- `.github/workflows/CodeQL.yaml` - Security analysis

Quality gates must pass (SonarCloud Grade A, >80% coverage) before merging to main.
