package com.jklib.snow

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class SnowClient implements Serializable {
    private final Object script
    private final String baseUrl
    private final String credentialId

    SnowClient(script, String env, String credentialId = 'snow-api-key') {
        this.script = script
        this.baseUrl = "https://${env}-instance.service-now.com"
        this.credentialId = credentialId
    }

    private Map request(String method, String path, Map body = null) {
        def url = baseUrl + path
        def result = null

        script.withCredentials([script.usernamePassword(credentialsId: credentialId, usernameVariable: 'SNOW_USER', passwordVariable: 'SNOW_PASS')]) {
            def encoded = "${script.env.SNOW_USER}:${script.env.SNOW_PASS}".bytes.encodeBase64().toString()
            def args = [
                url            : url,
                httpMode       : method,
                contentType    : 'APPLICATION_JSON',
                customHeaders  : [
                    [name: 'Authorization', value: "Basic ${encoded}", maskValue: true]
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
                throw new RuntimeException("SNOW API error [${statusCode}]: ${responseBody}")
            }

            result = new JsonSlurper().parseText(responseBody) as Map
        }

        return result
    }

    Map createChangeRequest(
        String shortDescription,
        String description = null,
        String category = null,
        String riskLevel = null,
        String plannedStart = null,
        String plannedEnd = null,
        String assignmentGroup = null
    ) {
        def body = [
            short_description: shortDescription
        ]
        if (description) body.description = description
        if (category) body.category = category
        if (riskLevel) body.risk_level = riskLevel
        if (plannedStart) body.planned_start = plannedStart
        if (plannedEnd) body.planned_end = plannedEnd
        if (assignmentGroup) body.assignment_group = assignmentGroup

        def response = request('POST', '/api/now/change_request', body)
        return response.result as Map
    }

    Map getChangeRequest(String sysId) {
        def response = request('GET', "/api/now/change_request/${sysId}")
        return response.result as Map
    }

    Map updateChangeRequest(
        String sysId,
        String shortDescription = null,
        String description = null,
        String riskLevel = null,
        String plannedStart = null,
        String plannedEnd = null
    ) {
        def body = [:]
        if (shortDescription) body.short_description = shortDescription
        if (description) body.description = description
        if (riskLevel) body.risk_level = riskLevel
        if (plannedStart) body.planned_start = plannedStart
        if (plannedEnd) body.planned_end = plannedEnd

        def response = request('PATCH', "/api/now/change_request/${sysId}", body)
        return response.result as Map
    }

    Map listChangeRequests(Map filters = [:]) {
        def queryParts = []
        filters.each { key, value ->
            if (value != null) {
                queryParts << "${key}=${URLEncoder.encode(value.toString(), 'UTF-8')}"
            }
        }
        def queryString = queryParts ? '?' + queryParts.join('&') : ''
        def response = request('GET', "/api/now/change_request${queryString}")
        return response
    }

    Map approveChangeRequest(String sysId, String approvalComments = null) {
        def body = [:]
        if (approvalComments) body.approval_comments = approvalComments

        def response = request('POST', "/api/now/change_request/${sysId}/approve", body)
        return response.result as Map
    }

    Map closeChangeRequest(String sysId, String closeCode, String closeNotes) {
        def body = [
            close_code: closeCode,
            close_notes: closeNotes
        ]
        def response = request('POST', "/api/now/change_request/${sysId}/close", body)
        return response.result as Map
    }
}
