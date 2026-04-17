# Agent Learning Skill

## Overview

The **agent-learning** skill enables AI agents to continuously improve by learning from conversation history and user feedback. It automatically identifies patterns, extracts insights, and updates agent configurations to better match user preferences and working styles.

## Features

- 🧠 **Pattern Recognition**: Identifies recurring user preferences and feedback
- 📝 **Automatic Updates**: Proposes and applies configuration improvements
- 🔄 **Continuous Learning**: Adapts behavior based on ongoing interactions
- 📊 **Effectiveness Tracking**: Monitors improvement impact
- 🔒 **User Control**: All changes require user confirmation

## How It Works

### 1. Automatic Learning
The skill monitors conversations for:
- Explicit feedback ("I prefer...", "Don't do...")
- Repeated corrections or suggestions
- Successful interaction patterns
- Areas needing improvement

### 2. Analysis & Proposal
When learning opportunities are detected:
- Analyzes conversation patterns
- Identifies actionable improvements
- Proposes specific configuration changes
- Explains rationale with evidence

### 3. User Confirmation
Before applying changes:
- Presents clear summary of proposed updates
- Shows before/after comparisons
- Requests user approval
- Allows modifications or rejection

### 4. Application & Validation
After confirmation:
- Updates agent configuration file
- Creates backup for rollback
- Validates format and syntax
- Logs changes for tracking

## Usage

### Automatic Trigger
The skill activates automatically when:
- You provide explicit feedback
- Patterns emerge across multiple conversations
- The agent detects repeated corrections

### Manual Trigger
Use the command `/agent-learning` or ask:
- "Please learn from our recent conversations"
- "Analyze my feedback and update your behavior"
- "Summarize my preferences and improve your configuration"
- "Review our chat history and suggest improvements"

### Example Interactions

```
User: "You keep adding too many comments. I prefer cleaner code."
Agent: [Recognizes code style preference]
       [Later proposes update to prioritize brevity]

User: "/agent-learning"
Agent: [Analyzes last 20 conversations]
       [Identifies 3 key patterns]
       [Proposes configuration updates]
       [Requests confirmation]
```

## Configuration Files

### Skill Location
```
.lingma/skills/agent-learning/SKILL.md
```

### Agent File Updates
The skill updates agent files like:
```
.lingma/agents/AI-Planner.md
```

## Best Practices

### For Users
- ✅ Provide clear, specific feedback
- ✅ Confirm or reject proposed changes
- ✅ Explain why certain approaches work better
- ✅ Periodically review accumulated learnings

### For Agents
- ✅ Always explain change rationale
- ✅ Preserve effective existing behaviors
- ✅ Make incremental rather than drastic changes
- ✅ Track effectiveness of improvements

## Learning Categories

The skill focuses on improving:

1. **Communication Style**
   - Verbosity level
   - Technical depth
   - Explanation approach
   - Tone and formality

2. **Code Quality Standards**
   - Naming conventions
   - Comment density
   - Architecture patterns
   - Testing practices

3. **Workflow Preferences**
   - Planning vs. execution balance
   - Tool selection criteria
   - Iteration style
   - Review processes

4. **Problem-Solving Approach**
   - Analysis depth
   - Solution exploration
   - Decision-making speed
   - Risk tolerance

## Safety & Control

### Privacy
- All learning data stays local
- No sensitive information stored
- Users can clear learning history anytime

### Reversibility
- Every change creates a backup
- Easy rollback to previous versions
- Change history maintained

### Transparency
- All changes clearly documented
- Rationale provided for each update
- Evidence from conversations cited

## Monitoring Effectiveness

Track improvement through:
- Reduction in corrections needed
- Increased user satisfaction signals
- Faster task completion times
- Fewer clarification requests

## Troubleshooting

### Issue: Too many update proposals
**Solution**: Adjust sensitivity threshold or batch changes

### Issue: Changes don't seem helpful
**Solution**: Provide more specific feedback, request rollback

### Issue: Conflicting preferences detected
**Solution**: Agent should identify context-dependent rules

### Issue: Configuration becomes too complex
**Solution**: Periodic cleanup of outdated or redundant rules

## Advanced Features

### Confidence Scoring
Each learning includes a confidence level (0.0-1.0) based on:
- Frequency of observation
- Clarity of user intent
- Consistency across contexts
- Time since last contradiction

### Pattern Validation
Learnings are validated through:
- Cross-referencing multiple instances
- Checking for contradictions
- Monitoring subsequent interactions
- User confirmation

### Adaptive Learning Rate
The skill adjusts learning speed based on:
- User's feedback frequency
- Stability of preferences
- Project phase (exploration vs. production)
- Historical change success rate

## Integration with Other Skills

Works alongside:
- **regression**: For analytical tasks
- Custom project-specific skills
- Domain-specific knowledge bases

## Future Enhancements

Potential improvements:
- Multi-agent learning sharing
- Automated A/B testing of behaviors
- Predictive preference modeling
- Context-aware rule activation

## Support

For issues or questions:
1. Review this documentation
2. Check agent configuration file for recent changes
3. Examine learning logs if available
4. Request agent to explain its reasoning

---

**Remember**: The goal is continuous improvement through collaboration. Your feedback shapes the agent's behavior to better serve your needs.
