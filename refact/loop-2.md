# Loop 2

Simplify listAuditRecords filter building by moving conversion logic to the model.

## Review Advice (ImplementReview)

**Rule check (#1):** `docs/JenkinsJob/AGENTS.md` states "Model code/class should also holds the data update/convert logic." The 11 if-blocks for filter building were in the Jenkinsfile, not in the model — violated this rule.

**DRY (#goal):** The filter-building pattern (check if field is non-null, add to map) was repeated 11 times. Moving it to a `toFilters()` method on the model centralizes the conversion.

**Feedback from Loop 1:** Loop 1 successfully extracted routing to a functional function. The remaining noise was the listAuditRecords case being disproportionately long.

## Before/After summary

| Metric | Before (Loop 2) | After (Loop 2) |
|---|---|---|
| Jenkinsfile total lines | 81 | 70 |
| listAuditRecords case lines | 14 | 4 |
| ICEModels total lines | 207 | 222 |
| Filter logic location | Jenkinsfile | ListAuditRecordsInput.toFilters() |

## Before Code

### Jenkinsfile (listAuditRecords case)
```groovy
        case 'listAuditRecords':
            def input = ICEModels.ListAuditRecordsInput.fromMap(inputMap)
            def filters = [:]
            if (input.resource_id) filters.resource_id = input.resource_id
            if (input.resource_type) filters.resource_type = input.resource_type
            if (input.actor) filters.actor = input.actor
            if (input.action) filters.action = input.action
            if (input.change_source) filters.change_source = input.change_source
            if (input.from) filters.from = input.from
            if (input.to) filters.to = input.to
            if (input.compliance_tag) filters.compliance_tag = input.compliance_tag
            if (input.limit) filters.limit = input.limit
            if (input.cursor) filters.cursor = input.cursor
            def result = ice.listAuditRecords(filters)
            writeReturnJson(200, 'success', ICEModels.ListAuditRecordsOutput.fromApiResult(result).toMap())
            break
```

### ICEModels (ListAuditRecordsInput)
```groovy
    static class ListAuditRecordsInput {
        String resource_id
        String resource_type
        String actor
        String action
        String change_source
        String from
        String to
        String compliance_tag
        Integer limit
        String cursor

        static ListAuditRecordsInput fromMap(Map m) {
            def input = new ListAuditRecordsInput()
            input.resource_id = m.resource_id as String
            input.resource_type = m.resource_type as String
            input.actor = m.actor as String
            input.action = m.action as String
            input.change_source = m.change_source as String
            input.from = m.from as String
            input.to = m.to as String
            input.compliance_tag = m.compliance_tag as String
            input.limit = m.limit as Integer
            input.cursor = m.cursor as String
            return input
        }
    }
```

## After Code

### Jenkinsfile (listAuditRecords case)
```groovy
        case 'listAuditRecords':
            def input = ICEModels.ListAuditRecordsInput.fromMap(inputMap)
            def result = ice.listAuditRecords(input.toFilters())
            writeReturnJson(200, 'success', ICEModels.ListAuditRecordsOutput.fromApiResult(result).toMap())
            break
```

### ICEModels (ListAuditRecordsInput)
```groovy
    static class ListAuditRecordsInput {
        String resource_id
        String resource_type
        String actor
        String action
        String change_source
        String from
        String to
        String compliance_tag
        Integer limit
        String cursor

        static ListAuditRecordsInput fromMap(Map m) {
            def input = new ListAuditRecordsInput()
            input.resource_id = m.resource_id as String
            input.resource_type = m.resource_type as String
            input.actor = m.actor as String
            input.action = m.action as String
            input.change_source = m.change_source as String
            input.from = m.from as String
            input.to = m.to as String
            input.compliance_tag = m.compliance_tag as String
            input.limit = m.limit as Integer
            input.cursor = m.cursor as String
            return input
        }

        Map toFilters() {
            def filters = [:]
            if (resource_id) filters.resource_id = resource_id
            if (resource_type) filters.resource_type = resource_type
            if (actor) filters.actor = actor
            if (action) filters.action = action
            if (change_source) filters.change_source = change_source
            if (from) filters.from = from
            if (to) filters.to = to
            if (compliance_tag) filters.compliance_tag = compliance_tag
            if (limit) filters.limit = limit
            if (cursor) filters.cursor = cursor
            return filters
        }
    }
```

## Final

Good. Filter conversion logic now lives in the model where it belongs per project rules. Jenkinsfile case reduced from 14 to 4 lines. No behavior change.

## Notes

- The `toFilters()` method uses the same null-check pattern as before — behavior is identical.
- This pattern could be applied to other models with optional fields in the future, but not done here to avoid scope creep.
