# Integration Tests

This document describes the two simple integration tests created for the rating system.

## Tests Created

### 1. GameObjectIntegrationTest
Tests the `GET /object` endpoint (getall method) which does not require authentication.

**Test Cases:**
- `getAllGameObjects_WithoutAuth_ReturnsAllObjects()` - Verifies that all game objects are returned without authentication
- `getAllGameObjects_WithGameTitleFilter_ReturnsFilteredObjects()` - Verifies filtering by game title works correctly

### 2. CommentIntegrationTest
Tests the `POST /users/{sellerId}/comments` endpoint (addComment method) which does not require authentication.

**Test Cases:**
- `addComment_WithoutAuth_AnonymousUser_CreatesPendingComment()` - Verifies that anonymous users can create comments
- `addComment_WithInvalidRating_ReturnsBadRequest()` - Verifies validation works for invalid ratings

## Files Created

1. **src/test/resources/application.properties** - Test configuration using H2 in-memory database
2. **src/test/java/com/tuguna/rating_system/integration/GameObjectIntegrationTest.java** - GameObject integration tests
3. **src/test/java/com/tuguna/rating_system/integration/CommentIntegrationTest.java** - Comment integration tests

## How to Run the Tests

### Option 1: Run all tests using Maven
```bash
mvn test
```

### Option 2: Run only integration tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Option 3: Run specific test class
```bash
# Run GameObjectIntegrationTest
mvn test -Dtest=GameObjectIntegrationTest

# Run CommentIntegrationTest
mvn test -Dtest=CommentIntegrationTest
```

### Option 4: Run from IDE
- Right-click on the test class file
- Select "Run 'GameObjectIntegrationTest'" or "Run 'CommentIntegrationTest'"
- Or run all tests in the `integration` package

## Test Configuration

The tests use:
- **H2 in-memory database** - No need for MySQL during testing
- **@SpringBootTest** - Full Spring context is loaded
- **@AutoConfigureMockMvc** - MockMvc for HTTP endpoint testing
- **@Transactional** - Each test runs in a transaction that is rolled back
- **@ActiveProfiles("test")** - Uses test profile configuration

## Test Data

Each test sets up its own test data in the `@BeforeEach` method:
- **GameObjectIntegrationTest**: Creates a seller, a game, and two game objects
- **CommentIntegrationTest**: Creates a seller for receiving comments

## Dependencies

All required dependencies are already in your `pom.xml`:
- `spring-boot-starter-test` - For testing support
- `h2` - In-memory database for tests
- `spring-security-test` - For security testing (not used in these tests as they test public endpoints)

## Notes

- Tests run without authentication (testing public endpoints)
- Each test is isolated and cleans up its data
- Tests use real database operations (not mocks) to verify end-to-end functionality

