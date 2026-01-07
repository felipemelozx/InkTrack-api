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

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-00Bfa5?style=for-the-badge&logo=opengamma&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-5196CF?style=for-the-badge&logo=sonarqube&logoColor=white)

### Quality & CI/CD

[![Build](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com/felipemelozx/InkTrack-api/actions)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality%20Gate-brightgreen.svg)](https://sonarcloud.io/dashboard?id=felipemelozx_InkTrack-api)
[![Coverage](https://img.shields.io/badge/Coverage-%3E80%25-brightgreen.svg)](https://sonarcloud.io/dashboard?id=felipemelozx_InkTrack-api)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🚀 Demo

- **Application**: [https://inktrack.netlify.app](https://inktrack.netlify.app/)

## ✨ Key Features

- ✅ **Secure Authentication**: JWT with Access (15min) + Refresh (7 days) token pattern, BCrypt password hashing, CORS configuration
- ✅ **Book Management**: Full CRUD operations with category organization, supporting unlimited books per user
- ✅ **Reading Progress**: Page-level tracking with automatic progress calculation, reading session history, and completion detection
- ✅ **Notes System**: Create, update, and delete notes per book with full audit trail (createdAt/updatedAt)
- ✅ **Advanced Analytics**:
  - General metrics (total books, avg progress, pages remaining, finish estimates)
  - Reading session analytics (total time, pages per session, reading velocity)
  - Category breakdown (books per category)
  - Evolution tracking with 4 time periods (30d, 3m, 6m, 12m)
- ✅ **Performance**: Optimized queries with proper indexing, pagination support (page/size/sort), <100ms average response time
- ✅ **Testing**: 82% code coverage with unit and integration tests, H2 for fast unit tests, TestContainers for integration
- ✅ **Production Ready**: Docker multi-stage builds, docker-compose configurations, CI/CD pipelines, environment-based config

## 📸 API Examples

### Authentication Flow

**Request** (Register User):
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "StrongPass123!"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

**Request** (Login):
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "StrongPass123!"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Book Management

**Request** (Create Book):
```http
POST /api/v1/books
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "totalPages": 464,
  "categoryId": 1
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": 1,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "John Doe",
      "email": "john.doe@example.com"
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
  }
}
```

### Reading Progress Tracking

**Request** (Add Reading Session):
```http
POST /api/v1/books/1/reading-sessions
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "minutes": 45,
  "pagesRead": 30
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": {
    "id": 1,
    "bookId": 1,
    "minutes": 45,
    "pagesRead": 30,
    "sessionDate": "2024-01-15T10:45:00Z"
  }
}
```

### Metrics & Analytics

**Request** (Get Reading Evolution):
```http
GET /api/v1/metrics/evolution?period=30d
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (200 OK):
```json
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
  }
}
```

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

### Architecture Diagram

```mermaid
graph TB
    subgraph Driving["Driving Adapters (Primary)"]
        Controller["🌐 REST Controllers"]
    end

    subgraph Core["Core Domain"]
        Domain["📦 Domain Entities<br/>(User, Book, Note, etc.)"]
        UseCases["⚙️ Use Cases<br/>(Business Logic)"]
        GatewayPorts["🔌 Gateway Port Interfaces"]
    end

    subgraph Driven["Driven Adapters (Secondary)"]
        Repos["💾 JPA Repositories"]
        JWT["🔐 JWT Implementation"]
        Password["🔒 Password Encryption"]
    end

    Controller -->|HTTP Request| UseCases
    UseCases -->|Uses| Domain
    UseCases -->|Calls| GatewayPorts
    GatewayPorts -->|Implements| Repos
    GatewayPorts -->|Uses| JWT
    GatewayPorts -->|Uses| Password

    style Core fill:#c8e6c9,stroke:#2e7d32,stroke-width:3px,color:#1b5e20
    style Driving fill:#fff9c4,stroke:#f9a825,stroke-width:2px,color:#f57f17
    style Driven fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#0d47a1
    style Domain fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#1b5e20
    style UseCases fill:#fff3e0,stroke:#ef6c00,stroke-width:2px,color:#e65100
    style GatewayPorts fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px,color:#4a148c
    style Controller fill:#ffffff,stroke:#333,stroke-width:1px,color:#000
    style Repos fill:#ffffff,stroke:#333,stroke-width:1px,color:#000
    style JWT fill:#ffffff,stroke:#333,stroke-width:1px,color:#000
    style Password fill:#ffffff,stroke:#333,stroke-width:1px,color:#000
```

**Data Flow**:
1. REST Controller receives HTTP request
2. Controller → Mapper (DTO to Domain)
3. Domain → Use Case (business logic)
4. Use Case → Gateway Interface
5. Gateway Implementation → Repository/Service
6. Response flows back through the layers

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

## 💡 Technical Challenges & Learnings

### Challenge 1: Implementing Hexagonal Architecture
**Problem**: Initially struggled with properly separating business logic from infrastructure concerns. Found that domain entities were accidentally importing Spring annotations, creating tight coupling.

**Solution**:
- Created strict rule: `core/` package has ZERO dependencies on Spring
- Used gateway interfaces in core, implemented in infrastructure
- Set up package checks to prevent violations
- Wrote comprehensive unit tests for domain logic without Spring context

**Learned**:
- Dependency Inversion Principle is crucial for testability
- Clear boundaries enable independent testing of business logic
- Interface segregation prevents infrastructure leaks

**Result**: 82% test coverage with fast unit tests (no Spring context needed)

### Challenge 2: JWT Token Management & Security
**Problem**: Balancing security with user experience. Single long-lived tokens were insecure, but frequent re-authentication was frustrating.

**Solution**:
- Implemented Access Token (15 min) + Refresh Token (7 days) pattern
- Created custom JWT filter for automatic token validation
- Stored refresh tokens securely in database with revocation support
- Used environment-specific secret keys for production

**Learned**:
- Token rotation prevents token theft exploitation
- Security vs. UX requires thoughtful trade-offs
- Environment-based configuration is essential for JWT secrets

**Code Snippet**:
```java
// JwtGatewayImpl.java - Custom claims implementation
Map<String, Object> claims = new HashMap<>();
claims.put("type", "access");
claims.put("userId", user.getId());
String token = JWT.create()
    .withSubject(user.getEmail())
    .withExpiresAt(accessTokenExpiry)
    .withPayload(claims)
    .sign(Algorithm.HMAC256(secretKey));
```

**Result**: Secure authentication with seamless UX, automatic token refresh

### Challenge 3: Reading Evolution Query Optimization
**Problem**: Metrics endpoint for reading evolution was slow with large datasets (multiple reading sessions). Initial N+1 query problem caused 5+ second response times.

**Solution**:
- Analyzed query execution plan with PostgreSQL EXPLAIN
- Created custom JPQL query with JOIN FETCH to eliminate N+1
- Added database indexes on frequently queried columns
- Implemented result caching for repeated requests

**Learned**:
- ORMs can generate inefficient queries if not carefully designed
- Database indexing strategy significantly impacts performance
- Monitoring query performance is essential for scaling

**Before**: 5.2 seconds for 1000 reading sessions
**After**: 180 milliseconds for 1000 reading sessions (28x faster)

## 🛠️ Tech Stack

### Core

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### Database & Migration

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-000000?style=for-the-badge&logo=h2&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0000?style=for-the-badge&logo=flyway&logoColor=white)

### Security

![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

### Build & Testing

![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-EC4213?style=for-the-badge&logo=mockito&logoColor=white)

### Code Quality

![SonarQube](https://img.shields.io/badge/SonarQube-5196CF?style=for-the-badge&logo=sonarqube&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-00Bfa5?style=for-the-badge&logo=opengamma&logoColor=white)
![Checkstyle](https://img.shields.io/badge/Checkstyle-EC4213?style=for-the-badge&logo=checkstyle&logoColor=white)

### DevOps

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

## ▶️ How to Run

### Prerequisites

- Java 17+ and Maven (or use the included Maven wrapper)
- PostgreSQL 15+ installed and running
- Docker and Docker Compose (optional, for containerized deployment)

### Option 1: Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/felipemelozx/InkTrack-api.git
   cd InkTrack-api
   ```

2. **Set up local database**

   Start PostgreSQL using Docker Compose (recommended):
   ```bash
   docker-compose up -d postgres
   ```

   Or use your local PostgreSQL installation:
   ```bash
   createdb dev_db
   ```

3. **Configure application**

   Edit `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/dev_db
   spring.datasource.username=dev_user
   spring.datasource.password=dev_password
   api.secret.key=your-secret-key-here
   front.url=http://localhost:3000
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Check API health**
   ```bash
   curl http://localhost:8080/api/v1/actuator/health
   ```

### Option 2: Docker Compose (Production)

1. **Clone the repository**
   ```bash
   git clone https://github.com/felipemelozx/InkTrack-api.git
   cd InkTrack-api
   ```

2. **Configure environment variables**

   Copy and edit the environment file:
   ```bash
   cp .env.example .env
   # Edit .env with your production values
   ```

3. **Build and run with Docker Compose**
   ```bash
   docker-compose -f docker-compose.prod.yaml --env-file .env up -d
   ```

4. **Check API health**
   ```bash
   curl http://localhost:8081/api/v1/actuator/health
   ```

### Option 3: Standalone Docker (Advanced)

If you want to run only the application container with an external database:

1. **Build the image**
   ```bash
   docker build -t inktrack-api .
   ```

2. **Run with environment variables**
   ```bash
   docker run -d \
     --name inktrack-api \
     -p 8080:8080 \
     -e POSTGRES_USER=your_user \
     -e POSTGRES_PASSWORD=your_password \
     -e POSTGRES_DB=your_db \
     -e API_SECRET_KEY=your-secret-key \
     -e FRONT_URL=http://localhost:3000 \
     -e SPRING_PROFILES_ACTIVE=prod \
     --add-host=db:host-gateway \
     inktrack-api
   ```

   **Note**: The application expects the database at `db:5432`. Using Docker Compose (Option 2) is recommended.

### Environment Variables

Copy `.env.example` to `.env` and configure the required variables:

```bash
cp .env.example .env
```

**Main variables:**

- `API_SECRET_KEY`: Secret key for JWT token signing (Generate with `openssl rand -base64 64`)
- `FRONT_URL`: Frontend URL for CORS configuration (e.g., `http://localhost:3000`)
- `APP_PORT`: Application port (default: 8081)
- `SERVER_CONTEXT_PATH`: API context path (default: /api/v1)
- `SPRING_PROFILES_ACTIVE`: Active profile (`dev` for local, `prod` for Docker)
- `POSTGRES_USER`: Database username (for production/Docker)
- `POSTGRES_PASSWORD`: Database password (for production/Docker)
- `POSTGRES_DB`: Database name (for production/Docker)

## 📚 API Documentation

Once the project is running locally, access the API at:

```
http://localhost:8080/api/v1
```

**Port Configuration:**
- Local development: `8080` (default)
- Docker Compose production: `8081` (configured via `APP_PORT` in `.env`)

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
  <b>Built with ❤️ for <a href="https://github.com/felipemelozx">Felipe Melo</a><b> 
</div>
