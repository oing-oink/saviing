# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Repository Guidelines

## Project Structure & Module Organization
- Root Gradle multi-module: `saviing`
  - `saviing-common/` — shared config, filters, aspects, responses (no bootJar)
  - `saviing-bank/` — bank domain, services, Spring Boot app
  - `saviing-game/` — game service, Spring Boot app
  - `config/common/logback-spring.xml` — logging config
  - Resources per module: `src/main/resources/application*.yaml`

## Build, Test, and Development Commands
- Build all: `./gradlew clean build`
- Test all: `./gradlew test`
- Test specific module: `./gradlew :saviing-bank:test`
- Test specific class: `./gradlew :saviing-bank:test --tests "*AccountTests"`
- Run bank locally: `./gradlew :saviing-bank:bootRun`
- Run game locally: `./gradlew :saviing-game:bootRun`
- Activate profile (example): `./gradlew :saviing-bank:bootRun --args='--spring.profiles.active=test'`
- Package app JARs: `./gradlew :saviing-bank:bootJar :saviing-game:bootJar`
- Compile specific module: `./gradlew :saviing-bank:compileJava`
- Check dependencies: `./gradlew dependencies`

## Architecture & Design Patterns

### Hexagonal Architecture Implementation
This codebase implements hexagonal (ports and adapters) architecture, particularly in the Account domain:

**Structure Pattern:**
```
domain/
├── model/           # Aggregate roots (Account, Product)
├── vo/              # Value objects (MoneyWon, AccountId, BasisPoints)
└── service/         # Domain services (InterestAccrualService)
application/
├── port/in/         # Use cases (CreateAccountUseCase)
├── port/out/        # Output ports (LoadAccountPort, SaveAccountPort)
└── service/         # Application services implementing use cases
adapter/
├── in/web/          # REST controllers and DTOs
└── out/persistence/ # JPA entities and repositories
```

**Key Principles:**
- Domain models are isolated from external dependencies
- Business logic encapsulated in aggregate roots with proper invariants
- Port-based contracts between layers
- Dependency inversion: core domain depends on abstractions

### Domain-Driven Design Patterns
- **Aggregate Roots**: `Account` contains all business logic for account operations
- **Value Objects**: `MoneyWon`, `BasisPoints`, `AccountNumber` with strong encapsulation
- **Domain Services**: Complex calculations like `InterestAccrualService`
- **Product Modeling**: Recent refactoring shows `ProductCategory` (enum) + `Product` (entity) separation
- **Rich Domain Models**: Account handles deposit, withdraw, freeze, interest calculation with proper state transitions

### Error Handling Architecture
- **Layered Exception Strategy**: `BusinessException` base → `AccountException` domain → specific exceptions
- **Contextual Information**: Exceptions carry `Map<String, Object>` for debugging context
- **Global Handling**: `GlobalExceptionHandler` in common module for consistent API responses
- **Type-Safe Error Codes**: `ErrorCode` interface with HTTP status mapping

## Domain Concepts & Business Rules

### Account Domain
- **Interest Calculation**: Complex carryover logic for fractional amounts (이월형)
- **State Management**: Proper state transitions (ACTIVE → FROZEN → CLOSED)
- **Money Handling**: Korean Won with overflow protection and business operations
- **Product Association**: Account references Product (not ProductCategory directly)

### Product Hierarchy
- **ProductCategory**: Enum for major classifications (DEMAND_DEPOSIT, INSTALLMENT_SAVINGS)
- **Product**: Entity with ID, category reference, name, and code
- **ProductService**: In-memory management with predefined products (자유입출금통장, 자유적금)

## Testing Guidelines
- Framework: JUnit Platform via `spring-boot-starter-test`
- Test class naming: `*Tests.java` (e.g., `AccountTests.java`)
- Test method naming: Korean business-readable names preferred
- Structure: Given-When-Then pattern with clear business scenarios
- Domain focus: Comprehensive unit tests for domain logic; use `@SpringBootTest` only when needed
- Assertions: AssertJ for fluent assertions
- Mocking: Mockito for domain service testing

## Coding Style & Naming Conventions
- See `docs/CODE_CONVENTIONS.md` for comprehensive 650+ line rules (Korean)
- **Key Patterns**: Constructor injection with `@RequiredArgsConstructor`, static factory methods, defensive programming
- **Korean Context**: Business logic comments and test names in Korean for domain clarity
- **Value Objects**: Immutable with validation in constructors
- **Builder Pattern**: Specific inline styling rules for method parameters

## Configuration & Environment
- **Profile-based**: `application.yaml`, `application-test.yaml`, `application-prod.yaml`
- **Database Strategy**: MySQL for production, H2 for testing
- **Centralized Logging**: `config/common/logback-spring.xml`
- **Development Tools**: Spring Boot DevTools for hot reload, Actuator for monitoring
- **API Documentation**: Swagger/OpenAPI integration

## Commit & Pull Request Guidelines
- Use Conventional Commits with scope and ticket:
  - Examples: `feat(be): add interest accrual (S13P21A309-42)`
  - `docs(be): update CI config`
- PRs include: clear description, module scope, linked issue, testing commands, green CI

## Security & Configuration Tips
- Do not commit secrets; use profiles for environment-specific configuration
- Central logging via `config/common/logback-spring.xml`
- Input validation with proper exception handling
- No sensitive information in logs

## Agent-Specific Notes
- Keep changes minimal and module-scoped; follow this guide when editing files
- Touch only necessary modules; update tests alongside code changes
- When modifying domain models, ensure JPA entities and DTOs are updated accordingly
- Product/ProductCategory changes require updates to: domain models, JPA entities, services, and tests
- Always run compilation and tests after domain model changes: `./gradlew :saviing-bank:compileJava :saviing-bank:test`

## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.