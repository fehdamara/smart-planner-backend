# Smart Planner Backend

Backend application developed with **Spring Boot 4**, **PostgreSQL**, **JWT Authentication** and **GraphQL**.  
The project demonstrates a complete and well-structured server-side application, including authentication, authorization, complex queries, external API integrations, and a read-only GraphQL dashboard.

---

## 🚀 Project Overview

Smart Planner is a task and project management backend that allows users to:

- Manage projects, tasks, tags, and priorities
- Authenticate using JWT (access + refresh tokens)
- Use role-based authorization (ADMIN, MANAGER, USER)
- Retrieve reports through REST APIs
- Access a **read-only GraphQL dashboard**
- Integrate external services (Weather + Quotes APIs)

The application is designed following best practices in:

- Clean architecture
- RESTful APIs
- Data validation
- Security
- Persistence with JPA
- Integration with third-party services

---

## 🧱 Tech Stack

- **Java 21**
- **Spring Boot 4**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Spring GraphQL**
- **PostgreSQL**
- **Maven (Wrapper)**
- **Docker (PostgreSQL)**
- **Postman**

---

## 🗄️ Domain Model

The domain model includes **8+ entities**, such as:

- User
- Role (ADMIN, MANAGER, USER)
- Project
- Task
- Tag
- TaskTag
- RefreshToken
- ExternalCache (or IntegrationCache)

The model includes:

- Meaningful relationships
- Inheritance where appropriate
- Realistic user profile data (email, password, name, surname, registration date, profile image)

---

## 🔐 Authentication & Authorization

- JWT-based authentication
- Access token + Refresh token
- Logout implemented using a **token blacklist**
- Roles:
  - **ADMIN** – full access
  - **MANAGER** – project and task management
  - **USER** – standard access

---

## 🌐 REST API Features

- User registration and login
- Token refresh
- Logout
- CRUD operations for projects and tasks
- Role-based access control
- Validation and structured error handling

---

## 📊 Reports (Complex Queries)

Implemented using JPA / JPQL:

- **Top tags by usage**
- **Tasks grouped by priority per project**

These queries demonstrate:

- Aggregations
- Grouping
- Filtering
- Real use cases

---

## 🧩 GraphQL Dashboard (Read-Only)

The GraphQL endpoint exposes a **read-only dashboard**, including:

- Top tags
- Tasks by priority
- Daily quote (external API)
- Current weather (external API)

### GraphQL Endpoint

POST /graphql

### Example Query

```graphql
query {
  dashboard(topTagsLimit: 10, latitude: 41.9, longitude: 12.5) {
    quote {
      content
      author
    }
    weather {
      temperature
      windspeed
      weatherCode
    }
    topTags {
      tagName
      usageCount
    }
    tasksByPriority {
      projectId
      priority
      taskCount
    }
  }
}
🌍 External API Integrations
The backend integrates with:

Open-Meteo API – weather data

Quotable API – daily inspirational quotes

Responses are cached to reduce unnecessary external calls.

🐳 Database Setup (Docker)
PostgreSQL is provided via Docker.


volumes:
  pgdata:
⚙️ Application Configuration
Configuration is handled via application.yml.

Main environment variables:

DB_URL

DB_USER

DB_PASS

JWT_SECRET

JWT_ACCESS_EXP_MIN

JWT_REFRESH_EXP_DAYS

Default values are provided for local development.

▶️ Running the Application
Prerequisites
Java 21 installed

Docker installed and running

Maven Wrapper included (mvnw / mvnw.cmd)

Steps
Start PostgreSQL:

bash
Copia codice
docker-compose up -d
# or
docker compose up -d
Run the application (Linux/macOS):

bash
Copia codice
./mvnw -DskipTests spring-boot:run
Run the application (Windows):

bat
Copia codice
.\mvnw.cmd -DskipTests spring-boot:run
The backend will be available at:

arduino
Copia codice
http://localhost:8080
🧪 Testing with Postman
A complete Postman collection (JSON) is included in the repository.

It contains:

Authentication flows

User management

Project and task APIs

Report endpoints

GraphQL queries

⚠️ Any functionality not included in the Postman collection is not evaluated.

👤 Sample Data
On startup, the application creates:

10 sample users

Roles assigned randomly

At least one ADMIN and one MANAGER

📁 Project Structure
css
Copia codice
src/main/java/com/example/smartplanner
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── exception
└── config
⚠️ Known Limitations & Notes
The GraphQL dashboard is intentionally read-only.
Mutations were not implemented by design, as the dashboard is meant for reporting and aggregated views only.

GraphQL numeric counters (e.g. tag usage count, task count) use the Int scalar type.
While internally the application may use Long values at the database level, Int was chosen in the GraphQL schema for compatibility and simplicity, in line with GraphQL standard scalars.

External API integrations (Weather and Quotes) depend on third-party services.
If those services are temporarily unavailable, the application returns safe fallback responses or cached data.

The application is designed for development and academic evaluation purposes.
Performance optimizations such as distributed caching, rate limiting, or advanced monitoring are outside the scope of this project.

The database schema is managed via Hibernate ddl-auto: update.
In a production environment, a dedicated migration tool (e.g. Flyway or Liquibase) would be preferred.

The frontend is not included in this project.
The backend is fully functional and testable via REST APIs, GraphQL, and the provided Postman collection.

✅ Evaluation Notes
This project fulfills all mandatory requirements:

Complete backend application

JWT authentication

Role-based authorization

Complex queries

External API integrations

GraphQL dashboard

PostgreSQL persistence

Postman collection

Clear documentation

Optional features were added to enhance the final evaluation.

👨‍💻 Author
Smart Planner Backend
Developed for academic evaluation using Spring Boot 4.
```
