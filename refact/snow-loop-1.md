# SNOW Loop 1

Extract dispatch logic from pipeline switch-case into a functional function.

## Review Advice (ImplementReview)

**Rule check (#1):** `docs/JenkinsJob/AGENTS.md` states "Pipeline code should be simple and only show the main flow. Put others into Functions in job or shared-libs" and "Acceptable stage should less or around 15 lines". The Execute stage was ~50 lines — violated both rules.

**Functional pattern (#2):** The job-as-function pattern's routing logic is implementation detail, not pipeline flow. Extract to a `def executeSnowFunction()` function.

## Before/After summary

| Metric | Before | After |
|---|---|---|
| Total lines | 85 | 81 |
| Execute stage lines | ~50 | 9 |
| Pipeline complexity | High (switch + 6 cases inline) | Low (3-line call) |
| Functional functions | 0 | 1 (executeSnowFunction) |

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
                    def result = null

                    switch(params.JOB_FUNCTION) {
                        case 'createChangeRequest':
                            def input = SnowModels.CreateChangeRequestInput.fromMap(inputMap)
                            result = snow.createChangeRequest(input.short_description, input.description, input.category, input.risk_level, input.planned_start, input.planned_end, input.assignment_group)
                            def output = SnowModels.CreateChangeRequestOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'getChangeRequest':
                            def input = SnowModels.GetChangeRequestInput.fromMap(inputMap)
                            result = snow.getChangeRequest(input.sys_id)
                            def output = SnowModels.GetChangeRequestOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'updateChangeRequest':
                            def input = SnowModels.UpdateChangeRequestInput.fromMap(inputMap)
                            result = snow.updateChangeRequest(input.sys_id, input.short_description, input.description, input.risk_level, input.planned_start, input.planned_end)
                            def output = SnowModels.UpdateChangeRequestOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'listChangeRequests':
                            def input = SnowModels.ListChangeRequestsInput.fromMap(inputMap)
                            def filters = [:]
                            if (input.state) filters.state = input.state
                            if (input.category) filters.category = input.category
                            if (input.assignment_group) filters.assignment_group = input.assignment_group
                            if (input.sysparm_limit) filters.sysparm_limit = input.sysparm_limit
                            if (input.sysparm_offset) filters.sysparm_offset = input.sysparm_offset
                            result = snow.listChangeRequests(filters)
                            def output = SnowModels.ListChangeRequestsOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'approveChangeRequest':
                            def input = SnowModels.ApproveChangeRequestInput.fromMap(inputMap)
                            result = snow.approveChangeRequest(input.sys_id, input.approval_comments)
                            def output = SnowModels.ApproveChangeRequestOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'closeChangeRequest':
                            def input = SnowModels.CloseChangeRequestInput.fromMap(inputMap)
                            result = snow.closeChangeRequest(input.sys_id, input.close_code, input.close_notes)
                            def output = SnowModels.CloseChangeRequestOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        default:
                            error("Unknown JOB_FUNCTION: ${params.JOB_FUNCTION}")
                    }
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
```

## After Code

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
            def filters = [:]
            if (input.state) filters.state = input.state
            if (input.category) filters.category = input.category
            if (input.assignment_group) filters.assignment_group = input.assignment_group
            if (input.sysparm_limit) filters.sysparm_limit = input.sysparm_limit
            if (input.sysparm_offset) filters.sysparm_offset = input.sysparm_offset
            def result = snow.listChangeRequests(filters)
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

## Final

Good. Pipeline code now shows only the main flow (parse → create client → dispatch). Routing logic moved to functional function per code style rules. No behavior change.

## Notes

- Removed intermediate `result` and `output` variables in most cases — inlined `Output.fromApiResult(result).toMap()` directly into `writeReturnJson` call for DRY.
- `listChangeRequests` case still has 5 if-blocks for filter building — target for Loop 2.
