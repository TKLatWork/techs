---
name: code-generation
description: Generates production-ready code from natural language requirements with proper structure, error handling, and documentation
---

# Code Generation Skill

## Purpose
This skill enables agents to transform user requirements into well-structured, tested, and documented code following industry best practices.

## When to Use
- Creating new features from specifications
- Generating boilerplate or scaffold code
- Implementing algorithms or data structures
- Building components, services, or utilities
- Converting pseudocode to actual implementation

## Workflow

### Step 1: Requirements Analysis
1. Parse user requirements carefully
2. Identify key functionalities and constraints
3. Ask clarifying questions if ambiguous
4. Determine technology stack and dependencies
5. Consider edge cases and error scenarios

### Step 2: Design Proposal
1. Propose architecture/approach (for complex tasks)
2. Outline file structure if creating multiple files
3. Identify interfaces and data models
4. Suggest design patterns if applicable
5. Get user confirmation before proceeding

### Step 3: Implementation
1. Generate clean, readable code
2. Follow language/framework conventions
3. Add comprehensive error handling
4. Include input validation
5. Write meaningful variable/function names
6. Add inline comments for complex logic

### Step 4: Documentation
1. Add JSDoc/docstring comments for functions
2. Include usage examples in comments
3. Document parameters and return values
4. Note any assumptions or limitations
5. Reference related files or resources

### Step 5: Quality Checks
1. Validate code syntax using get_problems
2. Check for common pitfalls and anti-patterns
3. Ensure consistent code style
4. Verify all imports/dependencies are correct
5. Test edge cases mentally

### Step 6: Delivery
1. Present complete, runnable code
2. Explain key implementation decisions
3. Provide usage instructions
4. Suggest testing approach
5. Recommend next steps or improvements

## Output Template

```[language]
// File: [filename]
// Description: [brief description]

[code implementation with comments]
```

**Key Features:**
- [Feature 1]
- [Feature 2]

**Usage Example:**
```[language]
[example code]
```

**Notes:**
- [Important considerations]
- [Limitations or caveats]

## Best Practices

### Code Quality
- Keep functions small and focused (single responsibility)
- Use meaningful names that express intent
- Avoid deep nesting (max 3 levels)
- Handle errors gracefully with specific messages
- Follow DRY principle but don't over-abstract

### Performance
- Consider time and space complexity
- Avoid unnecessary computations
- Use appropriate data structures
- Cache results when beneficial
- Profile critical paths if needed

### Security
- Validate and sanitize all inputs
- Never expose sensitive data in logs
- Use parameterized queries for databases
- Implement proper authentication/authorization
- Follow OWASP guidelines

### Maintainability
- Write self-documenting code
- Keep dependencies minimal
- Use established patterns
- Make code easy to test
- Consider future extensibility

## Examples

### Example 1: Simple Utility Function
**User Request**: "Create a function to validate email addresses"

**Response**:
```javascript
// File: utils/emailValidator.js
// Description: Validates email format using regex pattern

/**
 * Validates an email address format
 * @param {string} email - The email address to validate
 * @returns {boolean} True if email is valid, false otherwise
 */
function isValidEmail(email) {
  // Email regex pattern following RFC 5322 standard
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  
  // Check for null/undefined and type
  if (!email || typeof email !== 'string') {
    return false;
  }
  
  return emailPattern.test(email.trim());
}

export { isValidEmail };
```

**Usage Example:**
```javascript
import { isValidEmail } from './utils/emailValidator';

console.log(isValidEmail('user@example.com')); // true
console.log(isValidEmail('invalid-email'));     // false
```

### Example 2: React Component
**User Request**: "Create a button component with loading state"

**Response**:
```tsx
// File: components/LoadingButton.tsx
// Description: Button component with built-in loading state management

import React from 'react';

interface LoadingButtonProps {
  label: string;
  onClick: () => Promise<void>;
  disabled?: boolean;
  variant?: 'primary' | 'secondary' | 'danger';
}

const LoadingButton: React.FC<LoadingButtonProps> = ({
  label,
  onClick,
  disabled = false,
  variant = 'primary'
}) => {
  const [isLoading, setIsLoading] = React.useState(false);

  const handleClick = async () => {
    try {
      setIsLoading(true);
      await onClick();
    } catch (error) {
      console.error('Button action failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const baseStyles = 'px-4 py-2 rounded font-medium transition-all';
  const variantStyles = {
    primary: 'bg-blue-500 text-white hover:bg-blue-600',
    secondary: 'bg-gray-500 text-white hover:bg-gray-600',
    danger: 'bg-red-500 text-white hover:bg-red-600'
  };

  return (
    <button
      className={`${baseStyles} ${variantStyles[variant]} ${
        (disabled || isLoading) ? 'opacity-50 cursor-not-allowed' : ''
      }`}
      onClick={handleClick}
      disabled={disabled || isLoading}
    >
      {isLoading ? (
        <span className="flex items-center gap-2">
          <span className="animate-spin">⏳</span>
          Loading...
        </span>
      ) : (
        label
      )}
    </button>
  );
};

export default LoadingButton;
```

## Common Patterns

### Pattern 1: CRUD Operations
When implementing CRUD, always include:
- Input validation
- Error handling with specific messages
- Proper HTTP status codes
- Pagination for list operations
- Optimistic updates where appropriate

### Pattern 2: API Integration
For API calls, ensure:
- Timeout handling
- Retry logic for transient failures
- Request/response logging
- Proper error propagation
- Type safety with TypeScript interfaces

### Pattern 3: State Management
When managing state:
- Use appropriate state container (Redux, Context, etc.)
- Normalize data structure
- Handle loading/error states
- Implement proper cleanup
- Avoid unnecessary re-renders

## Anti-Patterns to Avoid

❌ **Don't**:
- Write giant functions (>50 lines)
- Use magic numbers without explanation
- Ignore error handling
- Create tight coupling between modules
- Duplicate code instead of abstracting
- Use `any` type in TypeScript unnecessarily

✅ **Do**:
- Break complex logic into smaller functions
- Use constants for magic values
- Handle errors at appropriate levels
- Design loose coupling with clear interfaces
- Extract reusable utilities
- Use specific types and interfaces

## Tips for Better Code Generation

1. **Be Specific**: Provide detailed requirements including edge cases
2. **Context Matters**: Share relevant existing code or patterns
3. **Iterate**: Start simple, then add complexity based on feedback
4. **Test Early**: Generate tests alongside implementation
5. **Document Decisions**: Explain why certain approaches were chosen
6. **Consider Scale**: Think about performance at scale
7. **Future-Proof**: Design for extensibility when appropriate

## Related Skills
- `/testing` - Generate tests for generated code
- `/refactoring` - Improve code quality post-generation
- `/documentation` - Create comprehensive documentation
