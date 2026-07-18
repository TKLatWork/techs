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
