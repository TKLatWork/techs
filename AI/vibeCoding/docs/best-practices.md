# Vibe Coding Best Practices

This guide outlines best practices for effective AI-assisted development using the vibe coding methodology.

## Table of Contents

- [AI Interaction Best Practices](#ai-interaction-best-practices)
- [Code Quality Standards](#code-quality-standards)
- [Project Organization](#project-organization)
- [Agent & Skill Usage](#agent--skill-usage)
- [Collaboration Guidelines](#collaboration-guidelines)
- [Performance Optimization](#performance-optimization)
- [Security Best Practices](#security-best-practices)
- [Common Pitfalls to Avoid](#common-pitfalls-to-avoid)

---

## AI Interaction Best Practices

### 1. Be Specific and Clear

**Good Prompt**:
```
"Create a React component for a user profile card that displays:
- User avatar (circular, 80px diameter)
- Name (bold, large font)
- Email address
- Join date (formatted as 'Joined Month Year')
- Status badge (online/offline)

Use TypeScript and Tailwind CSS. Make it responsive."
```

**Bad Prompt**:
```
"Make a user card"
```

**Why**: Specific requirements lead to better first-pass results and less iteration.

### 2. Provide Context

**Include**:
- Relevant code snippets
- Project structure
- Technology stack
- Existing patterns to follow
- Constraints and requirements

**Example**:
```
"I'm working on a Next.js 14 project with TypeScript and Prisma. 
Here's my current User model:

[prisma schema]

I need to create an API endpoint to update user profiles. 
Follow the existing pattern in /api/users/route.ts:

[existing code example]
```

### 3. Iterate Incrementally

**Strategy**: Build complex features in small, testable pieces

**Approach**:
1. Start with core functionality
2. Add error handling
3. Include edge cases
4. Optimize performance
5. Polish UI/UX

**Example Progression**:
```
Turn 1: "Create basic CRUD operations for users"
Turn 2: "Add input validation"
Turn 3: "Implement pagination and filtering"
Turn 4: "Add caching layer"
Turn 5: "Write comprehensive tests"
```

### 4. Ask for Explanations

**When to Ask**:
- Complex implementations
- Unfamiliar patterns
- Performance considerations
- Security implications
- Alternative approaches

**Example**:
```
"Explain why you chose Redux over Context API for this use case"
"What are the trade-offs of this approach?"
"Are there any security concerns with this implementation?"
```

### 5. Validate Understanding

**Technique**: Summarize what you've learned or request confirmation

**Example**:
```
"So to confirm, this approach will:
1. Fetch data server-side for SEO
2. Use SWR for client-side caching
3. Handle errors gracefully with fallbacks

Is that correct? Are there any gotchas I should know about?"
```

### 6. Provide Feedback

**Positive Feedback**:
```
"That solution works perfectly! The error handling is exactly what I needed."
```

**Constructive Feedback**:
```
"The code works, but I'd prefer if we used async/await instead of promises 
for consistency with the rest of the codebase."
```

**Why**: Helps the AI learn your preferences and improve future responses.

---

## Code Quality Standards

### 1. Follow SOLID Principles

**Single Responsibility**:
```typescript
// ✅ Good: Each function has one purpose
function validateEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function hashPassword(password: string): string {
  return bcrypt.hashSync(password, 10);
}

function createUser(userData: UserData): User {
  // Uses validateEmail and hashPassword
}

// ❌ Bad: Function does too much
function createUser(email: string, password: string) {
  // Validates email
  // Hashes password
  // Creates user
  // Sends email
  // Logs activity
}
```

**Open/Closed Principle**:
```typescript
// ✅ Good: Extensible without modification
interface PaymentProcessor {
  process(amount: number): Promise<void>;
}

class StripeProcessor implements PaymentProcessor {
  async process(amount: number) { /* ... */ }
}

class PayPalProcessor implements PaymentProcessor {
  async process(amount: number) { /* ... */ }
}

// Add new payment method without changing existing code
class CryptoProcessor implements PaymentProcessor {
  async process(amount: number) { /* ... */ }
}
```

### 2. Write Self-Documenting Code

**Naming**:
```typescript
// ✅ Clear intent
const daysUntilExpiration = 30;
const isUserAuthenticated = true;
const calculateOrderTotal = (items: Item[]) => { /* ... */ };

// ❌ Unclear
const d = 30;
const auth = true;
const calc = (i: any[]) => { /* ... */ };
```

**Function Size**:
```typescript
// ✅ Small, focused functions
function validateUserInput(input: UserInput): ValidationResult {
  // 10-15 lines max
}

function saveUserToDatabase(user: User): Promise<User> {
  // 5-10 lines
}

function sendWelcomeEmail(user: User): Promise<void> {
  // 5-10 lines
}

// ❌ Large function doing everything
function createUser(input: any) {
  // 100+ lines of mixed logic
}
```

### 3. Consistent Code Style

**Use Linting Tools**:
```json
{
  "scripts": {
    "lint": "eslint . --ext .ts,.tsx",
    "lint:fix": "eslint . --ext .ts,.tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx}\""
  }
}
```

**Configuration Example**:
```javascript
// .eslintrc.js
module.exports = {
  extends: ['airbnb-typescript', 'prettier'],
  rules: {
    'no-console': 'warn',
    '@typescript-eslint/explicit-function-return-type': 'off',
    'react/prop-types': 'off'
  }
};
```

### 4. Error Handling

**Comprehensive Error Handling**:
```typescript
// ✅ Good: Specific error handling
async function fetchUserData(userId: string): Promise<User> {
  try {
    const response = await api.get(`/users/${userId}`);
    
    if (!response.ok) {
      if (response.status === 404) {
        throw new UserNotFoundError(userId);
      }
      throw new ApiError(response.status, response.statusText);
    }
    
    return response.data;
  } catch (error) {
    if (error instanceof UserNotFoundError) {
      logger.warn(`User not found: ${userId}`);
      throw error;
    }
    
    logger.error(`Failed to fetch user ${userId}:`, error);
    throw new UserServiceError('Failed to fetch user data', { cause: error });
  }
}

// Custom error classes
class UserNotFoundError extends Error {
  constructor(userId: string) {
    super(`User not found: ${userId}`);
    this.name = 'UserNotFoundError';
  }
}
```

### 5. Type Safety

**TypeScript Best Practices**:
```typescript
// ✅ Strong typing
interface CreateUserRequest {
  email: string;
  name: string;
  age?: number;
  role: 'user' | 'admin' | 'moderator';
}

interface User {
  id: string;
  email: string;
  name: string;
  age?: number;
  role: UserRole;
  createdAt: Date;
}

enum UserRole {
  User = 'user',
  Admin = 'admin',
  Moderator = 'moderator'
}

// ❌ Avoid 'any'
function processData(data: any): any {
  // No type safety
}
```

---

## Project Organization

### 1. Directory Structure

**Recommended Structure**:
```
project/
├── src/
│   ├── components/       # Reusable UI components
│   │   ├── common/       # Shared components
│   │   ├── features/     # Feature-specific components
│   │   └── layouts/      # Layout components
│   ├── services/         # Business logic
│   │   ├── api/          # API clients
│   │   ├── auth/         # Authentication
│   │   └── data/         # Data processing
│   ├── utils/            # Utility functions
│   ├── hooks/            # Custom React hooks
│   ├── types/            # TypeScript types/interfaces
│   ├── constants/        # Constants and config
│   └── styles/           # Global styles
├── tests/
│   ├── unit/
│   ├── integration/
│   └── e2e/
├── docs/
├── config/
└── scripts/
```

### 2. Module Organization

**Feature-Based Structure**:
```
features/
├── authentication/
│   ├── components/
│   ├── services/
│   ├── hooks/
│   ├── types/
│   └── tests/
├── user-profile/
│   ├── components/
│   ├── services/
│   └── tests/
└── dashboard/
    ├── components/
    ├── services/
    └── tests/
```

**Benefits**:
- Easier to locate related code
- Better separation of concerns
- Simplifies feature removal/addition
- Improves team collaboration

### 3. File Naming Conventions

**Consistent Naming**:
```
✅ Good:
- UserProfile.tsx          # Component (PascalCase)
- useAuthentication.ts     # Hook (camelCase, prefixed with 'use')
- userService.ts           # Service (camelCase)
- user.types.ts            # Types (kebab-case with .types)
- user.test.ts             # Tests (.test.ts suffix)
- constants.ts             # Constants

❌ Bad:
- userProfile.jsx
- authentication-hook.ts
- User_Service.ts
- types_user.ts
```

### 4. Import Organization

**Standard Order**:
```typescript
// 1. External dependencies
import React from 'react';
import { useState, useEffect } from 'react';
import axios from 'axios';

// 2. Internal modules (absolute paths)
import { UserService } from '@/services/userService';
import { User } from '@/types/user';

// 3. Relative imports
import { Button } from '../common/Button';
import { formatDate } from '../../utils/date';

// 4. Styles
import styles from './UserProfile.module.css';
```

---

## Agent & Skill Usage

### 1. When to Use Agents vs Skills

**Use Agents For**:
- Defining AI persona and behavior
- Setting tool permissions
- Establishing working style
- Overall task coordination

**Use Skills For**:
- Specific workflows (code generation, testing, etc.)
- Repeatable procedures
- Domain-specific processes
- Task templates

### 2. Activating the Right Tool

**Automatic Activation**:
```
User: "I need to create a REST API for user management"
→ Agent automatically uses /code-generation skill

User: "This code is messy, can you clean it up?"
→ Agent automatically uses /refactoring skill
```

**Manual Activation**:
```
/code-generation - Create a payment processing service
/testing - Generate tests for the auth module
/refactoring - Simplify this complex function
/documentation - Document the API endpoints
```

### 3. Combining Multiple Skills

**Workflow Example**:
```
Step 1: /code-generation - Create initial implementation
Step 2: /testing - Generate test suite
Step 3: /refactoring - Improve code quality
Step 4: /documentation - Add documentation

All in one conversation session for context continuity
```

### 4. Skill Context Management

**Best Practices**:
- Batch related operations in single trigger
- Re-trigger skill for multi-turn workflows
- Don't assume skill context persists
- Reference previous outputs in conversation

**Example**:
```
Turn 1: /code-generation - Create user service
Turn 2: /testing - continue - Add tests for user service
Turn 3: /refactoring - continue - Improve user service code
```

### 5. Customizing Agents

**Project-Specific Agent**:
```markdown
---
name: project-coder
description: Specialized agent for our e-commerce platform with knowledge of our tech stack
tools:
  - Read
  - Write
  - Edit
  - Shell
---

You are an expert in our e-commerce platform built with:
- Next.js 14 with App Router
- PostgreSQL with Prisma ORM
- Redis for caching
- Stripe for payments

Always follow our coding standards:
- Use TypeScript strict mode
- Implement proper error boundaries
- Add comprehensive logging
- Follow our API versioning scheme
```

---

## Collaboration Guidelines

### 1. Version Control Best Practices

**Commit Messages**:
```bash
# Format: type(scope): description

feat(auth): add OAuth2 login support
fix(api): handle null pointer in user endpoint
docs(readme): update installation instructions
refactor(db): optimize user query performance
test(auth): add integration tests for login flow
chore(deps): update dependency versions
```

**Branch Naming**:
```bash
feature/user-authentication
bugfix/login-error
hotfix/security-patch
refactor/database-queries
release/v2.1.0
```

### 2. Code Review Process

**Before Submitting PR**:
- [ ] Self-review completed
- [ ] All tests passing
- [ ] Linter checks pass
- [ ] Documentation updated
- [ ] No console.log statements
- [ ] Meaningful commit messages

**Review Checklist**:
- Functionality: Does it work as intended?
- Quality: Is the code clean and maintainable?
- Performance: Any obvious bottlenecks?
- Security: Any vulnerabilities?
- Testing: Adequate test coverage?

### 3. Documentation Standards

**Update Docs When**:
- Adding new features
- Changing APIs
- Modifying configuration
- Fixing bugs that affect usage
- Deprecating functionality

**Documentation Locations**:
- README.md: Project overview
- docs/: Detailed guides
- Inline comments: Complex logic
- JSDoc: Public APIs
- CHANGELOG.md: Version changes

### 4. Team Communication

**Effective Handoffs**:
```markdown
## Feature: User Profile Updates

### Completed
- ✅ Backend API endpoints
- ✅ Database migrations
- ✅ Unit tests

### In Progress
- 🔄 Frontend components

### Blockers
- ⚠️ Waiting for design approval on new layout

### Next Steps
1. Complete frontend implementation
2. Integration testing
3. Deploy to staging
4. User acceptance testing

### Key Decisions
- Used GraphQL for flexible queries
- Implemented optimistic updates
- Added image compression on upload
```

---

## Performance Optimization

### 1. Frontend Performance

**React Optimization**:
```typescript
// ✅ Memoize expensive calculations
const total = useMemo(() => {
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0);
}, [items]);

// ✅ Memoize callbacks
const handleSubmit = useCallback(async (data: FormData) => {
  await submitForm(data);
}, []);

// ✅ Lazy load components
const Dashboard = lazy(() => import('./Dashboard'));

// ✅ Virtualize long lists
import { FixedSizeList } from 'react-window';
```

**Bundle Optimization**:
```javascript
// webpack.config.js
module.exports = {
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all'
        }
      }
    }
  }
};
```

### 2. Backend Performance

**Database Optimization**:
```typescript
// ✅ Use indexes
await db.collection.createIndex({ email: 1 }, { unique: true });

// ✅ Select only needed fields
const users = await db.users.findMany({
  select: {
    id: true,
    name: true,
    email: true
  },
  where: { active: true }
});

// ✅ Pagination
const users = await db.users.findMany({
  skip: (page - 1) * pageSize,
  take: pageSize,
  orderBy: { createdAt: 'desc' }
});

// ✅ Caching
import Redis from 'ioredis';
const redis = new Redis();

async function getCachedUser(userId: string) {
  const cached = await redis.get(`user:${userId}`);
  if (cached) return JSON.parse(cached);
  
  const user = await db.users.findUnique({ where: { id: userId } });
  await redis.setex(`user:${userId}`, 3600, JSON.stringify(user));
  return user;
}
```

**API Optimization**:
```typescript
// ✅ Rate limiting
import rateLimit from 'express-rate-limit';

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});

app.use('/api/', limiter);

// ✅ Compression
import compression from 'compression';
app.use(compression());

// ✅ Response caching
import cacheMiddleware from './middleware/cache';

app.get('/api/products', cacheMiddleware(300), getProductHandler);
```

### 3. Monitoring & Profiling

**Performance Metrics**:
```typescript
// Custom performance monitoring
class PerformanceMonitor {
  private metrics: Map<string, number[]> = new Map();

  startTimer(operation: string): () => void {
    const start = performance.now();
    return () => {
      const duration = performance.now() - start;
      if (!this.metrics.has(operation)) {
        this.metrics.set(operation, []);
      }
      this.metrics.get(operation)!.push(duration);
      
      if (duration > 1000) {
        console.warn(`Slow operation: ${operation} took ${duration.toFixed(2)}ms`);
      }
    };
  }

  getAverageTime(operation: string): number {
    const times = this.metrics.get(operation) || [];
    return times.length > 0
      ? times.reduce((a, b) => a + b, 0) / times.length
      : 0;
  }
}
```

---

## Security Best Practices

### 1. Input Validation

**Server-Side Validation**:
```typescript
import { z } from 'zod';

// Define validation schema
const createUserSchema = z.object({
  email: z.string().email('Invalid email format'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Password must contain uppercase letter')
    .regex(/[0-9]/, 'Password must contain number'),
  name: z.string().min(2).max(100),
  age: z.number().min(13).max(120).optional()
});

// Validate input
function createUser(input: unknown) {
  const validated = createUserSchema.parse(input);
  // Process validated data
}
```

**Client-Side Validation**:
```typescript
// Always validate on server too! Client-side is UX only
async function handleSubmit(formData: FormData) {
  // Client validation for immediate feedback
  const result = createUserSchema.safeParse(formData);
  
  if (!result.success) {
    setErrors(result.error.flatten().fieldErrors);
    return;
  }
  
  // Server will validate again
  await api.post('/users', formData);
}
```

### 2. Authentication & Authorization

**Secure Password Handling**:
```typescript
import bcrypt from 'bcrypt';

const SALT_ROUNDS = 12;

async function hashPassword(password: string): Promise<string> {
  return bcrypt.hash(password, SALT_ROUNDS);
}

async function verifyPassword(password: string, hash: string): Promise<boolean> {
  return bcrypt.compare(password, hash);
}
```

**JWT Implementation**:
```typescript
import jwt from 'jsonwebtoken';

const JWT_SECRET = process.env.JWT_SECRET!;
const JWT_EXPIRY = '24h';

function generateToken(userId: string, role: string): string {
  return jwt.sign(
    { userId, role },
    JWT_SECRET,
    { expiresIn: JWT_EXPIRY }
  );
}

function verifyToken(token: string): JwtPayload {
  try {
    return jwt.verify(token, JWT_SECRET) as JwtPayload;
  } catch (error) {
    throw new AuthenticationError('Invalid token');
  }
}

// Middleware
function authenticate(req: Request, res: Response, next: NextFunction) {
  const token = req.headers.authorization?.replace('Bearer ', '');
  
  if (!token) {
    return res.status(401).json({ error: 'Authentication required' });
  }
  
  try {
    const payload = verifyToken(token);
    req.user = payload;
    next();
  } catch (error) {
    return res.status(401).json({ error: 'Invalid token' });
  }
}
```

**Role-Based Authorization**:
```typescript
function authorize(...allowedRoles: string[]) {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user || !allowedRoles.includes(req.user.role)) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }
    next();
  };
}

// Usage
app.get('/api/admin/users',
  authenticate,
  authorize('admin'),
  getAllUsersHandler
);
```

### 3. SQL Injection Prevention

**Use Parameterized Queries**:
```typescript
// ✅ Safe: Parameterized query
const user = await db.query(
  'SELECT * FROM users WHERE email = $1',
  [email]
);

// ❌ Dangerous: String concatenation
const user = await db.query(
  `SELECT * FROM users WHERE email = '${email}'`
);
```

**ORM Protection**:
```typescript
// Prisma automatically prevents SQL injection
const user = await prisma.user.findUnique({
  where: { email: userInput }
});
```

### 4. XSS Prevention

**Sanitize Output**:
```typescript
import DOMPurify from 'dompurify';

// Sanitize HTML content
function renderUserBio(bio: string) {
  const sanitized = DOMPurify.sanitize(bio);
  return <div dangerouslySetInnerHTML={{ __html: sanitized }} />;
}

// Or better, avoid HTML entirely
function renderUserBio(bio: string) {
  return <p>{bio}</p>; // React auto-escapes
}
```

**Content Security Policy**:
```javascript
// Express helmet
import helmet from 'helmet';

app.use(helmet.contentSecurityPolicy({
  directives: {
    defaultSrc: ["'self'"],
    scriptSrc: ["'self'", "'unsafe-inline'"],
    styleSrc: ["'self'", "'unsafe-inline'"],
    imgSrc: ["'self'", 'data:', 'https:'],
  }
}));
```

### 5. Sensitive Data Protection

**Environment Variables**:
```typescript
// ✅ Good: Use environment variables
const apiKey = process.env.API_KEY;
const dbUrl = process.env.DATABASE_URL;

// ❌ Bad: Hardcoded secrets
const apiKey = 'sk-1234567890abcdef';
```

**.env File**:
```env
# .env (NEVER commit this!)
DATABASE_URL=postgresql://user:pass@localhost/db
JWT_SECRET=your-secret-key-here
API_KEY=your-api-key

# .env.example (Safe to commit)
DATABASE_URL=postgresql://user:pass@localhost/db
JWT_SECRET=change-me
API_KEY=change-me
```

**Git Ignore**:
```gitignore
# .gitignore
.env
.env.local
.env.production
*.key
*.pem
node_modules/
dist/
```

---

## Common Pitfalls to Avoid

### 1. Over-Reliance on AI

**Problem**: Accepting AI-generated code without understanding

**Solution**:
- Review all generated code
- Understand the logic before using
- Test thoroughly
- Ask for explanations when unsure

**Example**:
```
❌ "Just give me the code"
✅ "Generate the code and explain how it works"
```

### 2. Ignoring Edge Cases

**Problem**: Only testing happy path

**Solution**:
- Consider error scenarios
- Test boundary conditions
- Handle null/undefined values
- Validate input thoroughly

**Checklist**:
- [ ] Empty inputs
- [ ] Invalid formats
- [ ] Missing data
- [ ] Network failures
- [ ] Timeout scenarios
- [ ] Concurrent operations

### 3. Premature Optimization

**Problem**: Optimizing before measuring

**Solution**:
- Profile first
- Focus on readability
- Optimize bottlenecks only
- Measure impact

**Approach**:
```
1. Make it work
2. Make it right
3. Make it fast (if needed)
```

### 4. Poor Error Handling

**Problem**: Silent failures or generic errors

**Solution**:
```typescript
// ❌ Bad
try {
  await doSomething();
} catch (error) {
  console.log(error);
}

// ✅ Good
try {
  await doSomething();
} catch (error) {
  if (error instanceof NetworkError) {
    logger.error('Network failure:', error);
    throw new ServiceUnavailableError('External service unavailable');
  }
  
  if (error instanceof ValidationError) {
    logger.warn('Validation failed:', error);
    throw error;
  }
  
  logger.error('Unexpected error:', error);
  throw new InternalServerError('An unexpected error occurred');
}
```

### 5. Inconsistent Code Style

**Problem**: Mixed formatting and patterns

**Solution**:
- Use linters and formatters
- Enforce via pre-commit hooks
- Team agreement on standards
- Automated CI checks

**Setup**:
```json
{
  "husky": {
    "hooks": {
      "pre-commit": "lint-staged"
    }
  },
  "lint-staged": {
    "*.{ts,tsx}": [
      "eslint --fix",
      "prettier --write"
    ]
  }
}
```

### 6. Insufficient Testing

**Problem**: Low test coverage or meaningless tests

**Solution**:
- Aim for 80%+ coverage
- Test edge cases
- Use meaningful assertions
- Mock appropriately

**Test Quality**:
```typescript
// ❌ Weak test
it('should work', () => {
  const result = someFunction();
  expect(result).toBeTruthy();
});

// ✅ Strong test
it('should calculate total with tax', () => {
  const items = [{ price: 100, quantity: 2 }];
  const taxRate = 0.08;
  
  const total = calculateTotal(items, taxRate);
  
  expect(total).toBe(216); // 200 + 16 tax
});
```

### 7. Not Updating Documentation

**Problem**: Code changes but docs don't

**Solution**:
- Update docs as part of PR
- Automate API doc generation
- Keep README current
- Document breaking changes

**Checklist**:
- [ ] README updated
- [ ] API docs regenerated
- [ ] CHANGELOG entry added
- [ ] Migration guide if needed
- [ ] Code comments current

### 8. Tight Coupling

**Problem**: Components too dependent on each other

**Solution**:
- Use interfaces/abstractions
- Dependency injection
- Event-driven architecture
- Clear module boundaries

**Example**:
```typescript
// ❌ Tightly coupled
class OrderService {
  private emailService = new EmailService();
  private paymentService = new PaymentService();
  
  async placeOrder(order: Order) {
    await this.paymentService.charge(order);
    await this.emailService.sendConfirmation(order);
  }
}

// ✅ Loosely coupled
interface NotificationService {
  sendOrderConfirmation(order: Order): Promise<void>;
}

interface PaymentGateway {
  charge(amount: number): Promise<void>;
}

class OrderService {
  constructor(
    private paymentGateway: PaymentGateway,
    private notificationService: NotificationService
  ) {}
  
  async placeOrder(order: Order) {
    await this.paymentGateway.charge(order.total);
    await this.notificationService.sendOrderConfirmation(order);
  }
}
```

---

## Quick Reference

### Essential Commands

```bash
# Development
npm run dev              # Start dev server
npm run build            # Production build
npm run lint             # Check code style
npm run lint:fix         # Fix code style issues
npm run format           # Format code

# Testing
npm test                 # Run all tests
npm run test:watch       # Watch mode
npm run test:coverage    # Coverage report

# Database
npm run migrate          # Run migrations
npm run migrate:rollback # Rollback migrations
npm run seed             # Seed database

# Docker
docker-compose up        # Start services
docker-compose down      # Stop services
```

### Git Workflow

```bash
# Start feature
git checkout -b feature/description

# Commit changes
git add .
git commit -m "type(scope): description"

# Push and create PR
git push origin feature/description

# After merge
git checkout main
git pull origin main
git branch -d feature/description
```

### AI Assistant Tips

```
/code-generation     # Generate code
/testing             # Create tests
/refactoring         # Improve code
/documentation       # Generate docs

Be specific, provide context, iterate incrementally
```

---

## Additional Resources

- [Vibe Coding README](../README.md)
- [Workflow Guide](workflow.md)
- [Tongyi Lingma Docs](https://help.aliyun.com/zh/lingma/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [React Best Practices](https://react.dev/learn)
- [Node.js Best Practices](https://github.com/goldbergyoni/nodebestpractices)
