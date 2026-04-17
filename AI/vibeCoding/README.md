# Vibe Coding Planning

<div align="center">

![Status](https://img.shields.io/badge/status-production%20ready-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![AI Assistant](https://img.shields.io/badge/AI-Tongyi%20Lingma-purple)

**AI-Assisted Development Methodology for Modern Software Teams**

[Quick Start](docs/QUICKSTART.md) • [Documentation](docs/workflow.md) • [Best Practices](docs/best-practices.md) • [Contributing](CONTRIBUTING.md)

</div>

---

## Overview
Vibe Coding is an AI-assisted development methodology that leverages intelligent agents and skills to streamline the coding process through natural language interaction and automated workflows.

## 🎯 What's Included

✅ **1 Professional AI Agent** - vibe-coder with full-stack expertise  
✅ **4 Comprehensive Skills** - Code generation, testing, refactoring, documentation  
✅ **3 Detailed Guides** - Workflows, best practices, quick start  
✅ **Complete Project Structure** - Ready for immediate use  
✅ **11 Documentation Files** - 143KB+ of comprehensive guides  

## Project Structure

```
vibeCoding/
├── 📄 README.md                 # This file - Project overview
├── 📄 CONTRIBUTING.md           # Contribution guidelines
├── 📄 PROJECT_SUMMARY.md        # Complete project summary
│
├── .lingma/                     # Tongyi Lingma configuration
│   ├── agents/                  # Custom AI agents
│   │   └── vibe-coder.md        # Primary coding agent ⭐
│   └── skills/                  # Reusable skills
│       ├── code-generation/     # Code generation workflows 🚀
│       ├── refactoring/         # Code refactoring procedures ♻️
│       ├── testing/             # Testing workflows 🧪
│       └── documentation/       # Documentation generation 📝
│
├── docs/                        # Project documentation
│   ├── QUICKSTART.md            # Quick start guide 🏁
│   ├── workflow.md              # Development workflows 🔄
│   ├── best-practices.md        # Best practices guide ✨
│   └── examples/                # Example implementations
│
├── src/                         # Source code directory
│   ├── components/              # Reusable components
│   ├── services/                # Business logic services
│   └── utils/                   # Utility functions
│
├── tests/                       # Test files
│   ├── unit/                    # Unit tests
│   └── integration/             # Integration tests
│
└── config/                      # Configuration files
    ├── agent-config.yaml        # Agent configurations
    └── skill-config.yaml        # Skill configurations
```

## Core Components

### 1. AI Agents
Agents define the **identity and behavior** of AI assistants:
- **Role**: Who the agent is (e.g., Senior Developer, Code Reviewer)
- **Expertise**: Domain knowledge and specializations
- **Tools**: Permissions and capabilities
- **Behavior**: Working style and communication patterns

### 2. Skills
Skills define **capabilities and procedures**:
- **Code Generation**: Automated code creation from requirements
- **Refactoring**: Systematic code improvement workflows
- **Testing**: Test-driven development procedures
- **Documentation**: Auto-generated documentation workflows

### 3. Workflows
Structured processes for common development tasks:
- Requirement analysis → Implementation plan → Code generation
- Code review → Feedback incorporation → Iteration
- Testing → Bug fixing → Validation

## Getting Started

### Prerequisites
- Visual Studio Code with Tongyi Lingma extension
- Node.js (for JavaScript/TypeScript projects)
- Git for version control

### Initial Setup

1. **Configure AI Agents**
   ```bash
   # Create agent configuration in .lingma/agents/
   mkdir -p .lingma/agents
   ```

2. **Define Skills**
   ```bash
   # Create skill directories
   mkdir -p .lingma/skills/{code-generation,refactoring,testing,documentation}
   ```

3. **Initialize Project Structure**
   ```bash
   mkdir -p src/{components,services,utils}
   mkdir -p tests/{unit,integration}
   mkdir -p docs/examples
   mkdir -p config
   ```

### Using Agents and Skills

#### Activating an Agent
- **Automatic**: Describe your task naturally, Lingma will match the appropriate agent
- **Manual**: Use `/agent-name` command in VS Code Chat Panel

#### Triggering a Skill
- **Automatic**: Model detects when a skill is needed based on context
- **Manual**: Use `/skill-name` command (e.g., `/code-generation`)

## Development Workflow

### Phase 1: Planning & Analysis
1. Define requirements using natural language
2. Agent analyzes and proposes implementation strategy
3. Generate technical specification document

### Phase 2: Implementation
1. Activate appropriate coding agent
2. Trigger code generation skills as needed
3. Iterative refinement through conversation

### Phase 3: Testing & Validation
1. Generate unit tests using testing skills
2. Run test suite and fix issues
3. Integration testing and validation

### Phase 4: Documentation
1. Auto-generate API documentation
2. Create usage examples
3. Update project README

## Best Practices

### Agent Design
- Keep agent definitions focused and concise
- Clearly define role boundaries and expertise areas
- Specify tool permissions explicitly
- Use persona-based language ("You are...")

### Skill Development
- Write skills in English for consistency
- Use procedural language ("This skill enables...")
- Structure with clear sections (Purpose, When to Use, Workflow)
- Include concrete examples and templates

### Context Management
- Batch operations in single skill triggers when possible
- Re-trigger skills for multi-turn workflows
- Keep agent files minimal to save tokens
- Leverage conversation history for continuity

### Code Quality
- Always validate generated code with `get_problems`
- Follow established coding standards
- Maintain clear commit messages
- Document complex logic

## Configuration Examples

### Agent Configuration (.lingma/agents/vibe-coder.md)
```yaml
---
name: vibe-coder
description: An AI-powered coding assistant specialized in rapid development through natural language interaction
tools:
  - Read
  - Write
  - Edit
  - Glob
  - Grep
  - Shell
---
```

### Skill Configuration (.lingma/skills/code-generation/SKILL.md)
```markdown
---
name: code-generation
description: Generates production-ready code from natural language requirements
---

# Code Generation Skill

## Purpose
Transforms user requirements into well-structured, tested code following best practices.

## When to Use
- Creating new features from specifications
- Generating boilerplate code
- Implementing algorithms or data structures

## Workflow
1. Analyze requirements
2. Propose implementation approach
3. Generate code with proper structure
4. Add error handling and edge cases
5. Include inline documentation
```

## Collaboration Guidelines

### Team Workflow
1. **Shared Agents**: Define team-wide agents in `.lingma/agents/`
2. **Shared Skills**: Create reusable skills for common patterns
3. **Version Control**: Commit agent and skill configurations
4. **Documentation**: Keep workflow docs updated

### Individual Customization
- Personal agents go in user-level `~/.lingma/agents/`
- Override project agents with same name
- Customize skills for personal preferences

## Resources

- [Tongyi Lingma Documentation](https://help.aliyun.com/zh/lingma/)
- [VS Code Extensions](https://code.visualstudio.com/docs/editor/extension-marketplace)
- [AI-Assisted Development Best Practices](docs/best-practices.md)

## Contributing

1. Fork the repository
2. Create feature branch
3. Add new agents or skills
4. Submit pull request with documentation

## License

MIT License - See LICENSE file for details

---

**Last Updated**: 2026-04-18
**Version**: 1.0.0
