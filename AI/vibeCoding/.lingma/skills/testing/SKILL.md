---
name: testing
description: Generates comprehensive test suites including unit tests, integration tests, and end-to-end tests with proper coverage and assertions
---

# Testing Skill

## Purpose
This skill enables agents to create robust test suites that ensure code quality, catch bugs early, and provide confidence in refactoring. It covers unit tests, integration tests, and E2E tests following best practices.

## When to Use
- Writing tests for new features or components
- Adding tests to existing untested code
- Improving test coverage
- Creating test-driven development (TDD) workflows
- Setting up testing infrastructure
- Debugging failing tests

## Workflow

### Step 1: Test Strategy
1. Analyze code to determine what needs testing
2. Identify critical paths and edge cases
3. Choose appropriate test types (unit/integration/E2E)
4. Determine testing framework and tools
5. Plan test data and fixtures

### Step 2: Test Design
1. Define test scenarios and cases
2. Create test structure (describe/it blocks)
3. Plan mocks and stubs where needed
4. Design test data generators if required
5. Consider async/await patterns

### Step 3: Implementation
1. Write clear, descriptive test names
2. Follow AAA pattern (Arrange-Act-Assert)
3. Keep tests independent and isolated
4. Use appropriate assertions
5. Handle async operations properly
6. Add setup/teardown logic when needed

### Step 4: Coverage & Quality
1. Ensure all branches are tested
2. Test error paths and edge cases
3. Verify boundary conditions
4. Check for race conditions in async code
5. Validate mock behavior

### Step 5: Review & Optimize
1. Eliminate test duplication
2. Extract common test utilities
3. Optimize slow tests
4. Ensure tests are deterministic
5. Add test documentation if complex

## Output Template

```[language]
// File: [test filename]
// Description: Tests for [what is being tested]

import { describe, it, expect, beforeEach, afterEach } from '[testing-framework]';
// ... other imports

describe('[Component/Function Name]', () => {
  // Setup
  beforeEach(() => {
    // Common setup code
  });

  afterEach(() => {
    // Cleanup code
  });

  describe('[Category of tests]', () => {
    it('should [expected behavior] when [condition]', () => {
      // Arrange
      const input = ...;
      
      // Act
      const result = functionUnderTest(input);
      
      // Assert
      expect(result).toBe(expectedValue);
    });

    it('should handle edge case: [description]', () => {
      // Test implementation
    });

    it('should throw error when [invalid condition]', () => {
      expect(() => {
        functionUnderTest(invalidInput);
      }).toThrow(ErrorType);
    });
  });
});
```

**Test Coverage:**
- ✅ Normal cases: [X/Y scenarios]
- ✅ Edge cases: [List key edge cases]
- ✅ Error handling: [Error scenarios covered]

**Running Tests:**
```bash
[command to run these tests]
```

## Best Practices

### Test Naming
- Use descriptive names that explain the scenario
- Format: "should [expected outcome] when [condition]"
- Include context in describe blocks
- Avoid generic names like "test 1", "test 2"

### Test Structure
- **AAA Pattern**: Arrange → Act → Assert
- One assertion per test (mostly)
- Keep tests focused and small
- Use nested describe blocks for organization
- Share setup via beforeEach/afterEach

### Test Data
- Use factories/fixtures for consistent data
- Avoid hardcoded values when possible
- Generate dynamic data for variety
- Clean up test data after tests
- Use realistic but minimal data

### Mocking
- Mock external dependencies (APIs, databases)
- Don't mock what you don't own
- Verify mock calls when important
- Reset mocks between tests
- Use partial mocks sparingly

### Async Testing
- Always await async operations
- Test both success and failure paths
- Handle timeouts appropriately
- Test race conditions if relevant
- Use fake timers for time-based tests

## Testing Frameworks Guide

### Jest (JavaScript/TypeScript)
```javascript
// Unit test example
describe('calculateTotal', () => {
  it('should sum all item prices', () => {
    const items = [
      { price: 10 },
      { price: 20 },
      { price: 30 }
    ];
    
    const total = calculateTotal(items);
    
    expect(total).toBe(60);
  });

  it('should return 0 for empty array', () => {
    expect(calculateTotal([])).toBe(0);
  });

  it('should handle null items gracefully', () => {
    expect(() => calculateTotal(null)).toThrow(TypeError);
  });
});

// Mocking example
jest.mock('../api/client');

describe('fetchUserData', () => {
  it('should return user data from API', async () => {
    const mockUser = { id: 1, name: 'John' };
    apiClient.get.mockResolvedValue({ data: mockUser });
    
    const result = await fetchUserData(1);
    
    expect(result).toEqual(mockUser);
    expect(apiClient.get).toHaveBeenCalledWith('/users/1');
  });
});
```

### Pytest (Python)
```python
# Unit test example
def test_calculate_total():
    items = [
        {'price': 10},
        {'price': 20},
        {'price': 30}
    ]
    
    total = calculate_total(items)
    
    assert total == 60

def test_empty_items():
    assert calculate_total([]) == 0

def test_invalid_input():
    with pytest.raises(TypeError):
        calculate_total(None)

# Fixture example
@pytest.fixture
def sample_user():
    return {'id': 1, 'name': 'John', 'email': 'john@example.com'}

def test_user_creation(sample_user):
    user = User(**sample_user)
    assert user.name == 'John'
```

### JUnit (Java)
```java
@Test
void shouldCalculateTotalCorrectly() {
    List<Item> items = Arrays.asList(
        new Item(10),
        new Item(20),
        new Item(30)
    );
    
    double total = calculator.calculateTotal(items);
    
    assertEquals(60.0, total, 0.001);
}

@Test
void shouldHandleEmptyList() {
    assertEquals(0.0, calculator.calculateTotal(Collections.emptyList()));
}

@Test
void shouldThrowExceptionForNull() {
    assertThrows(IllegalArgumentException.class, () -> {
        calculator.calculateTotal(null);
    });
}
```

## Test Types

### Unit Tests
**Purpose**: Test individual functions/components in isolation
**Scope**: Single unit of code
**Speed**: Fast (milliseconds)
**Dependencies**: Mocked/stubbed

```javascript
// Pure function test
describe('formatCurrency', () => {
  it('should format number as USD currency', () => {
    expect(formatCurrency(1234.56)).toBe('$1,234.56');
  });

  it('should handle negative amounts', () => {
    expect(formatCurrency(-50)).toBe('-$50.00');
  });
});
```

### Integration Tests
**Purpose**: Test interaction between multiple units
**Scope**: Multiple components working together
**Speed**: Medium (seconds)
**Dependencies**: Real or semi-real

```javascript
describe('UserService Integration', () => {
  let database;
  
  beforeEach(async () => {
    database = await setupTestDatabase();
  });
  
  afterEach(async () => {
    await database.cleanup();
  });

  it('should create and retrieve user', async () => {
    const userService = new UserService(database);
    
    const created = await userService.createUser({
      name: 'John',
      email: 'john@test.com'
    });
    
    const retrieved = await userService.getUser(created.id);
    
    expect(retrieved.name).toBe('John');
  });
});
```

### End-to-End Tests
**Purpose**: Test complete user flows
**Scope**: Full application stack
**Speed**: Slow (minutes)
**Dependencies**: All real systems

```javascript
describe('User Registration E2E', () => {
  it('should complete registration flow', async () => {
    // Navigate to registration page
    await page.goto('/register');
    
    // Fill form
    await page.fill('#name', 'John Doe');
    await page.fill('#email', 'john@example.com');
    await page.fill('#password', 'SecurePass123!');
    
    // Submit
    await page.click('#submit-button');
    
    // Verify success
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page).toHaveURL('/dashboard');
  });
});
```

## Common Test Scenarios

### Scenario 1: API Endpoint Testing
```javascript
describe('GET /api/users/:id', () => {
  it('should return user by ID', async () => {
    const response = await request(app)
      .get('/api/users/1')
      .expect(200);
    
    expect(response.body).toMatchObject({
      id: 1,
      name: expect.any(String)
    });
  });

  it('should return 404 for non-existent user', async () => {
    await request(app)
      .get('/api/users/999')
      .expect(404);
  });

  it('should validate ID parameter', async () => {
    await request(app)
      .get('/api/users/invalid')
      .expect(400);
  });
});
```

### Scenario 2: React Component Testing
```javascript
import { render, screen, fireEvent } from '@testing-library/react';

describe('LoginForm', () => {
  it('should display validation errors for empty fields', async () => {
    render(<LoginForm onSubmit={() => {}} />);
    
    fireEvent.click(screen.getByText('Login'));
    
    expect(await screen.findByText('Email is required')).toBeInTheDocument();
    expect(await screen.findByText('Password is required')).toBeInTheDocument();
  });

  it('should call onSubmit with credentials', async () => {
    const mockSubmit = jest.fn();
    render(<LoginForm onSubmit={mockSubmit} />);
    
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'test@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByText('Login'));
    
    expect(mockSubmit).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
  });
});
```

### Scenario 3: Database Operations
```javascript
describe('UserRepository', () => {
  let repo;
  let db;

  beforeEach(async () => {
    db = await connectTestDatabase();
    repo = new UserRepository(db);
  });

  afterEach(async () => {
    await db.dropCollection('users');
    await db.close();
  });

  it('should save and find user', async () => {
    const userData = {
      name: 'Jane',
      email: 'jane@test.com'
    };
    
    const saved = await repo.save(userData);
    const found = await repo.findById(saved.id);
    
    expect(found.name).toBe('Jane');
    expect(found.email).toBe('jane@test.com');
  });

  it('should enforce unique email constraint', async () => {
    await repo.save({ name: 'User1', email: 'unique@test.com' });
    
    await expect(
      repo.save({ name: 'User2', email: 'unique@test.com' })
    ).rejects.toThrow('Duplicate key error');
  });
});
```

## Test Utilities

### Test Data Factory
```javascript
// utils/testFactories.js
class UserFactory {
  static create(overrides = {}) {
    return {
      id: Math.random().toString(36).substr(2, 9),
      name: 'Test User',
      email: `test${Math.random()}@example.com`,
      createdAt: new Date(),
      ...overrides
    };
  }

  static createAdmin(overrides = {}) {
    return this.create({
      role: 'admin',
      permissions: ['read', 'write', 'delete'],
      ...overrides
    });
  }
}

export { UserFactory };
```

### Mock Helpers
```javascript
// utils/testMocks.js
function createMockApiResponse(data, status = 200) {
  return {
    ok: status >= 200 && status < 300,
    status,
    json: async () => data,
    text: async () => JSON.stringify(data)
  };
}

function createMockLocalStorage() {
  const store = {};
  return {
    getItem: jest.fn((key) => store[key] || null),
    setItem: jest.fn((key, value) => { store[key] = value; }),
    removeItem: jest.fn((key) => { delete store[key]; }),
    clear: jest.fn(() => { Object.keys(store).forEach(k => delete store[k]); })
  };
}

export { createMockApiResponse, createMockLocalStorage };
```

## Anti-Patterns to Avoid

❌ **Don't**:
- Write tests that depend on execution order
- Use sleep/wait instead of proper async handling
- Test implementation details instead of behavior
- Create fragile tests that break with refactoring
- Ignore failing tests or mark them as skipped permanently
- Write overly complex test setup

✅ **Do**:
- Keep tests independent and isolated
- Use proper async/await patterns
- Test public APIs and behaviors
- Make tests resilient to internal changes
- Fix failing tests immediately
- Extract reusable test helpers

## Coverage Guidelines

**Minimum Coverage Targets:**
- Unit Tests: 80%+ line coverage, 70%+ branch coverage
- Integration Tests: Critical paths 100% covered
- E2E Tests: Key user journeys covered

**What to Prioritize:**
1. Business logic and algorithms
2. Authentication and authorization
3. Data validation and sanitization
4. Error handling and recovery
5. Integration points (APIs, databases)

**What Can Have Lower Coverage:**
- Simple getters/setters
- UI styling components
- Third-party library wrappers
- Configuration files
- Generated code

## Debugging Failing Tests

### Common Issues
1. **Async Timing**: Use proper awaits and async test helpers
2. **State Pollution**: Reset state between tests
3. **Mock Misconfiguration**: Verify mock setup matches usage
4. **Environment Differences**: Ensure test env matches expectations
5. **Race Conditions**: Add proper synchronization

### Debugging Steps
```javascript
// Add logging temporarily
it('should work correctly', async () => {
  console.log('Input:', inputData); // Debug log
  
  const result = await functionUnderTest(inputData);
  
  console.log('Output:', result); // Debug log
  expect(result).toBe(expected);
});

// Isolate the issue
describe.only('Specific failing test', () => {
  // Focus on one test at a time
});
```

## Related Skills
- `/code-generation` - Generate code with tests included
- `/refactoring` - Improve test quality and structure
- `/documentation` - Document test strategies and results
