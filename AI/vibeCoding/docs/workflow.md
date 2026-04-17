# Vibe Coding Workflows

This document outlines the standard workflows for AI-assisted development using the vibe coding methodology.

## Table of Contents

- [Development Workflow](#development-workflow)
- [Code Review Workflow](#code-review-workflow)
- [Bug Fixing Workflow](#bug-fixing-workflow)
- [Feature Development Workflow](#feature-development-workflow)
- [Refactoring Workflow](#refactoring-workflow)
- [Testing Workflow](#testing-workflow)

---

## Development Workflow

### Overview
The standard development workflow follows an iterative, AI-assisted approach that emphasizes rapid prototyping with continuous validation.

### Phases

#### Phase 1: Requirements Clarification
**Goal**: Transform vague ideas into clear, actionable requirements

**Steps**:
1. Describe your feature/fix in natural language
2. Agent asks clarifying questions
3. Define acceptance criteria
4. Identify edge cases and constraints
5. Confirm understanding before proceeding

**Example Interaction**:
```
User: "I need a user authentication system"

Agent: Let me clarify the requirements:
- What authentication methods? (email/password, OAuth, SSO?)
- Session management preferences?
- Security requirements?
- Integration with existing systems?
```

#### Phase 2: Implementation Planning
**Goal**: Create a clear implementation roadmap

**Steps**:
1. Analyze existing codebase structure
2. Propose architecture/approach
3. Identify files to create/modify
4. List dependencies needed
5. Estimate complexity and risks

**Output**: Implementation plan with file structure

#### Phase 3: Code Generation
**Goal**: Generate production-ready code

**Steps**:
1. Activate `/code-generation` skill
2. Generate code incrementally
3. Validate each component
4. Add error handling
5. Include inline documentation

**Best Practices**:
- Generate small, focused components
- Test as you go
- Ask for adjustments immediately
- Keep conversation context relevant

#### Phase 4: Testing
**Goal**: Ensure code quality and correctness

**Steps**:
1. Activate `/testing` skill
2. Generate unit tests
3. Run test suite
4. Fix failing tests
5. Add integration tests if needed

**Commands**:
```bash
npm test
npm run test:coverage
```

#### Phase 5: Validation & Iteration
**Goal**: Verify solution meets requirements

**Steps**:
1. Manual testing of happy paths
2. Test edge cases
3. Performance validation
4. Security review
5. User feedback incorporation

---

## Code Review Workflow

### Overview
Systematic code review process leveraging AI assistance for thorough, consistent reviews.

### Review Checklist

#### 1. Functionality
- [ ] Code implements requirements correctly
- [ ] Edge cases handled
- [ ] Error handling appropriate
- [ ] No obvious bugs

#### 2. Code Quality
- [ ] Follows project conventions
- [ ] Clean, readable code
- [ ] Appropriate abstractions
- [ ] No code duplication

#### 3. Performance
- [ ] No obvious performance issues
- [ ] Efficient algorithms used
- [ ] Proper caching where needed
- [ ] No memory leaks

#### 4. Security
- [ ] Input validation present
- [ ] No hardcoded secrets
- [ ] Proper authentication/authorization
- [ ] SQL injection/XSS prevention

#### 5. Testing
- [ ] Adequate test coverage
- [ ] Tests are meaningful
- [ ] Edge cases tested
- [ ] Tests are maintainable

#### 6. Documentation
- [ ] Code is self-documenting
- [ ] Complex logic explained
- [ ] API documentation updated
- [ ] README changes if needed

### Using AI for Code Review

**Activate Agent**: Use vibe-coder agent with review focus

**Prompt Examples**:
```
"Review this code for potential issues:"
[code paste]

"Check for security vulnerabilities in this authentication flow:"
[code paste]

"Suggest performance improvements for this function:"
[code paste]
```

**Review Output Format**:
```markdown
## Code Review Summary

### ✅ Strengths
- [Positive aspects]

### ⚠️ Issues Found

#### Critical
- [Issue description with line numbers]
- **Impact**: [Why it matters]
- **Fix**: [Suggested solution]

#### Warning
- [Less critical issues]

#### Suggestions
- [Optional improvements]

### 📊 Metrics
- Complexity: [Low/Medium/High]
- Test Coverage: [X%]
- Maintainability: [Score]
```

---

## Bug Fixing Workflow

### Overview
Systematic approach to identifying, diagnosing, and fixing bugs with root cause analysis.

### Steps

#### Step 1: Bug Reproduction
**Goal**: Understand and reproduce the issue

**Actions**:
1. Gather bug report details
2. Reproduce the issue locally
3. Identify exact failure point
4. Collect error messages/stack traces
5. Note environment specifics

**Information to Collect**:
- Steps to reproduce
- Expected vs actual behavior
- Error messages
- Environment (OS, browser, versions)
- Frequency (always/sometimes/rarely)

#### Step 2: Root Cause Analysis
**Goal**: Identify the underlying cause

**Techniques**:
1. Read error messages carefully
2. Check recent changes (git blame)
3. Add debug logging
4. Isolate the problematic code
5. Trace execution flow

**AI Assistance**:
```
"Analyze this error and suggest root causes:"
[error message + stack trace]

"Why might this function return null in production?"
[code + context]
```

#### Step 3: Fix Development
**Goal**: Implement correct fix

**Approach**:
1. Fix root cause, not symptoms
2. Consider side effects
3. Add regression tests
4. Validate fix doesn't break other things
5. Document the fix

**Best Practices**:
- Minimal change to fix issue
- Add comments explaining the fix
- Update related documentation
- Consider similar issues elsewhere

#### Step 4: Verification
**Goal**: Confirm bug is fixed

**Checks**:
- [ ] Original reproduction steps now work
- [ ] No new bugs introduced
- [ ] All tests pass
- [ ] Performance not degraded
- [ ] Fix works in all environments

#### Step 5: Prevention
**Goal**: Prevent similar bugs

**Actions**:
1. Add automated tests for this case
2. Update coding standards if needed
3. Add linting rules if applicable
4. Document lesson learned
5. Consider static analysis improvements

---

## Feature Development Workflow

### Overview
End-to-end workflow for developing new features from conception to deployment.

### Phase 1: Discovery & Planning

#### 1.1 Requirement Gathering
**Questions to Answer**:
- What problem does this solve?
- Who are the users?
- What are the success criteria?
- What are the constraints?
- What's the timeline?

#### 1.2 Technical Analysis
**Tasks**:
- Review existing architecture
- Identify integration points
- Assess technical feasibility
- Identify risks and blockers
- Estimate effort

#### 1.3 Design Proposal
**Deliverables**:
- Architecture diagram
- API specifications
- Data model changes
- UI/UX mockups (if applicable)
- Implementation plan

### Phase 2: Implementation

#### 2.1 Setup
```bash
# Create feature branch
git checkout -b feature/feature-name

# Install dependencies if needed
npm install [packages]
```

#### 2.2 Incremental Development
**Strategy**: Build in small, testable increments

**Cycle**:
1. Plan next small piece
2. Generate code with AI assistance
3. Write tests
4. Validate functionality
5. Commit changes
6. Repeat

**Example Progression**:
```
Day 1: Data models and database schema
Day 2: API endpoints (CRUD operations)
Day 3: Business logic and validation
Day 4: Frontend components
Day 5: Integration and polish
```

#### 2.3 Continuous Validation
**After Each Component**:
- Run tests
- Manual testing
- Code review (self or peer)
- Update documentation

### Phase 3: Testing & QA

#### 3.1 Automated Testing
```bash
# Unit tests
npm test

# Integration tests
npm run test:integration

# E2E tests
npm run test:e2e

# Coverage report
npm run test:coverage
```

#### 3.2 Manual Testing
**Test Scenarios**:
- Happy path (normal usage)
- Edge cases
- Error conditions
- Performance under load
- Cross-browser/device (if web)

#### 3.3 User Acceptance Testing
- Demo to stakeholders
- Gather feedback
- Make adjustments
- Get sign-off

### Phase 4: Documentation

#### 4.1 Code Documentation
- Inline comments for complex logic
- JSDoc/docstrings for public APIs
- Update type definitions

#### 4.2 User Documentation
- Update README if needed
- Add usage examples
- Create tutorial/guide
- Update API docs

#### 4.3 Technical Documentation
- Architecture decisions (ADR)
- API specification updates
- Deployment instructions
- Monitoring/alerting setup

### Phase 5: Deployment

#### 5.1 Pre-deployment Checklist
- [ ] All tests passing
- [ ] Code reviewed and approved
- [ ] Documentation updated
- [ ] Database migrations ready
- [ ] Environment variables configured
- [ ] Rollback plan documented

#### 5.2 Deployment Steps
```bash
# Merge to main
git checkout main
git merge feature/feature-name

# Run final checks
npm test
npm run build

# Deploy
[deployment commands]

# Verify deployment
[health check commands]
```

#### 5.3 Post-deployment
- Monitor logs and metrics
- Watch for errors
- Collect user feedback
- Performance monitoring
- Be ready to rollback if needed

---

## Refactoring Workflow

### Overview
Safe, systematic refactoring to improve code quality without changing behavior.

### When to Refactor

**Good Times**:
- Before adding new features to related code
- After completing a feature (cleanup pass)
- When code is difficult to understand
- During bug fixing (improve while fixing)
- Regular maintenance windows

**Bad Times**:
- Right before a deadline
- Without adequate test coverage
- In the middle of critical bug fixes
- Without team alignment on large refactors

### Refactoring Process

#### Step 1: Assessment
**Evaluate Current State**:
```
"What are the code quality issues in this file?"
[file content]

"Suggest refactoring opportunities in this module"
[code]
```

**Identify**:
- Code smells
- Complexity hotspots
- Duplication
- Naming issues
- Architectural problems

#### Step 2: Safety Net
**Ensure Tests Exist**:
```bash
# Run existing tests
npm test

# Check coverage
npm run test:coverage

# Add missing tests if needed
```

**Create Baseline**:
- Document current behavior
- Note performance metrics
- Save comparison benchmarks

#### Step 3: Planning
**Define Scope**:
- What will be refactored?
- What will stay the same?
- What's the end goal?
- What are the risks?

**Create Plan**:
1. List refactoring steps
2. Order by risk (low to high)
3. Define success criteria
4. Plan rollback strategy

#### Step 4: Execution
**Incremental Approach**:

**Small Refactor Example**:
```javascript
// Before
function process(data) {
  // 50 lines of mixed logic
}

// After - Step 1: Extract validation
function process(data) {
  validateData(data);
  // 40 lines remaining
}

function validateData(data) {
  // validation logic
}

// After - Step 2: Extract transformation
function process(data) {
  validateData(data);
  const transformed = transformData(data);
  // 20 lines remaining
}

// Continue until clean...
```

**After Each Step**:
- Run tests
- Verify behavior unchanged
- Commit changes
- Update documentation if needed

#### Step 5: Validation
**Comprehensive Checks**:
- [ ] All tests pass
- [ ] No behavioral changes
- [ ] Performance acceptable
- [ ] Code quality improved
- [ ] Team review completed

**Metrics to Compare**:
- Cyclomatic complexity
- Code coverage
- Lines of code
- Maintainability index
- Build/test times

#### Step 6: Documentation
**Update**:
- Code comments
- Architecture docs if structure changed
- Migration guide if API changed
- Team knowledge sharing

### Common Refactoring Patterns

#### Pattern 1: Extract Method
**When**: Long method doing multiple things
**How**: Break into smaller, named functions

#### Pattern 2: Rename for Clarity
**When**: Unclear variable/function names
**How**: Use intention-revealing names

#### Pattern 3: Remove Duplication
**When**: Same code in multiple places
**How**: Extract to shared function/module

#### Pattern 4: Simplify Conditionals
**When**: Complex nested if/else
**How**: Use guard clauses, early returns

#### Pattern 5: Introduce Abstraction
**When**: Repeated patterns with variations
**How**: Use interfaces, base classes, strategies

---

## Testing Workflow

### Overview
Comprehensive testing strategy ensuring code quality and preventing regressions.

### Testing Pyramid

```
        /\
       /E2E\       (Few - Slow - Comprehensive)
      /------\
     /Integration\ (Some - Medium speed)
    /------------\
   /    Unit      \ (Many - Fast - Focused)
  /----------------\
```

### Test Types

#### Unit Tests
**Purpose**: Test individual units in isolation
**Scope**: Single function/component
**Speed**: < 100ms per test
**Framework**: Jest, Pytest, JUnit

**Example**:
```javascript
describe('calculateTotal', () => {
  it('should sum item prices correctly', () => {
    const items = [
      { price: 10, quantity: 2 },
      { price: 5, quantity: 1 }
    ];
    
    expect(calculateTotal(items)).toBe(25);
  });

  it('should handle empty array', () => {
    expect(calculateTotal([])).toBe(0);
  });
});
```

#### Integration Tests
**Purpose**: Test component interactions
**Scope**: Multiple units working together
**Speed**: 1-10 seconds per test
**Examples**: API + Database, Service + External API

**Example**:
```javascript
describe('UserService Integration', () => {
  it('should create and retrieve user', async () => {
    const user = await userService.create({
      email: 'test@example.com',
      name: 'Test User'
    });
    
    const retrieved = await userService.findById(user.id);
    
    expect(retrieved.email).toBe('test@example.com');
  });
});
```

#### End-to-End Tests
**Purpose**: Test complete user flows
**Scope**: Full application stack
**Speed**: 10-60 seconds per test
**Framework**: Cypress, Playwright, Selenium

**Example**:
```javascript
describe('User Registration E2E', () => {
  it('should complete registration flow', async () => {
    await page.goto('/register');
    await page.fill('#email', 'user@example.com');
    await page.fill('#password', 'SecurePass123!');
    await page.click('#submit');
    
    await expect(page.locator('.success')).toBeVisible();
  });
});
```

### Testing Best Practices

#### Test Organization
```
tests/
├── unit/
│   ├── services/
│   ├── components/
│   └── utils/
├── integration/
│   ├── api/
│   └── database/
└── e2e/
    ├── user-flows/
    └── admin-flows/
```

#### Naming Conventions
**Format**: `should [expected behavior] when [condition]`

**Examples**:
- ✅ `should return user by ID when valid ID provided`
- ✅ `should throw ValidationError when email is invalid`
- ❌ `test1`, `test user`, `check stuff`

#### AAA Pattern
```javascript
it('should calculate discount correctly', () => {
  // Arrange
  const cart = { total: 100 };
  const coupon = { percent: 20 };
  
  // Act
  const result = applyDiscount(cart, coupon);
  
  // Assert
  expect(result.total).toBe(80);
});
```

### Test Coverage Goals

**Minimum Targets**:
- Unit Tests: 80%+ line coverage
- Branch Coverage: 70%+
- Critical Paths: 100%
- Integration Tests: All API endpoints
- E2E Tests: Key user journeys

**What to Prioritize**:
1. Business logic
2. Authentication/Authorization
3. Data validation
4. Error handling
5. Integration points

**What Can Have Lower Coverage**:
- Simple getters/setters
- UI styling
- Third-party wrappers
- Configuration

### Running Tests

```bash
# All tests
npm test

# Unit tests only
npm run test:unit

# Integration tests
npm run test:integration

# E2E tests
npm run test:e2e

# With coverage
npm run test:coverage

# Watch mode (TDD)
npm run test:watch

# Specific test file
npm test -- path/to/test.spec.js
```

### Debugging Failing Tests

**Common Issues**:
1. **Async Timing**: Missing awaits
2. **State Pollution**: Tests affecting each other
3. **Mock Issues**: Incorrect mock setup
4. **Environment**: Missing env vars
5. **Race Conditions**: Timing-dependent code

**Debugging Strategy**:
```javascript
// 1. Add logging
console.log('Input:', data);
console.log('Output:', result);

// 2. Isolate test
describe.only('Failing test suite', () => {
  // Run only these tests
});

// 3. Check mocks
console.log('Mock calls:', mockFunction.mock.calls);

// 4. Verify setup
beforeEach(() => {
  console.log('Test setup running');
});
```

### Test-Driven Development (TDD)

**Red-Green-Refactor Cycle**:

1. **Red**: Write failing test
```javascript
it('should add two numbers', () => {
  expect(add(2, 3)).toBe(5); // Fails - function doesn't exist
});
```

2. **Green**: Make test pass (minimal implementation)
```javascript
function add(a, b) {
  return 5; // Just enough to pass test
}
```

3. **Refactor**: Improve implementation
```javascript
function add(a, b) {
  return a + b; // Proper implementation
}
```

4. **Repeat**: Add more test cases
```javascript
it('should handle negative numbers', () => {
  expect(add(-1, 1)).toBe(0);
});

it('should handle decimals', () => {
  expect(add(1.5, 2.3)).toBe(3.8);
});
```

### Mocking Strategies

**When to Mock**:
- External APIs
- Database calls
- File system operations
- Time-dependent code
- Random number generation

**When NOT to Mock**:
- Simple pure functions
- Your own code (test it directly)
- Third-party libraries you trust

**Example**:
```javascript
// Mock external API
jest.mock('../services/emailService');

describe('UserService', () => {
  it('should send welcome email on signup', async () => {
    const mockSendEmail = jest.fn();
    emailService.sendEmail = mockSendEmail;
    
    await userService.createUser(userData);
    
    expect(mockSendEmail).toHaveBeenCalledWith(
      userData.email,
      expect.stringContaining('Welcome')
    );
  });
});
```

### Continuous Integration

**CI Pipeline**:
```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Node
        uses: actions/setup-node@v2
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run linter
        run: npm run lint
      
      - name: Run unit tests
        run: npm run test:unit
      
      - name: Run integration tests
        run: npm run test:integration
      
      - name: Upload coverage
        uses: codecov/codecov-action@v2
```

---

## Quick Reference

### Common Commands

```bash
# Start development
npm run dev

# Build for production
npm run build

# Run all checks
npm run validate  # lint + test + build

# Generate documentation
npm run docs

# Database migrations
npm run migrate
npm run migrate:rollback

# Docker
docker-compose up
docker-compose down
```

### Git Workflow

```bash
# Feature branch
git checkout -b feature/description

# Commit changes
git add .
git commit -m "feat(scope): description"

# Push branch
git push origin feature/description

# Create PR
# [Use GitHub/GitLab UI]

# After merge
git checkout main
git pull origin main
git branch -d feature/description
```

### AI Assistant Commands

```
/code-generation    # Generate code from requirements
/testing            # Create test suites
/refactoring        # Improve code quality
/documentation      # Generate documentation

/vibe-coder         # Activate primary coding agent
```

---

## Additional Resources

- [Project README](../README.md)
- [Contributing Guidelines](CONTRIBUTING.md)
- [Architecture Documentation](docs/architecture.md)
- [API Reference](docs/API.md)
- [Best Practices](best-practices.md)
