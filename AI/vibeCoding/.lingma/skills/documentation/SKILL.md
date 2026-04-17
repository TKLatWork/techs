---
name: documentation
description: Generates comprehensive documentation including API docs, README files, code comments, and technical guides following best practices
---

# Documentation Skill

## Purpose
This skill enables agents to create clear, comprehensive, and maintainable documentation that helps developers understand, use, and contribute to codebases effectively. It covers various documentation types from inline comments to full technical guides.

## When to Use
- Creating or updating project README files
- Generating API documentation
- Adding inline code comments and docstrings
- Writing technical guides and tutorials
- Documenting architecture decisions (ADRs)
- Creating contribution guidelines
- Generating changelogs and release notes
- Improving existing documentation quality

## Workflow

### Step 1: Audience Analysis
1. Identify target audience (developers, users, stakeholders)
2. Determine their knowledge level and needs
3. Understand what questions they need answered
4. Choose appropriate tone and depth
5. Select relevant documentation types

### Step 2: Content Planning
1. Outline documentation structure
2. Identify key topics and sections
3. Gather necessary information and examples
4. Plan code samples and diagrams
5. Determine format (Markdown, JSDoc, etc.)

### Step 3: Writing
1. Write clear, concise content
2. Use active voice and present tense
3. Include practical examples
4. Add visual aids when helpful
5. Maintain consistent style and terminology

### Step 4: Review & Refine
1. Check for clarity and completeness
2. Verify accuracy of technical details
3. Test all code examples
4. Ensure proper formatting
5. Get feedback if possible

### Step 5: Maintenance Plan
1. Document update procedures
2. Link related documentation
3. Version control considerations
4. Deprecation notices if needed
5. Feedback mechanisms

## Documentation Types

### 1. README Files
**Purpose**: Project overview and quick start guide
**Audience**: New developers and users
**Key Sections**:
- Project title and description
- Features and capabilities
- Installation instructions
- Quick start guide
- Usage examples
- Configuration options
- Contributing guidelines
- License information

**Template:**
```markdown
# [Project Name]

[![License](badge-url)](license-url)
[![Version](badge-url)](version-url)
[![Build Status](badge-url)](build-url)

## Overview

[Brief description of what this project does and why it exists]

## Features

- ✨ [Feature 1]
- ✨ [Feature 2]
- ✨ [Feature 3]

## Quick Start

### Prerequisites

- [Requirement 1]
- [Requirement 2]

### Installation

```bash
[installation commands]
```

### Basic Usage

```[language]
[basic usage example]
```

## Documentation

For detailed documentation, see [docs/](docs/) or visit our [documentation site](url).

## Examples

Check out the [examples directory](examples/) for more use cases.

## API Reference

See [API.md](API.md) for complete API documentation.

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Testing

```bash
[test commands]
```

## License

This project is licensed under the [License Name] - see [LICENSE](LICENSE) for details.

## Support

- 📧 Email: support@example.com
- 💬 Discord: [Join our community](link)
- 🐛 Issues: [Report bugs](issues-url)

## Acknowledgments

- [Credit to contributors, libraries, etc.]
```

### 2. API Documentation
**Purpose**: Document public APIs and interfaces
**Audience**: Developers integrating with your API
**Key Elements**:
- Endpoint/method signatures
- Parameter descriptions
- Return value specifications
- Error codes and messages
- Usage examples
- Authentication requirements
- Rate limiting info

**JSDoc Example:**
```javascript
/**
 * Creates a new user account with validation
 * 
 * @async
 * @param {Object} userData - The user registration data
 * @param {string} userData.email - Valid email address (must be unique)
 * @param {string} userData.password - Password (min 8 chars, must include uppercase, lowercase, number)
 * @param {string} userData.name - Display name (2-50 characters)
 * @param {Object} [options] - Optional configuration
 * @param {boolean} [options.sendWelcomeEmail=true] - Whether to send welcome email
 * @param {string} [options.role='user'] - User role ('user' | 'admin' | 'moderator')
 * 
 * @returns {Promise<User>} The created user object (excluding password)
 * @returns {string} User.id - Unique user identifier
 * @returns {string} User.email - User's email address
 * @returns {string} User.name - User's display name
 * @returns {Date} User.createdAt - Account creation timestamp
 * 
 * @throws {ValidationError} If input data fails validation
 * @throws {DuplicateError} If email already exists
 * @throws {DatabaseError} If database operation fails
 * 
 * @example
 * ```javascript
 * const user = await createUser({
 *   email: 'john@example.com',
 *   password: 'SecurePass123!',
 *   name: 'John Doe'
 * });
 * 
 * console.log(user.id); // "usr_123abc"
 * ```
 * 
 * @see {@link validateEmail} for email validation logic
 * @see {@link hashPassword} for password hashing
 * 
 * @since 1.0.0
 * @author Jane Smith
 */
async function createUser(userData, options = {}) {
  // Implementation
}
```

**Python Docstring Example:**
```python
def calculate_order_total(items: list[dict], discount_code: str = None) -> float:
    """
    Calculate the total price for an order with optional discount.
    
    Args:
        items: List of item dictionaries with 'price' and 'quantity' keys
        discount_code: Optional promotional discount code
        
    Returns:
        Final order total after applying discounts and tax
        
    Raises:
        ValueError: If items list is empty or contains invalid data
        InvalidDiscountError: If discount code is expired or invalid
        
    Example:
        >>> items = [
        ...     {'price': 29.99, 'quantity': 2},
        ...     {'price': 15.50, 'quantity': 1}
        ... ]
        >>> total = calculate_order_total(items, 'SUMMER20')
        >>> print(f"Total: ${total:.2f}")
        Total: $60.38
        
    Note:
        Tax rate is automatically applied based on shipping address.
        Discounts are applied before tax calculation.
        
    See Also:
        apply_discount: Function for applying discount codes
        calculate_tax: Function for calculating tax amount
    """
    # Implementation
```

### 3. Architecture Documentation
**Purpose**: Document system design and architectural decisions
**Audience**: Developers and architects
**Key Sections**:
- System overview diagram
- Component descriptions
- Data flow diagrams
- Technology stack rationale
- Design patterns used
- Scalability considerations
- Security architecture

**Template:**
```markdown
# Architecture Documentation

## System Overview

[High-level description of system architecture]

```
[ASCII or embedded diagram showing system components]
```

## Components

### [Component Name]

**Purpose**: [What this component does]

**Responsibilities**:
- [Responsibility 1]
- [Responsibility 2]

**Technologies**: [Tech stack used]

**Interfaces**:
- Input: [What it receives]
- Output: [What it produces]

**Dependencies**:
- [Dependency 1]
- [Dependency 2]

## Data Flow

[Description of how data moves through the system]

```
[Sequence diagram or flow chart]
```

## Design Decisions

### Decision 1: [Title]

**Context**: [Why this decision was needed]

**Options Considered**:
1. [Option A] - Pros/Cons
2. [Option B] - Pros/Cons

**Decision**: [What was chosen]

**Rationale**: [Why this option was selected]

**Consequences**: [Impact of this decision]

## Scalability

[How the system scales horizontally/vertically]

## Security

[Security measures and considerations]

## Deployment

[Deployment architecture and process]
```

### 4. Technical Guides/Tutorials
**Purpose**: Step-by-step instructions for specific tasks
**Audience**: Developers learning to use the system
**Structure**:
- Learning objectives
- Prerequisites
- Step-by-step instructions
- Code examples at each step
- Common pitfalls and solutions
- Next steps

**Template:**
```markdown
# [Guide Title]

## Learning Objectives

By the end of this guide, you will be able to:
- [Objective 1]
- [Objective 2]
- [Objective 3]

## Prerequisites

Before starting, ensure you have:
- [Requirement 1]
- [Requirement 2]
- [Basic knowledge of X]

**Estimated Time**: [X minutes/hours]

## Step 1: [Step Title]

[Explanation of what we're doing and why]

```[language]
[code example]
```

**Expected Output**:
```
[what you should see]
```

💡 **Tip**: [Helpful hint]

## Step 2: [Step Title]

[Continue with next step...]

## Common Issues

### Issue 1: [Problem Description]

**Symptoms**: [What you'll see]

**Cause**: [Why it happens]

**Solution**: [How to fix it]

```[language]
[fix code]
```

## Complete Example

Here's the complete working example:

```[language]
[full code example]
```

## Next Steps

Now that you've completed this guide, try:
- [Advanced topic 1]
- [Related tutorial link]
- [Real-world application idea]

## Additional Resources

- [Link to API docs]
- [Link to related guides]
- [Link to community forums]

## Feedback

Was this guide helpful? [Link to feedback form]
```

### 5. CONTRIBUTING.md
**Purpose**: Guide for potential contributors
**Key Sections**:
- Code of conduct
- How to submit issues
- Pull request process
- Coding standards
- Testing requirements
- Commit message conventions
- Development setup

**Template:**
```markdown
# Contributing to [Project Name]

Thank you for your interest in contributing! 🎉

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Community](#community)

## Code of Conduct

Please read our [Code of Conduct](CODE_OF_CONDUCT.md) before participating.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/PROJECT.git`
3. Create a branch: `git checkout -b feature/your-feature-name`

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce** the behavior
- **Expected vs actual behavior**
- **Screenshots** if applicable
- **Environment details** (OS, version, etc.)

**Example:**
```
**Bug**: Login fails with valid credentials

**Steps to Reproduce**:
1. Navigate to /login
2. Enter valid email and password
3. Click "Login"

**Expected**: Redirect to dashboard
**Actual**: Error message "Invalid credentials"

**Environment**: Chrome 120, Windows 11
```

### Suggesting Features

Feature suggestions should include:
- **Use case**: Why is this feature needed?
- **Proposed solution**: How should it work?
- **Alternatives considered**: Other approaches

## Development Setup

### Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0
- [Other requirements]

### Installation

```bash
# Install dependencies
npm install

# Set up environment variables
cp .env.example .env

# Start development server
npm run dev
```

## Coding Standards

### General Guidelines

- Follow existing code style
- Write meaningful commit messages
- Keep functions small and focused
- Add comments for complex logic
- Use TypeScript/types where applicable

### JavaScript/TypeScript

- Use ESLint configuration: `npm run lint`
- Follow Airbnb Style Guide
- Use async/await over promises
- Prefer const over let
- Use descriptive variable names

### Python

- Follow PEP 8
- Use Black for formatting: `black .`
- Add type hints
- Write docstrings for public functions

## Testing

### Running Tests

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### Writing Tests

- Write tests for new features
- Maintain >80% code coverage
- Use descriptive test names
- Follow AAA pattern (Arrange-Act-Assert)

**Example:**
```javascript
describe('UserService', () => {
  it('should create user with valid data', async () => {
    // Arrange
    const userData = { email: 'test@example.com', name: 'Test' };
    
    // Act
    const user = await userService.create(userData);
    
    // Assert
    expect(user.email).toBe('test@example.com');
  });
});
```

## Submitting Changes

### Pull Request Process

1. **Update documentation** if needed
2. **Add tests** for new functionality
3. **Ensure all tests pass**: `npm test`
4. **Run linter**: `npm run lint`
5. **Submit PR** with clear description

### Pull Request Template

```markdown
## Description

[Brief description of changes]

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing

- [ ] Tests added/updated
- [ ] All tests passing
- [ ] Manual testing completed

## Checklist

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No console.log statements
```

## Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

[optional body]

[optional footer]
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Example**:
```
feat(auth): add OAuth2 login support

- Implement Google OAuth2 integration
- Add OAuth2 callback endpoint
- Update user model with provider field

Closes #123
```

## Community

- 💬 Join our [Discord server](link)
- 📧 Email: contributors@example.com
- 🐦 Twitter: [@project](link)

## Questions?

Feel free to open an issue with the "question" label!
```

### 6. CHANGELOG.md
**Purpose**: Track changes between versions
**Format**: Follow [Keep a Changelog](https://keepachangelog.com/)

**Template:**
```markdown
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- [Feature being developed]

### Changed
- [Changes in progress]

## [2.1.0] - 2026-04-18

### Added
- OAuth2 authentication support (#123)
- New dashboard analytics widgets
- Export to CSV functionality

### Changed
- Improved error messages for API failures
- Updated dependency versions
- Enhanced mobile responsiveness

### Deprecated
- Legacy API v1 endpoints (will be removed in v3.0)

### Removed
- Deprecated user roles system

### Fixed
- Memory leak in WebSocket connections (#145)
- Incorrect timezone handling in reports
- Button alignment on Safari browser

### Security
- Updated JWT token validation
- Fixed XSS vulnerability in user profiles

## [2.0.0] - 2026-03-01

### Added
- Complete API redesign with RESTful conventions
- GraphQL support for complex queries
- Real-time notifications system
- Multi-language support (i18n)

### Changed
- **Breaking**: Authentication now requires API keys
- Database schema migration for performance
- Refactored user permission system

### Removed
- **Breaking**: SOAP API support discontinued
- Legacy session management

### Fixed
- Critical security patch for authentication bypass
- Race condition in concurrent updates

## [1.5.2] - 2026-02-15

### Fixed
- Hotfix for production database connection timeout
- Corrected pagination offset calculation

[Unreleased]: https://github.com/project/repo/compare/v2.1.0...HEAD
[2.1.0]: https://github.com/project/repo/compare/v2.0.0...v2.1.0
[2.0.0]: https://github.com/project/repo/compare/v1.5.2...v2.0.0
[1.5.2]: https://github.com/project/repo/releases/tag/v1.5.2
```

## Documentation Best Practices

### Writing Style

✅ **Do**:
- Use clear, simple language
- Write in active voice
- Be concise but complete
- Use consistent terminology
- Provide context and examples
- Update documentation with code changes

❌ **Don't**:
- Use jargon without explanation
- Write long, complex sentences
- Assume prior knowledge
- Use ambiguous pronouns
- Leave outdated information
- Document obvious things

### Code Examples

**Good Example:**
```javascript
// ✅ Clear, complete, and tested
const user = await createUser({
  email: 'john@example.com',
  name: 'John Doe'
});

console.log(`User created: ${user.id}`);
```

**Bad Example:**
```javascript
// ❌ Incomplete and unclear
const u = createUser(...); // creates user
```

### Structure and Organization

1. **Start with Overview**: What and why
2. **Progressive Disclosure**: Simple → Complex
3. **Logical Grouping**: Related topics together
4. **Clear Navigation**: Table of contents, links
5. **Search-Friendly**: Good headings and keywords

### Visual Aids

- **Diagrams**: Architecture, data flow, sequences
- **Screenshots**: UI features, error messages
- **Tables**: Comparisons, configurations
- **Code Blocks**: Syntax-highlighted examples
- **Callouts**: Tips, warnings, important notes

## Tools for Documentation

### Documentation Generators
- **JSDoc**: JavaScript/TypeScript API docs
- **Sphinx**: Python documentation
- **Javadoc**: Java documentation
- **Docusaurus**: React-based documentation sites
- **GitBook**: Collaborative documentation platform

### Diagram Tools
- **Mermaid**: Text-based diagrams in Markdown
- **PlantUML**: UML diagrams
- **Draw.io**: Free diagram editor
- **Excalidraw**: Hand-drawn style diagrams

### Quality Checkers
- **Vale**: Prose linter
- **write-good**: English prose checker
- **alex**: Catch insensitive language
- **Markdownlint**: Markdown style checker

## Documentation Templates

### Function/Method Template
```markdown
## [functionName]

[Brief one-line description]

### Syntax

```[language]
[function signature]
```

### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| param1 | type | Yes | [Description] |
| param2 | type | No | [Description] |

### Returns

[type] - [Description of return value]

### Throws

- [ErrorType]: [When this error occurs]

### Example

```[language]
[usage example]
```

### See Also

- [Related function/link]
```

### Component Template (React/Vue)
```markdown
## [ComponentName]

[Brief description of component purpose]

### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| prop1 | type | value | [Description] |

### Usage

```jsx
<ComponentName prop1="value">
  [children]
</ComponentName>
```

### Examples

#### Basic Example

[Code and screenshot/description]

#### Advanced Example

[Code and screenshot/description]

### Accessibility

- [ARIA attributes used]
- [Keyboard navigation support]
- [Screen reader compatibility]
```

## Maintaining Documentation

### Update Triggers
- Code changes affecting public APIs
- New features or functionality
- Bug fixes changing behavior
- Deprecations or breaking changes
- Configuration option changes

### Review Process
1. **Technical Accuracy**: Verify with code
2. **Clarity Check**: Can newcomers understand?
3. **Completeness**: Are all aspects covered?
4. **Examples Tested**: Do they still work?
5. **Links Verified**: No broken references

### Version Control
- Keep docs in same repo as code
- Version docs alongside code releases
- Use branches for major doc rewrites
- Tag documentation releases
- Maintain migration guides

## Anti-Patterns to Avoid

❌ **Don't**:
- Write documentation that contradicts code
- Leave TODO comments in production docs
- Use screenshots instead of text when possible
- Over-document obvious code
- Forget to update docs after refactoring
- Write walls of text without structure

✅ **Do**:
- Keep docs synchronized with code
- Use code comments for "why", docs for "what/how"
- Provide searchable, linkable content
- Focus on user needs and use cases
- Include troubleshooting sections
- Make docs part of definition of done

## Related Skills
- `/code-generation` - Generate code with documentation included
- `/refactoring` - Improve documentation structure
- `/testing` - Document test strategies and results
