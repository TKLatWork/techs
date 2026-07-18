# Refacotr Loop Skill

## Description
Use to do refactor in looping method for code. Plan/execute/review/feedback.

## When to Use
- After code implement
- User requests for 'code refactor', 'code imporve'

## Input
- Scope of the refactor, spec/code/lib 
- Optional, loop limit, default 4

## Goal and Loop break
- Break/End the looping when loop limit reached
- Break/End the looping When no sencenable refactor advice found and half of the limit reached.

## Scope and Layer and feedback
- When last loop run result bad or no sencenable refactor advice found, try suggest different layer or focucs to Implement Review.
- When scope have many code/files or their a layer(domian/module) in it, find those.
- Pick a layer or on the full scope be for starting a loop, maybe zoom out or zoom in.
- For each loop run, it should base on a fresh context to start the loop.

## Steps

# init
- Collect scope information, clarify and confirm the scope
- creat a Record Folder under `./refact` to store the refactor records

# Loop
- Descise the scope/layer of this loop run
- Use Implement Review skill to review the code for advice, provide feedback to it if available. Use subtask/subagent, keep it runs on a fresh context.
- Chceck the advice, see if it's sencenable to implement
- Use subtask/subagent to implement the advice, keep it runs on a fresh context.
- Review the result by compare the before and after, if the result is worser, rollback change from this loop.
- Save review report to Record Folder
- If go into another loop run. Provide the report as feedback for next loop run.

# Report and end
- Save final report to Record Folder

## Refactor Record

```markdown
# Loop <loop number>
<refactor advice>

## Before/After summary
<summary, statistics>

## Before Code
<full code before refactor>

## After Code
<full code after refactor>

## Finial 
<good/bad, if rollback, why>

## Notes

```
