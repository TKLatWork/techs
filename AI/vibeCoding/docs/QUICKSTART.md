# Quick Start Guide

Get up and running with Vibe Coding in minutes!

## 🚀 Prerequisites

Before you begin, ensure you have:

- ✅ **Visual Studio Code** installed
- ✅ **Tongyi Lingma extension** installed and configured
- ✅ **Git** for version control
- ✅ **Node.js** (for JavaScript/TypeScript projects)

## 📦 Project Setup

Your vibeCoding project is already initialized with the following structure:

```
vibeCoding/
├── README.md                          # Project overview
├── .lingma/
│   ├── agents/
│   │   └── vibe-coder.md             # Primary coding agent
│   └── skills/
│       ├── code-generation/SKILL.md  # Code generation workflows
│       ├── testing/SKILL.md          # Testing workflows
│       ├── refactoring/SKILL.md      # Code improvement workflows
│       └── documentation/SKILL.md    # Documentation workflows
└── docs/
    ├── workflow.md                    # Development workflows
    └── best-practices.md             # Best practices guide
```

## 🎯 First Steps

### Step 1: Open in VS Code

```bash
cd e:\code\Project\techs\AI\vibeCoding
code .
```

### Step 2: Activate the Agent

1. Open the **Chat Panel** in VS Code (Ctrl+Shift+I or Cmd+Shift+I)
2. Select the **vibe-coder** agent from the agent selector
3. If not visible, reload the window (Ctrl+Shift+P → "Reload Window")

### Step 3: Try Your First Task

In the Chat Panel, try one of these prompts:

**Generate Code:**
```
Create a simple Express.js API endpoint that returns user data
```

**Generate Tests:**
```
/testing - Create unit tests for a user authentication service
```

**Refactor Code:**
```
/refactoring - Help me improve this function's readability:
[paste your code]
```

**Generate Documentation:**
```
/documentation - Create a README for a todo list application
```

## 💡 Common Workflows

### Workflow 1: Build a Feature

```
1. Describe your feature in natural language
2. Agent proposes implementation approach
3. Use /code-generation to create the code
4. Use /testing to generate tests
5. Use /refactoring to improve quality
6. Use /documentation to add docs
```

**Example:**
```
User: "I need a REST API for managing books with CRUD operations"

Agent: [Proposes architecture]

User: "Sounds good, let's implement it"
/code-generation - Create Book API with Express.js

User: "Now add tests"
/testing - Generate comprehensive tests for the Book API

User: "Improve the code quality"
/refactoring - Optimize and clean up the Book API code

User: "Add documentation"
/documentation - Create API documentation for endpoints
```

### Workflow 2: Fix a Bug

```
1. Describe the bug and error messages
2. Agent helps diagnose the issue
3. Implement fix with AI assistance
4. Add regression tests
5. Verify the fix works
```

**Example:**
```
User: "I'm getting a 'Cannot read property of undefined' error 
when fetching user data. Here's the stack trace:
[error details]"

Agent: [Analyzes and suggests root cause]

User: "Let's fix it"
[Implement fix together]

User: "/testing - Add tests to prevent this issue"
```

### Workflow 3: Refactor Legacy Code

```
1. Identify code that needs improvement
2. Use /refactoring skill
3. Review suggested changes
4. Apply incrementally
5. Test after each change
```

**Example:**
```
User: "/refactoring - This function is too long and complex:
[paste code]"

Agent: [Suggests refactoring approach]

User: "Let's extract the validation logic first"
[Apply changes step by step]
```

## 🛠️ Using Skills

### Manual Activation

Type `/` in the chat to see available skills:

- `/code-generation` - Generate production-ready code
- `/testing` - Create test suites
- `/refactoring` - Improve code quality
- `/documentation` - Generate documentation

### Automatic Activation

Just describe what you need, and the agent will automatically use the appropriate skill:

```
"I need to create a login form" → Automatically uses /code-generation
"This code needs better tests" → Automatically uses /testing
"Clean up this messy function" → Automatically uses /refactoring
"Document this API" → Automatically uses /documentation
```

## 📚 Learning Resources

### Essential Reading

1. **[README.md](README.md)** - Project overview and architecture
2. **[Workflow Guide](docs/workflow.md)** - Detailed development workflows
3. **[Best Practices](docs/best-practices.md)** - Coding standards and tips

### Skill Documentation

Each skill has detailed documentation:

- [Code Generation Skill](.lingma/skills/code-generation/SKILL.md)
- [Testing Skill](.lingma/skills/testing/SKILL.md)
- [Refactoring Skill](.lingma/skills/refactoring/SKILL.md)
- [Documentation Skill](.lingma/skills/documentation/SKILL.md)

## 🎓 Tips for Success

### 1. Be Specific
```
❌ "Make a website"
✅ "Create a React landing page with hero section, features grid, and contact form"
```

### 2. Provide Context
```
Include:
- Technology stack
- Existing code patterns
- Project structure
- Requirements and constraints
```

### 3. Iterate Incrementally
```
Build complex features in small steps:
1. Basic structure
2. Core functionality
3. Error handling
4. Edge cases
5. Optimization
6. Polish
```

### 4. Ask Questions
```
Don't hesitate to ask:
- "Why did you choose this approach?"
- "What are the alternatives?"
- "Are there any security concerns?"
- "How can I test this?"
```

### 5. Validate Everything
```
Always:
- Review generated code
- Run tests
- Check for errors
- Test manually
- Ask for explanations
```

## 🔧 Troubleshooting

### Agent Not Showing Up

**Problem**: Can't find vibe-coder agent in Chat Panel

**Solution**:
1. Reload VS Code window (Ctrl+Shift+P → "Reload Window")
2. Check that `.lingma/agents/vibe-coder.md` exists
3. Verify Tongyi Lingma extension is installed and enabled

### Skill Not Activating

**Problem**: Skill commands don't work

**Solution**:
1. Ensure skill files exist in `.lingma/skills/`
2. Check YAML frontmatter is properly formatted
3. Try manual activation with `/skill-name`
4. Reload VS Code window

### Code Has Errors

**Problem**: Generated code doesn't work

**Solution**:
1. Share error messages with the agent
2. Ask for fixes: "This code has an error: [error message]"
3. Request explanation: "Why isn't this working?"
4. Iterate on the solution

### Poor Quality Output

**Problem**: Generated code doesn't meet standards

**Solution**:
1. Provide more specific requirements
2. Share examples of desired style
3. Ask for improvements: "Can you make this more efficient?"
4. Use /refactoring skill to enhance

## 🚀 Next Steps

Now that you're set up:

1. **Explore the documentation** - Read through the workflow and best practices guides
2. **Try a small project** - Practice with a simple feature or component
3. **Customize the agent** - Modify `vibe-coder.md` to match your preferences
4. **Create custom skills** - Add your own workflows for repetitive tasks
5. **Join the community** - Share your experiences and learn from others

## 📞 Getting Help

If you run into issues:

1. **Check the docs** - Most questions are answered in the documentation
2. **Ask the agent** - "Help me troubleshoot this issue..."
3. **Review examples** - Look at skill documentation for examples
4. **Experiment** - Try different approaches and prompts

## 🎉 You're Ready!

Your Vibe Coding environment is fully set up and ready to use. Start building amazing things with AI assistance!

**Happy Coding! 🚀**

---

**Quick Links:**
- [Main README](README.md)
- [Workflow Guide](docs/workflow.md)
- [Best Practices](docs/best-practices.md)
- [Tongyi Lingma Docs](https://help.aliyun.com/zh/lingma/)
