# Auth Service

Микросервис аутентификации и управления пользователями / ролями / правами доступа.

[![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue)](https://www.postgresql.org/)

## Назначение

Центральный сервис Identity & Access Management (IAM):

- Регистрация / авторизация пользователей
- JWT-токены (access + refresh)
- Управление ролями, департаментами, правами

## Технологический стек

- Java 21
- Spring Boot 3.3+
- Spring Security + OAuth2 / JWT
- Spring Data JPA + Hibernate
- PostgreSQL 16+ (JSONB для custom claims)
- Liquibase / Flyway для миграций
- OpenTelemetry + Prometheus для observability
- Docker + Compose

## Структура проекта

```
auth-service/
├── src/
│ ├── main/
│ │ ├── java/com/company/auth/
│ │ └── resources/
│ └── test/
├── src/main/resources/db/migration/
├── pom.xml
├── Dockerfile
└── docker-compose.yml
```

## Быстрый старт (local)

```bash
# 1. PostgreSQL (docker)
docker run -d --name auth-postgres \
  -e POSTGRES_USER=auth \
  -e POSTGRES_PASSWORD=authsecret \
  -e POSTGRES_DB=auth_db \
  -p 5432:5432 postgres:16

# 2. Запуск
mvn clean spring-boot:run \
  -Dspring.profiles.active=local \
  -Dspring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
```

## Docker Compose пример

```yaml
services:
  auth-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: auth
      POSTGRES_PASSWORD: authsecret
      POSTGRES_DB: auth_db
    ports:
      - "5433:5432" # другой порт, чтобы не конфликтовать

  auth-service:
    build: .
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://auth-db:5432/auth_db
      # ... остальные переменные
    depends_on:
      - auth-db
```

## Основные эндпоинты

- POST /api/v1/auth/sign_up — регистрация
- POST /api/v1/auth/sign_in — получение токенов
- GET /api/v1/users/me — текущий пользователь (с JWT)
- GET /api/v1/roles — список ролей (admin)

## Health & Metrics

/actuator/health
/actuator/prometheus

## Окружения

- local — разработка
- dev — staging
- prod — продакшен (secrets via Vault / AWS SSM)
