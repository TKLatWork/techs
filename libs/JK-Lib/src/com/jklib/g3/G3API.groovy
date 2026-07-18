package com.jklib.g3

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class G3API implements Serializable {
    private final Object script
    private final String baseUrl
    private final String credentialId

    static final Map ENDPOINTS = [
        createReleasePackage: [method: 'POST', path: '/api/v1/packages', body: true],
        getReleasePackage: [method: 'GET', path: '/api/v1/packages/{package_id}'],
        listReleasePackages: [method: 'GET', path: '/api/v1/packages', query: true],
        executeRelease: [method: 'POST', path: '/api/v1/packages/{package_id}/execute', body: true],
        getExecutionStatus: [method: 'GET', path: '/api/v1/executions/{execution_id}'],
        rollbackRelease: [method: 'POST', path: '/api/v1/executions/{execution_id}/rollback', body: true],
    ]

    G3API(script, String env, String credentialId = 'g3-api-key') {
        this.script = script
        this.baseUrl = "https://g3-api.${env}.internal"
        this.credentialId = credentialId
    }

    private Map request(String method, String path, Map body = null) {
        script.withCredentials([script.string(credentialsId: credentialId, variable: 'G3_API_KEY')]) {
            def args = [
                url            : baseUrl + path,
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

            if (response.status >= 400) {
                throw new RuntimeException("G3 API error [${response.status}]: ${response.content}")
            }

            new JsonSlurper().parseText(response.content) as Map
        }
    }

    Map callApi(String name, Object input) {
        def ep = ENDPOINTS[name]
        def path = resolvePath(ep.path, input)
        def body = ep.body ? input.toBody() : null
        if (ep.query) {
            def q = input.toFilters().collect { k, v -> "${k}=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')
            if (q) path += '?' + q
        }
        return request(ep.method, path, body)
    }

    private String resolvePath(String template, Object input) {
        return template.replaceAll(/\{(\w+)\}/) { String m ->
            input[m[1..-2]].toString()
        }
    }
}
