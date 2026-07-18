# SNOW Loop 2

Simplify listChangeRequests filter building by moving conversion logic to the model.

## Review Advice (ImplementReview)

**Rule check (#1):** `docs/JenkinsJob/AGENTS.md` states "Model code/class should also holds the data update/convert logic." The 5 if-blocks for filter building were in the Jenkinsfile, not in the model — violated this rule.

**DRY (#goal):** The filter-building pattern (check if field is non-null, add to map) was repeated 5 times. Moving it to a `toFilters()` method on the model centralizes the conversion.

**Feedback from Loop 1:** Loop 1 successfully extracted routing to a functional function. The remaining noise was the listChangeRequests case being disproportionately long.

## Before/After summary

| Metric | Before (Loop 2) | After (Loop 2) |
|---|---|---|
| Jenkinsfile total lines | 81 | 75 |
| listChangeRequests case lines | 10 | 4 |
| SnowModels total lines | 188 | 198 |
| Filter logic location | Jenkinsfile | ListChangeRequestsInput.toFilters() |

## Before Code

### Jenkinsfile (listChangeRequests case)
```groovy
        case 'listChangeRequests':
            def input = SnowModels.ListChangeRequestsInput.fromMap(inputMap)
            def filters = [:]
            if (input.state) filters.state = input.state
            if (input.category) filters.category = input.category
            if (input.assignment_group) filters.assignment_group = input.assignment_group
            if (input.sysparm_limit) filters.sysparm_limit = input.sysparm_limit
            if (input.sysparm_offset) filters.sysparm_offset = input.sysparm_offset
            def result = snow.listChangeRequests(filters)
            writeReturnJson(200, 'success', SnowModels.ListChangeRequestsOutput.fromApiResult(result).toMap())
            break
```

### SnowModels (ListChangeRequestsInput)
```groovy
    static class ListChangeRequestsInput {
        String state
        String category
        String assignment_group
        Integer sysparm_limit
        Integer sysparm_offset

        static ListChangeRequestsInput fromMap(Map m) {
            def input = new ListChangeRequestsInput()
            input.state = m.state as String
            input.category = m.category as String
            input.assignment_group = m.assignment_group as String
            input.sysparm_limit = m.sysparm_limit as Integer
            input.sysparm_offset = m.sysparm_offset as Integer
            return input
        }
    }
```

## After Code

### Jenkinsfile (listChangeRequests case)
```groovy
        case 'listChangeRequests':
            def input = SnowModels.ListChangeRequestsInput.fromMap(inputMap)
            def result = snow.listChangeRequests(input.toFilters())
            writeReturnJson(200, 'success', SnowModels.ListChangeRequestsOutput.fromApiResult(result).toMap())
            break
```

### SnowModels (ListChangeRequestsInput)
```groovy
    static class ListChangeRequestsInput {
        String state
        String category
        String assignment_group
        Integer sysparm_limit
        Integer sysparm_offset

        static ListChangeRequestsInput fromMap(Map m) {
            def input = new ListChangeRequestsInput()
            input.state = m.state as String
            input.category = m.category as String
            input.assignment_group = m.assignment_group as String
            input.sysparm_limit = m.sysparm_limit as Integer
            input.sysparm_offset = m.sysparm_offset as Integer
            return input
        }

        Map toFilters() {
            def filters = [:]
            if (state) filters.state = state
            if (category) filters.category = category
            if (assignment_group) filters.assignment_group = assignment_group
            if (sysparm_limit) filters.sysparm_limit = sysparm_limit
            if (sysparm_offset) filters.sysparm_offset = sysparm_offset
            return filters
        }
    }
```

## Final

Good. Filter conversion logic now lives in the model where it belongs per project rules. Jenkinsfile case reduced from 10 to 4 lines. No behavior change.

## Notes

- The `toFilters()` method uses the same null-check pattern as before — behavior is identical.
- This pattern could be applied to other models with optional fields in the future, but not done here to avoid scope creep.
