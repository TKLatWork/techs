# Loop 1

Extract dispatch logic from pipeline switch-case into a functional function.

## Review Advice (ImplementReview)

**Rule check (#1):** `docs/JenkinsJob/AGENTS.md` states "Pipeline code should be simple and only show the main flow. Put others into Functions in job or shared-libs" and "Acceptable stage should less or around 15 lines". The Execute stage was ~50 lines — violated both rules.

**Functional pattern (#2):** The job-as-function pattern's routing logic is implementation detail, not pipeline flow. Extract to a `def executeIceFunction()` function.

## Before/After summary

| Metric | Before | After |
|---|---|---|
| Total lines | 84 | 81 |
| Execute stage lines | ~50 | 3 |
| Pipeline complexity | High (switch + 5 cases inline) | Low (3-line call) |
| Functional functions | 0 | 1 (executeIceFunction) |

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
                    def result = null

                    switch(params.JOB_FUNCTION) {
                        case 'createAuditEntry':
                            def input = ICEModels.CreateAuditEntryInput.fromMap(inputMap)
                            result = ice.createAuditEntry(input.resource_id, input.resource_type, input.action, input.actor, input.change_source, input.details ?: [:], input.compliance_tags ?: [], input.change_request_id)
                            def output = ICEModels.CreateAuditEntryOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'getAuditRecord':
                            def input = ICEModels.GetAuditRecordInput.fromMap(inputMap)
                            result = ice.getAuditRecord(input.audit_id)
                            def output = ICEModels.GetAuditRecordOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
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
                            result = ice.listAuditRecords(filters)
                            def output = ICEModels.ListAuditRecordsOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'getAuditSummary':
                            def input = ICEModels.GetAuditSummaryInput.fromMap(inputMap)
                            result = ice.getAuditSummary(input.from, input.to, input.group_by, input.compliance_tag)
                            def output = ICEModels.GetAuditSummaryOutput.fromApiResult(result)
                            writeReturnJson(200, 'success', output.toMap())
                            break
                        case 'exportAuditLog':
                            def input = ICEModels.ExportAuditLogInput.fromMap(inputMap)
                            result = ice.exportAuditLog(input.from, input.to, input.format, input.resource_type, input.compliance_tag, input.async)
                            def output = ICEModels.ExportAuditLogOutput.fromApiResult(result)
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
                writeReturnJson(500, "Tooling-ICE failed: ${currentBuild.result}")
            }
        }
        always {
            echo "Tooling-ICE completed at ${new Date()}"
        }
    }
}
```

## After Code

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

## Final

Good. Pipeline code now shows only the main flow (parse → create client → dispatch). Routing logic moved to functional function per code style rules. No behavior change.

## Notes

- Removed intermediate `result` and `output` variables in most cases — inlined `Output.fromApiResult(result).toMap()` directly into `writeReturnJson` call for DRY.
- `listAuditRecords` case still has 11 if-blocks for filter building — target for Loop 2.
