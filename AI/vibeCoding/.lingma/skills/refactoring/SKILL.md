---
name: refactoring
description: Improves code quality, readability, and maintainability through systematic refactoring techniques while preserving functionality
---

# Refactoring Skill

## Purpose
This skill enables agents to systematically improve existing code by applying refactoring techniques that enhance readability, maintainability, performance, and adherence to best practices without changing external behavior.

## When to Use
- Improving legacy or poorly structured code
- Reducing code duplication (DRY principle)
- Simplifying complex functions or classes
- Applying design patterns appropriately
- Improving naming and documentation
- Optimizing performance bottlenecks
- Preparing code for new features
- Code review follow-up improvements

## Workflow

### Step 1: Analysis
1. Read and understand current implementation
2. Identify code smells and anti-patterns
3. Assess complexity and coupling
4. Check test coverage before refactoring
5. Document current behavior for verification

### Step 2: Planning
1. Define refactoring goals and scope
2. Choose appropriate refactoring techniques
3. Plan incremental changes if large refactor
4. Identify potential risks and mitigations
5. Ensure tests are in place to catch regressions

### Step 3: Execution
1. Apply small, focused changes
2. Run tests after each change
3. Maintain backward compatibility when needed
4. Update related code and dependencies
5. Keep commit history clean and logical

### Step 4: Validation
1. Verify all tests still pass
2. Check for unintended side effects
3. Validate performance hasn't degraded
4. Review code for consistency
5. Get peer review if significant changes

### Step 5: Documentation
1. Update comments and documentation
2. Note any API changes or breaking changes
3. Document rationale for major changes
4. Update README or architecture docs if needed
5. Create migration guide if applicable

## Common Refactoring Techniques

### 1. Extract Function/Method
**Problem**: Long function doing too much
**Solution**: Break into smaller, focused functions

**Before:**
```javascript
function processOrder(order) {
  // Validate order
  if (!order.items || order.items.length === 0) {
    throw new Error('Order must have items');
  }
  
  // Calculate total
  let total = 0;
  for (const item of order.items) {
    total += item.price * item.quantity;
  }
  
  // Apply discount
  if (order.customer.isVip) {
    total *= 0.9;
  }
  
  // Calculate tax
  const tax = total * 0.08;
  total += tax;
  
  // Save to database
  database.save({ ...order, total, tax });
  
  // Send confirmation
  emailService.send(order.customer.email, 'Order Confirmed');
  
  return { success: true, total };
}
```

**After:**
```javascript
function processOrder(order) {
  validateOrder(order);
  
  const subtotal = calculateSubtotal(order.items);
  const discountedTotal = applyDiscount(subtotal, order.customer);
  const tax = calculateTax(discountedTotal);
  const finalTotal = discountedTotal + tax;
  
  saveOrder(order, finalTotal, tax);
  sendConfirmation(order.customer.email);
  
  return { success: true, total: finalTotal };
}

function validateOrder(order) {
  if (!order.items || order.items.length === 0) {
    throw new Error('Order must have items');
  }
}

function calculateSubtotal(items) {
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0);
}

function applyDiscount(subtotal, customer) {
  return customer.isVip ? subtotal * 0.9 : subtotal;
}

function calculateTax(amount) {
  return amount * 0.08;
}

function saveOrder(order, total, tax) {
  database.save({ ...order, total, tax });
}

function sendConfirmation(email) {
  emailService.send(email, 'Order Confirmed');
}
```

### 2. Rename for Clarity
**Problem**: Unclear variable/function names
**Solution**: Use descriptive, intention-revealing names

**Before:**
```javascript
function proc(d) {
  let t = 0;
  for (let i = 0; i < d.length; i++) {
    t += d[i].p * d[i].q;
  }
  return t;
}
```

**After:**
```javascript
function calculateOrderTotal(orderItems) {
  let totalAmount = 0;
  for (let i = 0; i < orderItems.length; i++) {
    totalAmount += orderItems[i].price * orderItems[i].quantity;
  }
  return totalAmount;
}
```

### 3. Replace Conditional with Polymorphism
**Problem**: Complex conditional logic
**Solution**: Use polymorphism or strategy pattern

**Before:**
```javascript
function getShippingCost(shippingMethod, weight) {
  if (shippingMethod === 'standard') {
    return weight * 2;
  } else if (shippingMethod === 'express') {
    return weight * 5;
  } else if (shippingMethod === 'overnight') {
    return weight * 10;
  }
}
```

**After:**
```javascript
class ShippingStrategy {
  calculateCost(weight) {
    throw new Error('Must implement calculateCost');
  }
}

class StandardShipping extends ShippingStrategy {
  calculateCost(weight) {
    return weight * 2;
  }
}

class ExpressShipping extends ShippingStrategy {
  calculateCost(weight) {
    return weight * 5;
  }
}

class OvernightShipping extends ShippingStrategy {
  calculateCost(weight) {
    return weight * 10;
  }
}

const shippingStrategies = {
  standard: new StandardShipping(),
  express: new ExpressShipping(),
  overnight: new OvernightShipping()
};

function getShippingCost(shippingMethod, weight) {
  const strategy = shippingStrategies[shippingMethod];
  if (!strategy) {
    throw new Error(`Unknown shipping method: ${shippingMethod}`);
  }
  return strategy.calculateCost(weight);
}
```

### 4. Remove Duplication (DRY)
**Problem**: Repeated code blocks
**Solution**: Extract common logic

**Before:**
```javascript
function formatUserForDisplay(user) {
  return `${user.firstName} ${user.lastName} - ${user.email}`;
}

function formatAdminForDisplay(admin) {
  return `${admin.firstName} ${admin.lastName} - ${admin.email}`;
}

function formatCustomerForDisplay(customer) {
  return `${customer.firstName} ${customer.lastName} - ${customer.email}`;
}
```

**After:**
```javascript
function formatPersonForDisplay(person) {
  return `${person.firstName} ${person.lastName} - ${person.email}`;
}

// Usage
formatPersonForDisplay(user);
formatPersonForDisplay(admin);
formatPersonForDisplay(customer);
```

### 5. Simplify Boolean Logic
**Problem**: Complex nested conditionals
**Solution**: Use guard clauses and early returns

**Before:**
```javascript
function processPayment(payment) {
  if (payment.amount > 0) {
    if (payment.method !== null) {
      if (payment.currency === 'USD' || payment.currency === 'EUR') {
        if (!payment.isExpired) {
          // Process payment
          return executePayment(payment);
        } else {
          throw new Error('Payment expired');
        }
      } else {
        throw new Error('Unsupported currency');
      }
    } else {
      throw new Error('Payment method required');
    }
  } else {
    throw new Error('Invalid amount');
  }
}
```

**After:**
```javascript
function processPayment(payment) {
  if (payment.amount <= 0) {
    throw new Error('Invalid amount');
  }
  
  if (!payment.method) {
    throw new Error('Payment method required');
  }
  
  if (!['USD', 'EUR'].includes(payment.currency)) {
    throw new Error('Unsupported currency');
  }
  
  if (payment.isExpired) {
    throw new Error('Payment expired');
  }
  
  return executePayment(payment);
}
```

### 6. Introduce Parameter Object
**Problem**: Too many parameters
**Solution**: Group related parameters into an object

**Before:**
```javascript
function createUser(firstName, lastName, email, age, phone, address, city, state, zip) {
  // Implementation
}
```

**After:**
```javascript
function createUser(userData) {
  const { firstName, lastName, email, age, phone, address } = userData;
  // Implementation
}

// Usage
createUser({
  firstName: 'John',
  lastName: 'Doe',
  email: 'john@example.com',
  age: 30,
  phone: '555-1234',
  address: {
    street: '123 Main St',
    city: 'Springfield',
    state: 'IL',
    zip: '62701'
  }
});
```

## Output Template

## Refactoring Summary

**File(s):** `[file paths]`
**Goal:** [Brief description of refactoring goal]

### Changes Made

#### 1. [Change Title]
**Before:**
```[language]
[original code]
```

**After:**
```[language]
[refactored code]
```

**Rationale:** [Why this change improves the code]

#### 2. [Change Title]
[Repeat for each significant change]

### Benefits
- ✅ [Benefit 1: e.g., Improved readability]
- ✅ [Benefit 2: e.g., Reduced complexity]
- ✅ [Benefit 3: e.g., Better maintainability]

### Testing
- All existing tests pass: ✅
- No behavioral changes: ✅
- Performance impact: [None/Negligible/Improved]

### Notes
- [Any important considerations]
- [Breaking changes if any]
- [Migration steps if needed]

## Code Smells to Detect

### 1. Long Method/Function
**Indicator**: Function > 20-30 lines
**Fix**: Extract Method, Decompose Conditional

### 2. Large Class
**Indicator**: Class with too many responsibilities
**Fix**: Extract Class, Extract Subclass

### 3. Duplicate Code
**Indicator**: Same/similar code in multiple places
**Fix**: Extract Method, Pull Up Method, Form Template Method

### 4. Long Parameter List
**Indicator**: Function with > 3-4 parameters
**Fix**: Introduce Parameter Object, Preserve Whole Object

### 5. Feature Envy
**Indicator**: Method uses other class's data more than its own
**Fix**: Move Method, Extract Method

### 6. Data Clumps
**Indicator**: Groups of data that appear together repeatedly
**Fix**: Extract Class, Introduce Parameter Object

### 7. Switch Statements
**Indicator**: Complex switch/if-else chains
**Fix**: Replace with Polymorphism, Strategy Pattern

### 8. Comments as Crutches
**Indicator**: Comments explaining what code does (not why)
**Fix**: Rename, Extract Method, Introduce Assertion

## Language-Specific Refactoring

### JavaScript/TypeScript

#### Convert Callbacks to Promises/Async-Await
**Before:**
```javascript
function getUser(id, callback) {
  database.find(id, function(err, user) {
    if (err) {
      callback(err);
    } else {
      getProfile(user.profileId, function(err, profile) {
        if (err) {
          callback(err);
        } else {
          callback(null, { ...user, profile });
        }
      });
    }
  });
}
```

**After:**
```javascript
async function getUser(id) {
  const user = await database.find(id);
  const profile = await getProfile(user.profileId);
  return { ...user, profile };
}
```

#### Modernize Syntax
**Before:**
```javascript
var self = this;
var items = [];
for (var i = 0; i < data.length; i++) {
  items.push(data[i].name);
}
```

**After:**
```javascript
const items = data.map(item => item.name);
```

### Python

#### Use List Comprehensions
**Before:**
```python
squares = []
for x in range(10):
    if x % 2 == 0:
        squares.append(x ** 2)
```

**After:**
```python
squares = [x ** 2 for x in range(10) if x % 2 == 0]
```

#### Use Context Managers
**Before:**
```python
file = open('data.txt', 'r')
try:
    content = file.read()
finally:
    file.close()
```

**After:**
```python
with open('data.txt', 'r') as file:
    content = file.read()
```

### Java

#### Use Streams
**Before:**
```java
List<String> names = new ArrayList<>();
for (User user : users) {
    if (user.isActive()) {
        names.add(user.getName().toUpperCase());
    }
}
```

**After:**
```java
List<String> names = users.stream()
    .filter(User::isActive)
    .map(user -> user.getName().toUpperCase())
    .collect(Collectors.toList());
```

## Refactoring Safety Checklist

### Before Refactoring
- [ ] Tests are in place and passing
- [ ] Current behavior is documented
- [ ] Backup or version control snapshot created
- [ ] Scope and goals are defined
- [ ] Potential risks identified

### During Refactoring
- [ ] Small, incremental changes
- [ ] Tests run after each change
- [ ] No functionality changes (unless intentional)
- [ ] Code style remains consistent
- [ ] Dependencies updated appropriately

### After Refactoring
- [ ] All tests pass
- [ ] Code reviewed (if significant)
- [ ] Performance validated
- [ ] Documentation updated
- [ ] Team notified of changes

## Performance Considerations

### When to Optimize
1. **Profile First**: Don't optimize blindly
2. **Focus on Hot Paths**: Optimize frequently executed code
3. **Measure Impact**: Verify optimization actually helps
4. **Maintain Readability**: Don't sacrifice clarity for marginal gains

### Common Optimizations
- Cache expensive computations
- Use appropriate data structures
- Reduce algorithmic complexity
- Minimize I/O operations
- Lazy load when appropriate

## Anti-Patterns to Avoid

❌ **Don't**:
- Refactor without tests
- Change behavior unintentionally
- Make huge changes in one commit
- Optimize prematurely
- Ignore team conventions
- Over-engineer simple solutions

✅ **Do**:
- Test thoroughly before and after
- Keep commits small and focused
- Document reasoning for changes
- Profile before optimizing
- Follow team standards
- Prefer simplicity over cleverness

## Tools and Automation

### Automated Refactoring Tools
- **ESLint/Prettier**: Auto-format and fix style issues
- **SonarQube**: Detect code smells and complexity
- **Refactoring tools**: IDE built-in refactorings
- **Code metrics**: Cyclomatic complexity, maintainability index

### Manual Review Focus Areas
- Business logic correctness
- Architecture decisions
- Design pattern application
- Edge case handling
- Security considerations

## Related Skills
- `/code-generation` - Generate improved code from scratch
- `/testing` - Ensure tests cover refactored code
- `/documentation` - Document architectural changes
