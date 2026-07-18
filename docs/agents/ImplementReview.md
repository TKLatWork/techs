# Implement Review Skill

## Description
Reviews Jenkins job, shared-lib's code implementation with resource awarence. Target to provide better code pattern, more thin and clean code. This skill prefer to be run as a subtask/subagent.

## When to Use
- Before implementing a new job, lib class
- After implement a new job, lib class or a part of those
- When user says "review implementation", "check implementation", or "code improve"

## Input
- Corresponding Spec documents and codes
- Resource that relates to the spec

## Goal
- Suggestions for abstraction/design pattern
- Make code more readable, reduce line count, reduce complexity
- DRY rule/code reuse

## Review Checklist

### 1. Functional pattern review
- Base on the spec/requirements, does it match some abstraction/design pattern

### 2. Technical pattern review
- Base on the codes, do we have abstraction/design pattern or code improvement advice

## Output Format

```markdown
# Implement Review: <job/lib name>

## Findings

### Important

<fix suggestion and reason>

#### BEFORE and ARFTER


### Optional

<fix suggestion and reason>

#### BEFORE and ARFTER

```
