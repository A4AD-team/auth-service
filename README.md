# IAM Service

Central Identity & Access Management (IAM) service for a microservices project.

## Stack
- Java 21
- Spring Boot 3.3
- Spring Security + OAuth2 Resource Server (JWT)
- Spring Data JPA + Hibernate
- PostgreSQL 16 (JSONB for custom claims)
- Flyway migrations
- OpenTelemetry + Prometheus
- Docker + Compose

## Quick Start (Docker Compose)
```bash
docker compose up --build
```

The stack starts PostgreSQL and the IAM service on `http://localhost:8080`.

### Default bootstrap admin
Compose sets a default admin user:
- Email: `admin@example.com`
- Password: `admin12345`

Override via environment variables:
- `BOOTSTRAP_ADMIN_EMAIL`
- `BOOTSTRAP_ADMIN_PASSWORD`
- `BOOTSTRAP_ADMIN_FULL_NAME`

## API Endpoints
- `POST /api/v1/auth/sign_up` — registration
- `POST /api/v1/auth/sign_in` — obtain access + refresh tokens
- `GET /api/v1/users/me` — current user (JWT required)
- `GET /api/v1/roles` — list roles (admin)

Additional management endpoints:
- `GET /api/v1/permissions` — list permissions
- `POST /api/v1/permissions` — create permission (admin)
- `POST /api/v1/roles` — create role (admin)

## Auth Flow
1. Sign up with email + password
2. Sign in to receive `accessToken` and `refreshToken`
3. Use `Authorization: Bearer <accessToken>` for protected endpoints

## Observability
- Prometheus metrics: `GET /actuator/prometheus`
- Health: `GET /actuator/health`

Set OTLP endpoint for tracing using:
- `OTEL_EXPORTER_OTLP_ENDPOINT`
- `OTEL_SERVICE_NAME`

## Configuration
Key env vars:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`
- `JWT_ACCESS_TTL_MINUTES`, `JWT_REFRESH_TTL_MINUTES`
- `JWT_ISSUER`
