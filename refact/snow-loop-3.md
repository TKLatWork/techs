# SNOW Loop 3

Pipeline code style compliance review against `docs/JenkinsJob/AGENTS.md`.

## Review Advice (ImplementReview)

**Rule check (#1):** Reviewed all rules in `docs/JenkinsJob/AGENTS.md`:
- Structure order (Imports → Config+Var → Pipeline → Functional functions): ✓ Compliant
- Execute stage line count (≤15 lines): ✓ 9 lines
- Pipeline simplicity ("only show the main flow"): ✓ 3 lines of script
- Stage boilerplate minimization: ✓ Already compact
- "If a stage only 1 line of script, make the whole stage a single line": N/A (3 lines)

**DRY check:** The `writeReturnJson(200, 'success', ...)` is called 6 times in the switch-case. Could be made DRY with a dispatch-map pattern (like G3's `FUNCTION_MAP`), but this would:
- Introduce closures (less readable than switch-case)
- Save only 5 lines total
- Violate priority #2 (Readability) for marginal DRY gain

**Decision:** Not a sensible refactor. The trade-off favors readability over DRY.

## Before/After summary

No changes made. Code is already compliant.

| Metric | Value |
|---|---|
| Jenkinsfile total lines | 75 |
| Execute stage lines | 9 |
| Pipeline script lines | 3 |
| Rule violations | 0 |

## Before Code

```groovy
@Library('JK-Lib') _

import groovy.json.JsonSlurper
import com.jklib.snow.SnowClient
import com.jklib.snow.SnowModels

pipeline {
    agent any

    parameters {
        text(name: 'INPUT_JSON', description: 'JSON string with input parameters for the function', defaultValue: '{}')
        choice(name: 'JOB_FUNCTION', choices: ['createChangeRequest', 'getChangeRequest', 'updateChangeRequest', 'listChangeRequests', 'approveChangeRequest', 'closeChangeRequest'], description: 'Function to call')
        choice(name: 'snow_env', choices: ['dev', 'qa', 'prod'], description: 'ServiceNow environment')
    }

    stages {
        stage('Execute') {
            steps {
                script {
                    def inputMap = new JsonSlurper().parseText(params.INPUT_JSON ?: '{}')
                    def snow = new SnowClient(this, params.snow_env)
                    executeSnowFunction(snow, params.JOB_FUNCTION, inputMap)
                }
            }
        }
    }

    post {
        failure {
            script {
                writeReturnJson(500, "Tooling-SNOW failed: ${currentBuild.result}")
            }
        }
        always {
            echo "Tooling-SNOW completed at ${new Date()}"
        }
    }
}

def executeSnowFunction(snow, String functionName, Map inputMap) {
    switch(functionName) {
        case 'createChangeRequest':
            def input = SnowModels.CreateChangeRequestInput.fromMap(inputMap)
            def result = snow.createChangeRequest(input.short_description, input.description, input.category, input.risk_level, input.planned_start, input.planned_end, input.assignment_group)
            writeReturnJson(200, 'success', SnowModels.CreateChangeRequestOutput.fromApiResult(result).toMap())
            break
        case 'getChangeRequest':
            def input = SnowModels.GetChangeRequestInput.fromMap(inputMap)
            def result = snow.getChangeRequest(input.sys_id)
            writeReturnJson(200, 'success', SnowModels.GetChangeRequestOutput.fromApiResult(result).toMap())
            break
        case 'updateChangeRequest':
            def input = SnowModels.UpdateChangeRequestInput.fromMap(inputMap)
            def result = snow.updateChangeRequest(input.sys_id, input.short_description, input.description, input.risk_level, input.planned_start, input.planned_end)
            writeReturnJson(200, 'success', SnowModels.UpdateChangeRequestOutput.fromApiResult(result).toMap())
            break
        case 'listChangeRequests':
            def input = SnowModels.ListChangeRequestsInput.fromMap(inputMap)
            def result = snow.listChangeRequests(input.toFilters())
            writeReturnJson(200, 'success', SnowModels.ListChangeRequestsOutput.fromApiResult(result).toMap())
            break
        case 'approveChangeRequest':
            def input = SnowModels.ApproveChangeRequestInput.fromMap(inputMap)
            def result = snow.approveChangeRequest(input.sys_id, input.approval_comments)
            writeReturnJson(200, 'success', SnowModels.ApproveChangeRequestOutput.fromApiResult(result).toMap())
            break
        case 'closeChangeRequest':
            def input = SnowModels.CloseChangeRequestInput.fromMap(inputMap)
            def result = snow.closeChangeRequest(input.sys_id, input.close_code, input.close_notes)
            writeReturnJson(200, 'success', SnowModels.CloseChangeRequestOutput.fromApiResult(result).toMap())
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
