# Loop 3

Pipeline code style compliance review against `docs/JenkinsJob/AGENTS.md`.

## Review Advice (ImplementReview)

**Rule check (#1):** Reviewed all rules in `docs/JenkinsJob/AGENTS.md`:
- Structure order (Imports → Config+Var → Pipeline → Functional functions): ✓ Compliant
- Execute stage line count (≤15 lines): ✓ 9 lines
- Pipeline simplicity ("only show the main flow"): ✓ 3 lines of script
- Stage boilerplate minimization: ✓ Already compact
- "If a stage only 1 line of script, make the whole stage a single line": N/A (3 lines)

**DRY check:** The `writeReturnJson(200, 'success', ...)` is called 5 times in the switch-case. Could be made DRY with a dispatch-map pattern, but this would:
- Introduce closures (less readable than switch-case)
- Save only 4 lines total
- Violate priority #2 (Readability) for marginal DRY gain

**Decision:** Not a sensible refactor. The trade-off favors readability over DRY.

## Before/After summary

No changes made. Code is already compliant.

| Metric | Value |
|---|---|
| Jenkinsfile total lines | 70 |
| Execute stage lines | 9 |
| Pipeline script lines | 3 |
| Rule violations | 0 |

## Before Code

```groovy
@Library('JK-Lib') _

import groovy.json.JsonSlurper
import com.jklib.ice.ICEClient
import com.jklib.ice.ICEModels

pipeline {
    agent any

    parameters {
        text(name: 'INPUT_JSON', description: 'JSON string with input parameters for the function', defaultValue: '{}')
        choice(name: 'JOB_FUNCTION', choices: ['createAuditEntry', 'getAuditRecord', 'listAuditRecords', 'getAuditSummary', 'exportAuditLog'], description: 'Function to call')
        choice(name: 'ice_env', choices: ['dev', 'qa', 'prod'], description: 'ICE API environment')
    }

    stages {
        stage('Execute') {
            steps {
                script {
                    def inputMap = new JsonSlurper().parseText(params.INPUT_JSON ?: '{}')
                    def ice = new ICEClient(this, params.ice_env)
                    executeIceFunction(ice, params.JOB_FUNCTION, inputMap)
                }
            }
        }
    }

    post {
        failure {
            script {
                writeReturnJson(500, "Tooling-ICE failed: ${currentBuild.result}")
            }
        }
        always {
            echo "Tooling-ICE completed at ${new Date()}"
        }
    }
}

def executeIceFunction(ice, String functionName, Map inputMap) {
    switch(functionName) {
        case 'createAuditEntry':
            def input = ICEModels.CreateAuditEntryInput.fromMap(inputMap)
            def result = ice.createAuditEntry(input.resource_id, input.resource_type, input.action, input.actor, input.change_source, input.details ?: [:], input.compliance_tags ?: [], input.change_request_id)
            writeReturnJson(200, 'success', ICEModels.CreateAuditEntryOutput.fromApiResult(result).toMap())
            break
        case 'getAuditRecord':
            def input = ICEModels.GetAuditRecordInput.fromMap(inputMap)
            def result = ice.getAuditRecord(input.audit_id)
            writeReturnJson(200, 'success', ICEModels.GetAuditRecordOutput.fromApiResult(result).toMap())
            break
        case 'listAuditRecords':
            def input = ICEModels.ListAuditRecordsInput.fromMap(inputMap)
            def result = ice.listAuditRecords(input.toFilters())
            writeReturnJson(200, 'success', ICEModels.ListAuditRecordsOutput.fromApiResult(result).toMap())
            break
        case 'getAuditSummary':
            def input = ICEModels.GetAuditSummaryInput.fromMap(inputMap)
            def result = ice.getAuditSummary(input.from, input.to, input.group_by, input.compliance_tag)
            writeReturnJson(200, 'success', ICEModels.GetAuditSummaryOutput.fromApiResult(result).toMap())
            break
        case 'exportAuditLog':
            def input = ICEModels.ExportAuditLogInput.fromMap(inputMap)
            def result = ice.exportAuditLog(input.from, input.to, input.format, input.resource_type, input.compliance_tag, input.async)
            writeReturnJson(200, 'success', ICEModels.ExportAuditLogOutput.fromApiResult(result).toMap())
            break
        default:
            error("Unknown JOB_FUNCTION: ${functionName}")
    }
}
```

## After Code

Same as before. No changes.

## Final

Good. No changes needed. Code is compliant with all rules in `docs/JenkinsJob/AGENTS.md`.

## Notes

- Loop 3 found no sensible refactor advice.
- Per RefactorLoop rules: "Break/End the looping When no sencenable refactor advice found and half of the limit reached."
- We're at loop 3 (past half of limit 4), so breaking early.
- Loop 4 is skipped.
