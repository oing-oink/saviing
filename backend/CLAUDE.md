# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Rules
- ALWAYS use UTF-8 Encoding
- NEVER use emoji

# Repository Guidelines

## Project Structure & Module Organization
- Root Gradle multi-module: `saviing`
  - `saviing-common/` — shared config, filters, aspects, responses (no bootJar)
  - `saviing-bank/` — bank domain, services, Spring Boot app
  - `saviing-bank-internal-procedure/` — internal API contracts for inter-domain communication (no bootJar)
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

### Transaction Domain
- **Transaction Model**: Central aggregate root tracking all account transactions (입금, 출금, 이체)
- **Double-Entry Ledger**: LedgerPairSnapshot system for balanced transaction recording
- **Transfer System**: Complete transfer functionality with domain services and event handling
- **Transaction Types**: DEPOSIT, WITHDRAWAL, TRANSFER, REVERSAL with proper direction validation
- **State Management**: Transaction status handling (POSTED, VOID) with proper state transitions
- **Idempotency**: IdempotencyKey support for reliable transaction processing
- **Domain Services**: TransferDomainService for complex transfer validation and processing
- **Event-Driven**: Transfer events (TransferSettledEvent, TransferFailedEvent) for async processing

### Product Hierarchy
- **ProductCategory**: Enum for major classifications (DEMAND_DEPOSIT, INSTALLMENT_SAVINGS)
- **Product**: Entity with ID, category reference, name, and code
- **ProductService**: In-memory management with predefined products (자유입출금통장, 자유적금)

### Game Domain Architecture
The game module (`saviing-game`) follows **Hexagonal Architecture** with clear layer separation:

**Complete Directory Structure:**
```
domain/
├── model/
│   ├── aggregate/           # Aggregate roots (Item, Character, Inventory)
│   ├── enums/              # Domain enums (ItemType, Pet, Decoration)
│   └── vo/                 # Value objects (ItemId, CharacterId, PetLevel)
├── event/                  # Domain events (ItemRegisteredEvent, etc.)
├── exception/              # Domain-specific exceptions
└── repository/             # Repository interfaces

application/
├── dto/
│   ├── command/            # Command DTOs (CQRS pattern)
│   ├── query/              # Query DTOs
│   ├── result/             # Result DTOs
│   └── enums/              # Application-layer enums
├── mapper/                 # Result mappers
├── service/                # Application services (CommandService, QueryService)
└── event/handler/          # Event handlers

infrastructure/
└── persistence/
    ├── entity/             # JPA entities
    ├── mapper/             # Entity-domain mappers
    └── repository/         # Repository implementations

presentation/
├── dto/
│   ├── request/            # Request DTOs
│   └── response/           # Response DTOs
├── interfaces/             # API interfaces
├── mapper/                 # Request/Response mappers
└── rest/                   # REST controllers
```

**Game Domain Concepts:**
- **Character Domain**: Player management, coins, rooms, account connections
- **Inventory Domain**: Item ownership, placement, equipment, consumption (extends base Inventory)
- **Pet Domain**: Virtual pet lifecycle (level, experience, affection, energy)
- **Item Domain**: Game item catalog with type-category validation system
- **Shop Domain**: In-game commerce and purchases

### Game Domain Implementation Patterns

#### **Aggregate Root Pattern (참고: Item.java, Character.java)**
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    @Builder
    private Item(/* parameters */) {
        // field assignments with defaults
        validateInvariants();
    }

    public static Item create(/* parameters */) {
        Item item = Item.builder()...build();
        item.addDomainEvent(ItemRegisteredEvent.of(...));
        return item;
    }

    // Business methods with event generation
    public void makeAvailable() {
        if (isAvailable) {
            throw ItemUnavailableException.alreadyAvailable(this.itemId);
        }
        this.isAvailable = true;
        updateTimestamp();
        addDomainEvent(ItemAvailabilityChangedEvent.of(...));
    }
}
```

#### **Value Object Pattern (참고: ItemId.java, CharacterId.java)**
```java
public record ItemId(Long value) {
    public ItemId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("아이템 ID는 양수여야 합니다");
        }
    }

    public static ItemId of(Long value) {
        return new ItemId(value);
    }
}
```

#### **Domain Event Pattern (참고: ItemRegisteredEvent.java)**
```java
public record ItemRegisteredEvent(
    ItemId itemId,
    ItemName itemName,
    ItemType itemType,
    Category itemCategory,
    LocalDateTime occurredOn
) implements DomainEvent {

    public static ItemRegisteredEvent of(/* parameters */) {
        return new ItemRegisteredEvent(/* params */, LocalDateTime.now());
    }
}
```

#### **Type-Category Validation System (참고: ItemType.java, Pet.java)**
```java
public enum ItemType {
    PET {
        @Override
        public List<Category> getCategories() {
            return Arrays.asList(Pet.values());
        }

        @Override
        public boolean isValidCategory(Category category) {
            return category instanceof Pet;
        }
    };

    public abstract List<Category> getCategories();
    public abstract boolean isValidCategory(Category category);
}

public enum Pet implements Category {
    CAT;

    @Override
    public ItemType getItemType() {
        return ItemType.PET;
    }
}
```

#### **Inventory Extension Pattern (참고: DecorationInventory.java, PetInventory.java)**
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetInventory extends Inventory {
    private Pet category;
    private Long roomId;

    public static PetInventory create(CharacterId characterId, ItemId itemId, Pet petCategory) {
        PetInventory petInventory = PetInventory.builder()
            .characterId(characterId)
            .itemId(itemId)
            .category(petCategory)
            .isUsed(false)
            .build();

        petInventory.addDomainEvent(InventoryItemAddedEvent.of(...));
        return petInventory;
    }
}
```

#### **CQRS Application Service Pattern (참고: ItemCommandService.java)**
```java
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemCommandService {
    private final ItemRepository itemRepository;
    private final ItemResultMapper itemResultMapper;

    public ItemResult registerItem(RegisterItemCommand command) {
        log.info("아이템 등록 시작: {}", command.itemName());

        Item item = Item.create(/* parameters from command */);
        Item savedItem = itemRepository.save(item);

        log.info("아이템 등록 완료: ID={}", savedItem.getItemId().value());
        return itemResultMapper.toResult(savedItem);
    }
}
```

#### **JPA Entity Pattern (참고: ItemEntity.java)**
```java
@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Builder
    public ItemEntity(/* parameters */) {
        // field assignments
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

#### **Controller Pattern (참고: ItemController.java)**
```java
@Slf4j
@RestController
@RequestMapping("/v1/game")
@RequiredArgsConstructor
public class ItemController implements ItemApi {
    private final ItemQueryService itemQueryService;
    private final ItemResponseMapper itemResponseMapper;

    @Override
    @GetMapping("/items/{itemId}")
    public ApiResult<ItemResponse> getItem(@PathVariable Long itemId) {
        log.info("아이템 조회 요청: itemId={}", itemId);

        GetItemQuery query = itemRequestMapper.toQuery(itemId);
        ItemResult result = itemQueryService.getItem(query);
        ItemResponse response = itemResponseMapper.toResponse(result);

        log.info("아이템 조회 완료: itemId={}", itemId);
        return ApiResult.ok(response);
    }
}
```

### Pet Domain Business Rules
- **Pet Lifecycle**: Level progression through experience points with configurable curves
- **Affection System**: Time-based decay with interaction-based recovery
- **Energy Management**: Affects affection multipliers and experience gain
- **Pet Categories**: Currently supports CAT, with extensible enum design
- **Room Placement**: Pets can be placed in specific character rooms
- **Item Integration**: Pets are created via ItemPurchasedEvent when PET items are purchased
- **Inventory Extension**: PetInventory extends base Inventory class like other specialized inventories

## Testing Guidelines
- Framework: JUnit Platform via `spring-boot-starter-test`
- Test class naming: `*Tests.java` (e.g., `AccountTests.java`)
- Test method naming: Korean business-readable names preferred
- Structure: Given-When-Then pattern with clear business scenarios
- Domain focus: Comprehensive unit tests for domain logic; use `@SpringBootTest` only when needed
- Assertions: AssertJ for fluent assertions
- Mocking: Mockito for domain service testing

### Module-Specific Testing
- Bank module: `./gradlew :saviing-bank:test --tests "*AccountTests"`
- Game module: `./gradlew :saviing-game:test --tests "*CharacterTests"`
- Game domain tests: `./gradlew :saviing-game:test --tests "*PetTests"`

## Coding Style & Naming Conventions
- **ALWAYS** See `docs/CODE_CONVENTIONS.md` for comprehensive 650+ line rules (Korean)
- **Key Patterns**: Constructor injection with `@RequiredArgsConstructor`, static factory methods, defensive programming
- **Korean Context**: Business logic comments and test names in Korean for domain clarity
- **Value Objects**: Immutable with validation in constructors
- **Builder Pattern**: Specific inline styling rules for method parameters

## Configuration & Environment
- **Profile-based**: `application.yaml`, `application-test.yaml`, `application-prod.yaml`
- **Database Strategy**: MySQL for production, H2 for testing
- **Port Configuration**: Bank service (8080), Game service (8081)
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

## Inter-Module Communication
- **Internal API Pattern**: `saviing-bank-internal-procedure` module defines contracts for inter-domain calls
- **Account Internal API**: Provides withdraw, deposit, getAccount operations for transaction processing
- **API Response Pattern**: Sealed success/failure responses with proper error handling
- **Domain Isolation**: Transaction domain accesses Account domain only through internal API contracts

## Pet Domain Implementation Guide

### **Reference Files for Pet Domain Development**
Pet 도메인 구현 시 다음 파일들을 참고하여 동일한 패턴을 적용하세요:

#### **Domain Layer References**
- **Aggregate Root**: `saviing-game/src/main/java/saviing/game/item/domain/model/aggregate/Item.java`
- **Inventory Extension**: `saviing-game/src/main/java/saviing/game/inventory/domain/model/aggregate/DecorationInventory.java`
- **Value Objects**: `saviing-game/src/main/java/saviing/game/item/domain/model/vo/ItemId.java`
- **Domain Events**: `saviing-game/src/main/java/saviing/game/item/domain/event/ItemRegisteredEvent.java`
- **Repository Interface**: `saviing-game/src/main/java/saviing/game/item/domain/repository/ItemRepository.java`
- **Exception Handling**: `saviing-game/src/main/java/saviing/game/item/domain/exception/ItemException.java`

#### **Application Layer References**
- **Command Service**: `saviing-game/src/main/java/saviing/game/item/application/service/ItemCommandService.java`
- **Query Service**: `saviing-game/src/main/java/saviing/game/item/application/service/ItemQueryService.java`
- **DTOs**: `saviing-game/src/main/java/saviing/game/item/application/dto/command/RegisterItemCommand.java`
- **Event Handler**: `saviing-game/src/main/java/saviing/game/inventory/application/event/handler/ItemPurchasedEventHandler.java`
- **Result Mapper**: `saviing-game/src/main/java/saviing/game/item/application/mapper/ItemResultMapper.java`

#### **Infrastructure Layer References**
- **JPA Entity**: `saviing-game/src/main/java/saviing/game/item/infrastructure/persistence/entity/ItemEntity.java`
- **Repository Implementation**: `saviing-game/src/main/java/saviing/game/item/infrastructure/persistence/repository/ItemJpaRepository.java`
- **Entity Mapper**: `saviing-game/src/main/java/saviing/game/item/infrastructure/persistence/mapper/ItemEntityMapper.java`

#### **Presentation Layer References**
- **Controller**: `saviing-game/src/main/java/saviing/game/item/presentation/rest/ItemController.java`
- **API Interface**: `saviing-game/src/main/java/saviing/game/item/presentation/interfaces/ItemApi.java`
- **Request/Response DTOs**: `saviing-game/src/main/java/saviing/game/item/presentation/dto/`
- **Presentation Mapper**: `saviing-game/src/main/java/saviing/game/item/presentation/mapper/ItemResponseMapper.java`

### **Pet Domain Implementation Checklist**
1. ✅ Create `Pet` aggregate extending existing patterns from `Item`
2. ✅ Implement `PetInfo` value object with pet-specific fields (level, experience, affection, energy)
3. ✅ Extend `PetInventory` from base `Inventory` class (already exists)
4. ✅ Create Pet-specific events for lifecycle management
5. ✅ Implement CQRS services (`PetCommandService`, `PetQueryService`)
6. ✅ Create JPA entities and repositories following `ItemEntity` pattern
7. ✅ Build REST controllers following `ItemController` pattern
8. ✅ Handle `ItemPurchasedEvent` for pet creation
9. ✅ Add pet-specific business logic (level calculation, affection decay)
10. ✅ Write comprehensive tests following existing patterns

## Agent-Specific Notes
- Keep changes minimal and module-scoped; follow this guide when editing files
- Touch only necessary modules; update tests alongside code changes
- When modifying domain models, ensure JPA entities and DTOs are updated accordingly
- Product/ProductCategory changes require updates to: domain models, JPA entities, services, and tests
- Transaction domain changes may require updates to both domain models and internal API contracts
- **Pet Domain**: Follow existing game domain patterns, especially Item and Inventory patterns
- Always run compilation and tests after domain model changes: `./gradlew :saviing-game:compileJava :saviing-game:test`

## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.