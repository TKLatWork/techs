# Loop 3

## Review: G3Client technical patterns

### Important

1. **Method signatures accept positional parameters** — `createReleasePackage` has 6 positional params, `executeRelease` has 4. Since Input models now have `execute()`, the client should accept model objects directly. This eliminates parameter unpacking in `execute()` methods and reduces signature complexity.

### Optional

None.

## Before/After summary

| File | Before | After | Delta |
|---|---|---|---|
| G3Client | 97 lines | 92 lines | -5 (-5%) |
| G3Models | 254 lines | 248 lines | -6 (-2%) |

Changes:
- G3Client: All 6 methods now accept their corresponding Input model instead of positional params
- G3Models: All 6 `execute()` methods simplified to `client.method(this)` pattern

## Before Code

### G3Client.groovy (97 lines)
```groovy
package com.jklib

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class G3Client implements Serializable {
    private final Object script
    private final String baseUrl
    private final String credentialId

    G3Client(script, String env, String credentialId = 'g3-api-key') {
        this.script = script
        this.baseUrl = "https://g3-api.${env}.internal"
        this.credentialId = credentialId
    }

    private Map request(String method, String path, Map body = null) {
        def url = baseUrl + path
        def result = null

        script.withCredentials([script.string(credentialsId: credentialId, variable: 'G3_API_KEY')]) {
            def args = [
                url            : url,
                httpMode       : method,
                contentType    : 'APPLICATION_JSON',
                customHeaders  : [
                    [name: 'X-API-Key', value: script.env.G3_API_KEY, maskValue: true]
                ],
                validResponseCodes: '100:599'
            ]

            if (body != null) {
                args.requestBody = JsonOutput.toJson(body)
            }

            def response = script.httpRequest(args)
            def statusCode = response.status
            def responseBody = response.content

            if (statusCode >= 400) {
                throw new RuntimeException("G3 API error [${statusCode}]: ${responseBody}")
            }

            result = new JsonSlurper().parseText(responseBody) as Map
        }

        return result
    }

    Map createReleasePackage(String name, String version, String description = null, List artifacts = [], String targetEnv = null, Map configOverrides = [:]) {
        def body = [
            name    : name,
            version : version
        ]
        if (description) body.description = description
        if (artifacts) body.artifacts = artifacts
        if (targetEnv) body.target_env = targetEnv
        if (configOverrides) body.config_overrides = configOverrides

        return request('POST', '/api/v1/packages', body)
    }

    Map getReleasePackage(String packageId) {
        return request('GET', "/api/v1/packages/${packageId}")
    }

    Map listReleasePackages(Map filters = [:]) {
        def queryParts = []
        filters.each { key, value ->
            queryParts << "${key}=${URLEncoder.encode(value.toString(), 'UTF-8')}"
        }
        def queryString = queryParts ? '?' + queryParts.join('&') : ''
        return request('GET', "/api/v1/packages${queryString}")
    }

    Map executeRelease(String packageId, boolean dryRun = false, boolean rollbackOnFailure = true, List notify = []) {
        def body = [
            dry_run             : dryRun,
            rollback_on_failure : rollbackOnFailure
        ]
        if (notify) body.notify = notify

        return request('POST', "/api/v1/packages/${packageId}/execute", body)
    }

    Map getExecutionStatus(String executionId) {
        return request('GET', "/api/v1/executions/${executionId}")
    }

    Map rollbackRelease(String executionId, String reason, boolean force = false) {
        def body = [
            reason: reason,
            force : force
        ]
        return request('POST', "/api/v1/executions/${executionId}/rollback", body)
    }
}
```

### G3Models.groovy execute() methods (before — 6 methods with field unpacking)
```groovy
        CreateReleasePackageOutput execute(G3Client client) {
            return CreateReleasePackageOutput.fromApiResult(
                client.createReleasePackage(name, version, description, artifacts, target_env, config_overrides)
            )
        }
        ...
        ExecuteReleaseOutput execute(G3Client client) {
            return ExecuteReleaseOutput.fromApiResult(
                client.executeRelease(package_id, dry_run, rollback_on_failure, notify)
            )
        }
        ...
        RollbackReleaseOutput execute(G3Client client) {
            return RollbackReleaseOutput.fromApiResult(
                client.rollbackRelease(execution_id, reason, force)
            )
        }
```

## After Code

### G3Client.groovy (92 lines)
```groovy
package com.jklib

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class G3Client implements Serializable {
    private final Object script
    private final String baseUrl
    private final String credentialId

    G3Client(script, String env, String credentialId = 'g3-api-key') {
        this.script = script
        this.baseUrl = "https://g3-api.${env}.internal"
        this.credentialId = credentialId
    }

    private Map request(String method, String path, Map body = null) {
        def url = baseUrl + path
        def result = null

        script.withCredentials([script.string(credentialsId: credentialId, variable: 'G3_API_KEY')]) {
            def args = [
                url            : url,
                httpMode       : method,
                contentType    : 'APPLICATION_JSON',
                customHeaders  : [
                    [name: 'X-API-Key', value: script.env.G3_API_KEY, maskValue: true]
                ],
                validResponseCodes: '100:599'
            ]

            if (body != null) {
                args.requestBody = JsonOutput.toJson(body)
            }

            def response = script.httpRequest(args)
            def statusCode = response.status
            def responseBody = response.content

            if (statusCode >= 400) {
                throw new RuntimeException("G3 API error [${statusCode}]: ${responseBody}")
            }

            result = new JsonSlurper().parseText(responseBody) as Map
        }

        return result
    }

    Map createReleasePackage(G3Models.CreateReleasePackageInput input) {
        def body = [name: input.name, version: input.version]
        if (input.description) body.description = input.description
        if (input.artifacts) body.artifacts = input.artifacts
        if (input.target_env) body.target_env = input.target_env
        if (input.config_overrides) body.config_overrides = input.config_overrides
        return request('POST', '/api/v1/packages', body)
    }

    Map getReleasePackage(G3Models.GetReleasePackageInput input) {
        return request('GET', "/api/v1/packages/${input.package_id}")
    }

    Map listReleasePackages(G3Models.ListReleasePackagesInput input) {
        def queryParts = []
        input.toFilters().each { key, value ->
            queryParts << "${key}=${URLEncoder.encode(value.toString(), 'UTF-8')}"
        }
        def queryString = queryParts ? '?' + queryParts.join('&') : ''
        return request('GET', "/api/v1/packages${queryString}")
    }

    Map executeRelease(G3Models.ExecuteReleaseInput input) {
        def body = [
            dry_run             : input.dry_run,
            rollback_on_failure : input.rollback_on_failure
        ]
        if (input.notify) body.notify = input.notify
        return request('POST', "/api/v1/packages/${input.package_id}/execute", body)
    }

    Map getExecutionStatus(G3Models.GetExecutionStatusInput input) {
        return request('GET', "/api/v1/executions/${input.execution_id}")
    }

    Map rollbackRelease(G3Models.RollbackReleaseInput input) {
        def body = [
            reason: input.reason,
            force : input.force
        ]
        return request('POST', "/api/v1/executions/${input.execution_id}/rollback", body)
    }
}
```

### G3Models.groovy execute() methods (after — all use `this`)
```groovy
        CreateReleasePackageOutput execute(G3Client client) {
            return CreateReleasePackageOutput.fromApiResult(client.createReleasePackage(this))
        }
        ...
        GetReleasePackageOutput execute(G3Client client) {
            return GetReleasePackageOutput.fromApiResult(client.getReleasePackage(this))
        }
        ...
        ListReleasePackagesOutput execute(G3Client client) {
            return ListReleasePackagesOutput.fromApiResult(client.listReleasePackages(this))
        }
        ...
        ExecuteReleaseOutput execute(G3Client client) {
            return ExecuteReleaseOutput.fromApiResult(client.executeRelease(this))
        }
        ...
        GetExecutionStatusOutput execute(G3Client client) {
            return GetExecutionStatusOutput.fromApiResult(client.getExecutionStatus(this))
        }
        ...
        RollbackReleaseOutput execute(G3Client client) {
            return RollbackReleaseOutput.fromApiResult(client.rollbackRelease(this))
        }
```

## Notes

- Advice accepted. Client methods accepting model objects is cleaner than 6 positional params.
- The `execute()` methods are now uniformly `client.method(this)` — maximum DRY.
- G3Client's conditional body-building (`if (input.description)`) is preserved — this is request construction logic, not defensive coding.
- `listReleasePackages` client now calls `input.toFilters()` internally, keeping filter logic in the model.
