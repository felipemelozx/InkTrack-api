<div align="center">

# InkTrack API

**A comprehensive book reading management API**

A powerful RESTful API backend designed to help book lovers track their reading journey. Manage your personal library,
monitor reading progress, take notes, record reading sessions, and analyze your reading habits with detailed metrics and
analytics.

Built with Java 17 and Spring Boot 4, following Clean Architecture principles, JWT security, and production-ready DevOps
practices.

</div>

### Technologies & Build

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

### Quality & CI/CD

[![Build](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com/felipemelozx/InkTrack-api/actions)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality%20Gate-brightgreen.svg)](https://sonarcloud.io/dashboard?id=felipemelozx_InkTrack-api)
[![Coverage](https://img.shields.io/badge/Coverage-%3E80%25-brightgreen.svg)](https://sonarcloud.io/dashboard?id=felipemelozx_InkTrack-api)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🚀 Demo

- **Application**: Available upon deployment

## ✨ Key Features

- ✅ **Secure Authentication**: JWT with Access + Refresh tokens
- ✅ **Book Management**: Complete CRUD operations for your reading library
- ✅ **Reading Progress**: Track pages read, completion status, and reading sessions
- ✅ **Notes System**: Create and manage notes for each book
- ✅ **Categories**: Organize your books by categories
- ✅ **Metrics & Analytics**: Comprehensive reading statistics and evolution tracking
- ✅ **Performance**: Optimized queries with pagination, filtering, and sorting
- ✅ **Security**: CORS configuration, input validation, and secure password handling
- ✅ **DevOps**: Automated CI/CD pipelines with GitHub Actions
- ✅ **Quality**: Code quality analysis with SonarCloud and JaCoCo

## 🏗️ Architecture

The project adopts **Hexagonal Architecture** (Ports and Adapters) to promote low coupling and high cohesion, following
Clean Architecture principles.

**Key Concepts:**

- **Core Domain**: Business rules and entities, independent of frameworks
- **Ports (Gateways)**: Interfaces that define contracts between core and outside world
    - **Driving/Primary Ports**: Operations triggered from outside (use cases)
    - **Driven/Secondary Ports**: Operations the core needs (repositories, services)
- **Adapters**: Implementations that plug into ports
    - **Driving Adapters**: Controllers, REST API
    - **Driven Adapters**: Database repositories, external services
- **Infrastructure**: All technical details (Spring, JPA, JWT, etc)

**Technical Decisions:**

- **DTOs**: Complete isolation of domain model from exposed API
- **Manual Mappers**: Lightweight mapping between DTOs and domain objects
- **Flyway**: Database version control and migration management
- **Gateway Pattern**: Ports (interfaces) in core, implementations in infrastructure

### Project Structure

```
src/main/java/com/inktrack/
├── core/                          # Domain logic
│   ├── domain/                    # Domain entities (User, Book, Category, Note, ReadingSession)
│   ├── exception/                 # Custom exceptions
│   ├── gateway/                   # Gateway interfaces
│   ├── usecases/                  # Use cases (business logic)
│   └── utils/                     # Utility classes
├── infrastructure/                # Implementation details
│   ├── controller/                # REST API controllers
│   ├── dto/                       # Data Transfer Objects
│   ├── entity/                    # JPA entities
│   ├── gateway/                   # Gateway implementations
│   ├── mapper/                    # DTO/Domain mappers
│   ├── persistence/               # Repository interfaces
│   ├── security/                  # Security configuration
│   └── utils/                     # Infrastructure utilities
└── InkTrackApplication.java       # Main application class
```

## 🛠️ Tech Stack

### Core

[![Java](https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-brightgreen.svg?style=for-the-badge&logo=spring-security)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-brightgreen.svg?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-data-jpa)

### Database & Migration

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![H2](https://img.shields.io/badge/H2-Database-blue.svg?style=for-the-badge&logo=h2database)](https://www.h2database.com/)
[![Flyway](https://img.shields.io/badge/Flyway-Migration-red.svg?style=for-the-badge&logo=flyway)](https://flywaydb.org/)

### Security

[![JWT](https://img.shields.io/badge/JWT-java--jwt%204.4.0-red.svg?style=for-the-badge&logo=JSON%20web%20tokens)](https://github.com/auth0/java-jwt)

### Build & Testing

[![Maven](https://img.shields.io/badge/Maven-3.9-red.svg?style=for-the-badge&logo=apache-maven)](https://maven.apache.org/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5-green.svg?style=for-the-badge&logo=junit5)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-5.x-orange.svg?style=for-the-badge&logo=mockito)](https://site.mockito.org/)

### Code Quality

[![SonarQube](https://img.shields.io/badge/SonarQube-Analysis-blue.svg?style=for-the-badge&logo=sonarqube)](https://www.sonarqube.org/)
[![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-brightgreen.svg?style=for-the-badge&logo=ja%2FCoCo)](https://www.jacoco.org/jacoco/)
[![Checkstyle](https://img.shields.io/badge/Checkstyle-10.x-orange.svg?style=for-the-badge)](https://checkstyle.sourceforge.io/)

### DevOps

[![Docker](https://img.shields.io/badge/Docker-Multi--stage%20Build-blue.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-lightgrey.svg?style=for-the-badge&logo=github-actions)](https://github.com/features/actions)

## ▶️ How to Run

### Prerequisites

- Docker and Docker Compose installed
- Java 17+ and Maven (optional, if not using the wrapper)

### Quick Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/felipemelozx/InkTrack-api.git
   cd InkTrack-api
   ```

2. **Configure Environment Variables**
   ```bash
   cp .env.example .env
   # Edit the .env file with your credentials (DB, JWT Secret, etc.)
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Docker:
   ```bash
   docker build -t inktrack-api .
   docker run -p 8081:8080 --env-file .env inktrack-api
   ```

4. **Check API health**
   ```bash
   curl http://localhost:8081/api/v1/actuator/health
   ```

### Environment Variables

Required variables are listed in `.env.example` in the project root.

**Main variables:**

- `DB_URL`: PostgreSQL connection URL
- `POSTGRES_USER`: Database user
- `POSTGRES_PASSWORD`: Database password
- `POSTGRES_DB`: Database name
- `API_SECRET_KEY`: Secret key for token signing (Generate with `openssl rand -base64 64`)
- `FRONT_URL`: Frontend URL for CORS configuration
- `APP_PORT`: Application port (default: 8081)
- `SERVER_CONTEXT_PATH`: API context path (default: /api/v1)
- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)

## 📚 API Documentation

Once the project is running locally, access the API at:

```
http://localhost:8081/api/v1
```

### Authentication

#### Register User

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "StrongPass123!"
}
```

Success Response:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "errors": [],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "StrongPass123!"
}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "errors": [],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Refresh Token

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "errors": [],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Books

#### Create Book

```http
POST /api/v1/books
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "totalPages": 464,
  "categoryId": 1
}
```

Success Response:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": 1,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    "category": {
      "id": 1,
      "name": "Technology"
    },
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "totalPages": 464,
    "pagesRead": 0,
    "progress": 0,
    "createdAt": "2024-01-15T10:35:00Z",
    "updatedAt": "2024-01-15T10:35:00Z"
  },
  "errors": [],
  "timestamp": "2024-01-15T10:35:00Z"
}
```

#### Get All Books

```http
GET /api/v1/books?page=0&size=10&sort=title,asc
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "pageSize": 10,
    "totalPages": 1,
    "currentPage": 0,
    "data": [...]
  },
  "errors": [],
  "timestamp": "2024-01-15T10:35:00Z"
}
```

#### Get Book by ID

```http
GET /api/v1/books/1
Authorization: Bearer {accessToken}
```

#### Update Book

```http
PUT /api/v1/books/1
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
  "author": "Robert C. Martin",
  "totalPages": 464,
  "categoryId": 1
}
```

#### Delete Book

```http
DELETE /api/v1/books/1
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 204 No Content
```

### Notes

#### Create Note

```http
POST /api/v1/books/1/notes
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "content": "Excellent chapter on code formatting!"
}
```

Success Response:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": 1,
    "bookId": 1,
    "content": "Excellent chapter on code formatting!",
    "createdAt": "2024-01-15T10:40:00Z",
    "updatedAt": "2024-01-15T10:40:00Z"
  },
  "errors": [],
  "timestamp": "2024-01-15T10:40:00Z"
}
```

#### Get Book Notes

```http
GET /api/v1/books/1/notes
Authorization: Bearer {accessToken}
```

#### Update Note

```http
PUT /api/v1/books/1/notes/1
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "content": "Updated: Excellent chapter on code formatting and naming conventions!"
}
```

#### Delete Note

```http
DELETE /api/v1/books/1/notes/1
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 204 No Content
```

### Reading Sessions

#### Create Reading Session

```http
POST /api/v1/books/1/reading-sessions
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "minutes": 45,
  "pagesRead": 30
}
```

Success Response:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": 1,
    "bookId": 1,
    "minutes": 45,
    "pagesRead": 30,
    "sessionDate": "2024-01-15T10:45:00Z"
  },
  "errors": [],
  "timestamp": "2024-01-15T10:45:00Z"
}
```

#### Get Book Reading Sessions

```http
GET /api/v1/books/1/reading-sessions
Authorization: Bearer {accessToken}
```

#### Update Reading Session

```http
PUT /api/v1/books/1/reading-sessions/1
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "minutes": 60,
  "pagesRead": 40
}
```

#### Delete Reading Session

```http
DELETE /api/v1/books/1/reading-sessions/1
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 204 No Content
```

### Categories

#### Get All Categories

```http
GET /api/v1/categories
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": [
    {
      "id": 1,
      "name": "Technology",
      "createdAt": "2024-01-15T10:50:00Z"
    },
    {
      "id": 2,
      "name": "Fiction",
      "createdAt": "2024-01-15T10:50:00Z"
    }
  ],
  "errors": [],
  "timestamp": "2024-01-15T10:50:00Z"
}
```

#### Get Category by ID

```http
GET /api/v1/categories/1
Authorization: Bearer {accessToken}
```

### Metrics

#### Get General Metrics

```http
GET /api/v1/metrics
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "totalBooks": 15,
    "averageProgress": 65.5,
    "totalPagesRemaining": 1500,
    "estimatedDaysToFinish": 30
  },
  "errors": [],
  "timestamp": "2024-01-15T11:00:00Z"
}
```

#### Get Reading Session Metrics

```http
GET /api/v1/metrics/reading-sessions
Authorization: Bearer {accessToken}
```

#### Get Books by Category

```http
GET /api/v1/metrics/categories
Authorization: Bearer {accessToken}
```

#### Get Reading Evolution

```http
GET /api/v1/metrics/evolution?period=30d
Authorization: Bearer {accessToken}
```

Success Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "period": "30d",
    "data": [
      {
        "date": "2024-01-01",
        "pagesRead": 45
      },
      {
        "date": "2024-01-02",
        "pagesRead": 60
      }
    ]
  },
  "errors": [],
  "timestamp": "2024-01-15T11:00:00Z"
}
```

**Valid period values:** `30d`, `3m`, `6m`, `12m`

### Error Response Format

All errors follow this standard format:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "success": false,
  "message": "Validation errors",
  "data": null,
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid"
    },
    {
      "field": "password",
      "message": "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Authentication:**
Most endpoints require a Bearer Token. Obtain one by logging in at `POST /api/v1/auth/login` and include it in the
Authorization header:

```
Authorization: Bearer {accessToken}
```

## 🧪 Tests

The project uses unit and integration tests to ensure quality.

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw clean verify
```

**Current Metrics:**

- Code Coverage: > 80%
- SonarQube Quality: Grade A

## ⚙️ CI/CD Pipeline

The project uses GitHub Actions for automation:

- **Build & Test**: Compilation and test suite execution
- **Quality Gate**: Static analysis with SonarCloud
- **Docker**: Multi-stage build and push to Docker Hub
- **Deploy**: Automated deployment to VPS on merge to main branch

**Workflows:**

- `.github/workflows/test.yaml` - Automated tests on push/PR
- `.github/workflows/sonarQube.yaml` - SonarCloud analysis on PR
- `.github/workflows/deploy.yaml` - Full CI/CD pipeline for main branch
- `.github/workflows/CodeQL.yaml` - Security analysis

## 🔧 Development

### Code Style

The project uses Checkstyle to enforce code quality standards. Run:

```bash
./mvnw checkstyle:check
```

### Database Migrations

Migrations are managed with Flyway. Place new migration files in:

```
src/main/resources/db/migration/
```

Naming convention: `V{version}__{description}.sql`

### Adding New Features

1. Create domain entities in `core/domain`
2. Create use cases in `core/usecases`
3. Implement repositories in `infrastructure/persistence`
4. Create DTOs in `infrastructure/dto`
5. Implement controllers in `infrastructure/controller`
6. Write tests for all layers

## 🤝 Contributing

1. Fork the project
2. Create a branch for your feature (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

**Commit Convention:**
Follow conventional commits:

- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `docs:` - Documentation changes
- `chore:` - Maintenance tasks

## 👨‍💻 Author

**Felipe Melo**
Backend Engineer | Java & Spring Boot Specialist

- [GitHub](https://github.com/felipemelozx)
- [LinkedIn](https://linkedin.com/in/felipemelozx)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <b>Built with ❤️ for book lovers</b>
</div>
