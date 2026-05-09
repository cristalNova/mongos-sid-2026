# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.3 fitness/exercise management web application (WAR packaging, Java 17). Uses PostgreSQL in production and H2 in-memory for tests. MVC architecture with Thymeleaf templates, Spring Security, and JPA repositories.

## Commands

### Build & Run
```bash
./mvnw spring-boot:run          # Run locally (dev mode)
./mvnw clean package            # Build WAR artifact
./mvnw clean install            # Download deps + full build
```

### Tests
```bash
./mvnw test                     # Run all tests
./mvnw test -Dtest=UserServiceTest          # Run a single test class
./mvnw test -Dtest=UserServiceTest#methodName  # Run a single test method
./mvnw verify                   # Build + tests + JaCoCo coverage report
```

Coverage report generated at `target/site/jacoco/index.html`.

## Architecture

**Layer flow:** Controller → Service → Repository (JPA) → PostgreSQL/H2

```
src/main/java/co/icesi/exercise/
├── model/          JPA entities (AppUser, Role, Permission, Exercise, Routine, Event, etc.)
├── repositories/   JpaRepository interfaces with custom query methods
├── services/       Business logic, all @Transactional where needed
├── controller/     MVC controllers returning Thymeleaf views (4 controllers)
├── dto/            Form input objects (AppUserDTO, RoleDTO, PermissionDTO)
└── config/         Spring Security (BCrypt, form login, method-level @PreAuthorize)
```

**Templates:** `src/main/resources/templates/` — Thymeleaf with Spring Security dialect. Organized by feature: `dashboard/`, `login/`, `user/`, `role/`, `permission/`.

**Database scripts:** `scripts/project_compunet_schema.sql` (schema) and `scripts/data_inserts.sql` (seed data).

## Key Design Decisions

- **Security:** Method-level with `@PreAuthorize("hasAuthority('PERMISSION_NAME')")`. Custom `CustomUserDetailsService` loads users with their roles and flattened permissions.
- **Test database:** H2 in `MODE=PostgreSQL` — test config at `src/test/resources/application.properties` uses `create-drop` DDL.
- **Composite key:** `SubscriptionPK` is an `@Embeddable` composite key for the `Subscription` entity.
- **M2M relationships:** User↔Role, Role↔Permission, User↔Trainer, Routine↔Exercise all use join tables.
- **Context path:** Production app runs at `/exercise` on port 8080.

## Production Database

PostgreSQL at `10.147.19.38:5432` (database: `exercise_app`) — requires ZeroTier network `93afae59635dc904`. Do not change production credentials in commits.
