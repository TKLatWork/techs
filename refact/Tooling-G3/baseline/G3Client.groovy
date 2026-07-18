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
