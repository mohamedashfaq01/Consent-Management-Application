# Project Implementation Details

This document chronicles the step-by-step implementation, architectural patterns, database migrations, and testing verification implemented across the **Consent Management Application**.

---

## 1. Incremental Implementation Phases

### Phase 1: Core Database & Infrastructure
- **Objective**: Establish a robust relational foundation with automated schema version control and validation.
- **Components Configured**:
  - In-memory H2 database configured with PostgreSQL dialect compatibility:
    `jdbc:h2:mem:consent_management;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`
  - Flyway migration auto-execution on application bootstrap (`classpath:db/migration`).
  - Hibernate strict schema validation (`spring.jpa.hibernate.ddl-auto: validate`).
  - H2 Web Console enabled at `/h2-console`.
- **Migration**: [`V1__create_consentable_category_v2.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V1__create_consentable_category_v2.sql) creating `consentable_category_v2`.
- **Verification**: [`DatabaseConfigurationTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/DatabaseConfigurationTest.kt) validates live JDBC connectivity and `flyway_schema_history` initialization.

---

### Phase 2: Category Retrieval REST API
- **Objective**: Expose `GET /consentableCategory/all` to retrieve all categories from the database without hardcoded values.
- **Components Implemented**:
  - **DTO**: [`ConsentableCategoryDto.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/dto/ConsentableCategoryDto.kt) with `val name: String`.
  - **Service**: [`ConsentableCategoryService.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/service/ConsentableCategoryService.kt) with `@Transactional(readOnly = true) fun getAllCategories(): List<ConsentableCategoryDto>`.
  - **Controller**: [`ConsentableCategoryController.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/controller/ConsentableCategoryController.kt) with `@GetMapping("/all")`.
- **Testing**:
  - Slice test [`ConsentableCategoryControllerTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/controller/ConsentableCategoryControllerTest.kt) (`@WebMvcTest`) testing JSON structure and empty array handling.
  - End-to-end integration test [`ConsentableCategoryControllerIntegrationTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/controller/ConsentableCategoryControllerIntegrationTest.kt) (`@SpringBootTest` + `@AutoConfigureMockMvc`) executing against real H2 data.
  - Unit test [`ConsentableCategoryServiceTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/service/ConsentableCategoryServiceTest.kt).

---

### Phase 3: ConsentableItem Persistence & Category Relationship
- **Objective**: Implement individual consent items and establish a Many-to-One relationship to `ConsentableCategory`.
- **Components Implemented**:
  - **Entity**: [`ConsentableItem.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/ConsentableItem.kt) with fields: `identifier` (UUID), `name`, `category` (`@ManyToOne`), `status`, `businessLine`, `startDate` (LocalDate), `expirationDate` (LocalDate), `version` (Int).
  - **Repository**: [`ConsentableItemRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/ConsentableItemRepository.kt) with relationship derived queries: `findByCategory` and `findByCategoryName`.
  - **Migration**: [`V2__create_consentable_item_v2.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V2__create_consentable_item_v2.sql) with foreign key `fk_consentable_item_category`.
- **Testing**:
  - [`ConsentableItemRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentableItemRepositoryTest.kt) validating field mappings, Many-to-One relationships, delete isolation, and foreign key constraint enforcement in H2.

---

### Phase 4: Customer & Consent Domain
- **Objective**: Introduce customer identity, consent audit history, and associated financial account roles.
- **Components Implemented**:
  1. **`TargetCustomer`**:
     - Entity: [`TargetCustomer.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/TargetCustomer.kt) (`identifier`, `customerId`, `createdAt`, `lastUpdate`).
     - Repository: [`TargetCustomerRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/TargetCustomerRepository.kt) (`findByCustomerId`).
     - Migration: [`V3__create_target_customer_v2.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V3__create_target_customer_v2.sql).
  2. **`Consent`**:
     - Entity: [`Consent.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/Consent.kt) (`ConsentDecision` enum: `ACCEPTED`, `REFUSED`, `@ManyToOne` to `TargetCustomer`).
     - Repository: [`ConsentRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/ConsentRepository.kt).
     - Migration: [`V4__create_consent_v2.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V4__create_consent_v2.sql).
  3. **`ConsentAccountRole`**:
     - Entity: [`ConsentAccountRole.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/ConsentAccountRole.kt) (`@ManyToOne` to `Consent`, `accountId`, `accountType`, `role`).
     - Repository: [`ConsentAccountRoleRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/ConsentAccountRoleRepository.kt).
     - Migration: [`V5__create_consent_account_role_v2.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V5__create_consent_account_role_v2.sql).
- **Testing**:
  - [`TargetCustomerRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/TargetCustomerRepositoryTest.kt) (2 tests).
  - [`ConsentRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentRepositoryTest.kt) (4 tests).
  - [`ConsentAccountRoleRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentAccountRoleRepositoryTest.kt) (4 tests).

---

### Phase 5: Rating Model & Cascade Dependencies
- **Objective**: Implement customer-to-item rating readiness and item/category cascade dependency graphs.
- **Key Discovery & Resolution**:
  - The specification diagrams referenced key values as `Long` integers. Inspection of the active schema revealed that `ConsentableItem` and `TargetCustomer` identifiers are **UUIDs**, and `ConsentableCategory` identifiers are **Strings**.
  - All composite keys and foreign keys were mapped to the actual domain types (`UUID` and `String`) for exact schema adherence.
- **Components Implemented**:
  1. **`ContactConsentableRating`**:
     - Composite Key: [`CustomerConsentableRatingKey`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/ContactConsentableRating.kt) (`consentableItemId: UUID`, `targetCustomerId: UUID`).
     - Entity: [`ContactConsentableRating.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/ContactConsentableRating.kt).
     - Repository: [`ContactConsentableRatingRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/ContactConsentableRatingRepository.kt).
  2. **`CascadeConsentableItem`**:
     - Composite Key: [`CascadeConsentableItemKey`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/CascadeConsentableItem.kt) (`parentId: UUID`, `childId: UUID`).
     - Entity: [`CascadeConsentableItem.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/CascadeConsentableItem.kt).
     - Repository: [`CascadeConsentableItemRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/CascadeConsentableItemRepository.kt).
  3. **`CascadeConsentableCategory`**:
     - Composite Key: [`CascadeConsentableCategoryKey`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/CascadeConsentableCategory.kt) (`parentCategoryId: String`, `childCategoryId: String`).
     - Entity: [`CascadeConsentableCategory.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/entity/CascadeConsentableCategory.kt).
     - Repository: [`CascadeConsentableCategoryRepository.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/kotlin/com/consentmanagement/repository/CascadeConsentableCategoryRepository.kt).
  - **Migration**: [`V6__create_contact_consentable_rating_and_cascades.sql`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/main/resources/db/migration/V6__create_contact_consentable_rating_and_cascades.sql).
- **Testing**:
  - [`ContactConsentableRatingRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ContactConsentableRatingRepositoryTest.kt) (3 tests).
  - [`CascadeConsentableItemRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/CascadeConsentableItemRepositoryTest.kt) (2 tests).
  - [`CascadeConsentableCategoryRepositoryTest.kt`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/CascadeConsentableCategoryRepositoryTest.kt) (2 tests).

---

## 2. Test Execution & Coverage Summary

The test suite consists of **30 automated tests** testing presentation, service, repository, and physical H2 constraint layers.

```
Total Tests: 30
Passed: 30
Failed: 0
Skipped: 0
Build Time: ~4 seconds
```

### Breakdown by Test Suite

| Test Suite | Layer Tested | Tests | Scope |
|---|---|---|---|
| [`DatabaseConfigurationTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/DatabaseConfigurationTest.kt) | Infrastructure | 1 | Live H2 connectivity & Flyway history initialization |
| [`ConsentableCategoryControllerTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/controller/ConsentableCategoryControllerTest.kt) | Web Slice (`@WebMvcTest`) | 2 | Controller routing, HTTP status 200, JSON schema |
| [`ConsentableCategoryControllerIntegrationTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/controller/ConsentableCategoryControllerIntegrationTest.kt) | Full Integration | 2 | End-to-end controller to H2 database query |
| [`ConsentableCategoryServiceTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/service/ConsentableCategoryServiceTest.kt) | Service Unit | 2 | Entity-to-DTO transformation & empty handling |
| [`ConsentableCategoryRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentableCategoryRepositoryTest.kt) | JPA Persistence | 2 | Category single/batch persistence and retrieval |
| [`ConsentableItemRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentableItemRepositoryTest.kt) | JPA Persistence | 4 | Item mapping, Many-to-One FK, delete isolation |
| [`TargetCustomerRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/TargetCustomerRepositoryTest.kt) | JPA Persistence | 2 | Customer persistence, timestamp mapping, findByCustomerId |
| [`ConsentRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentRepositoryTest.kt) | JPA Persistence | 4 | Consent decisions, optional nulls, Customer FK |
| [`ConsentAccountRoleRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ConsentAccountRoleRepositoryTest.kt) | JPA Persistence | 4 | Account role mapping, optional accountType, Consent FK |
| [`ContactConsentableRatingRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/ContactConsentableRatingRepositoryTest.kt) | JPA Persistence | 3 | Composite key mapping, dual FK enforcement in H2 |
| [`CascadeConsentableItemRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/CascadeConsentableItemRepositoryTest.kt) | JPA Persistence | 2 | Item cascade parent-child dependencies & queries |
| [`CascadeConsentableCategoryRepositoryTest`](file:///Users/mohamedashfaq/Technical/Vibe%20Coding%20Projects/Consent%20Management%20Application/src/test/kotlin/com/consentmanagement/repository/CascadeConsentableCategoryRepositoryTest.kt) | JPA Persistence | 2 | Category cascade parent-child dependencies & queries |

---

## 3. How to Run Locally

### Run All Tests
```bash
./gradlew test
```

### Start the Application Server
```bash
./gradlew bootRun
```
The server starts on port `8080`.

### Access Developer Consoles
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:consent_management;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`
  - User: `sa`
  - Password: `(empty)`
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI 3 JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
