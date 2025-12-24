# Smart Planner Backend

Backend application built with **Spring Boot 4**, **PostgreSQL**, **JWT**, and **GraphQL**.

## Features (requirements coverage)
- REST APIs with consistent request/response patterns and validation
- JWT Authentication + Authorization
- Roles: **ADMIN**, **MANAGER**, **USER**
- Refresh tokens (stored hashed) + refresh rotation
- Logout with access-token blacklist (JTI blacklist)
- Domain model with **8+ tables** and **inheritance** (Item -> Task/Appointment)
- Complex reports:
    - Top tags by usage
    - Tasks per priority per project
- GraphQL read-only dashboard
- Integrations with **2 third-party APIs**:
    - Weather (Open-Meteo)
    - Quote (Quotable)
- External API caching in DB (external_cache table)
- Postman collection included

---

## Tech Stack
- Java 25
- Spring Boot 4
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- Spring GraphQL
- Maven
- Docker Compose

---

## Domain Model (Tables)
Example tables included:
- roles
- users
- projects
- project_members
- items (base)
- tasks (extends items)
- appointments (extends items)
- tags
- item_tags (join)
- refresh_tokens
- token_blacklist
- external_cache

Inheritance: Item -> Task/Appointment (JOINED).

---

## Running locally

### 1) Start PostgreSQL (Docker)
```bash
docker compose up -d
