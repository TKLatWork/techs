package com.jklib.ice

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class ICEClient implements Serializable {
    private final Object script
    private final String baseUrl
    private final String credentialId

    ICEClient(script, String env, String credentialId = 'ice-api-key') {
        this.script = script
        this.baseUrl = "https://ice-audit.${env}.internal"
        this.credentialId = credentialId
    }

    private Map request(String method, String path, Map body = null) {
        def url = baseUrl + path
        def result = null

        script.withCredentials([script.string(credentialsId: credentialId, variable: 'ICE_API_KEY')]) {
            def args = [
                url            : url,
                httpMode       : method,
                contentType    : 'APPLICATION_JSON',
                customHeaders  : [
                    [name: 'Authorization', value: "Bearer ${script.env.ICE_API_KEY}", maskValue: true]
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
                throw new RuntimeException("ICE API error [${statusCode}]: ${responseBody}")
            }

            result = new JsonSlurper().parseText(responseBody) as Map
        }

        return result
    }

    Map createAuditEntry(
        String resourceId,
        String resourceType,
        String action,
        String actor,
        String changeSource,
        Map details = [:],
        List complianceTags = [],
        String changeRequestId = null
    ) {
        def body = [
            resource_id    : resourceId,
            resource_type  : resourceType,
            action         : action,
            actor          : actor,
            change_source  : changeSource
        ]
        if (details) body.details = details
        if (complianceTags) body.compliance_tags = complianceTags
        if (changeRequestId) body.change_request_id = changeRequestId

        return request('POST', '/api/v1/audit', body)
    }

    Map getAuditRecord(String auditId) {
        return request('GET', "/api/v1/audit/${auditId}")
    }

    Map listAuditRecords(Map filters = [:]) {
        def queryParts = []
        filters.each { key, value ->
            if (value != null) {
                queryParts << "${key}=${URLEncoder.encode(value.toString(), 'UTF-8')}"
            }
        }
        def queryString = queryParts ? '?' + queryParts.join('&') : ''
        return request('GET', "/api/v1/audit${queryString}")
    }

    Map getAuditSummary(String from, String to, String groupBy = null, String complianceTag = null) {
        def queryParts = [
            "from=${URLEncoder.encode(from, 'UTF-8')}",
            "to=${URLEncoder.encode(to, 'UTF-8')}"
        ]
        if (groupBy) queryParts << "group_by=${URLEncoder.encode(groupBy, 'UTF-8')}"
        if (complianceTag) queryParts << "compliance_tag=${URLEncoder.encode(complianceTag, 'UTF-8')}"
        def queryString = '?' + queryParts.join('&')
        return request('GET', "/api/v1/audit/summary${queryString}")
    }

    Map exportAuditLog(String from, String to, String format = null, String resourceType = null, String complianceTag = null, Boolean async = null) {
        def queryParts = [
            "from=${URLEncoder.encode(from, 'UTF-8')}",
            "to=${URLEncoder.encode(to, 'UTF-8')}"
        ]
        if (format) queryParts << "format=${URLEncoder.encode(format, 'UTF-8')}"
        if (resourceType) queryParts << "resource_type=${URLEncoder.encode(resourceType, 'UTF-8')}"
        if (complianceTag) queryParts << "compliance_tag=${URLEncoder.encode(complianceTag, 'UTF-8')}"
        if (async != null) queryParts << "async=${async}"
        def queryString = '?' + queryParts.join('&')
        return request('GET', "/api/v1/audit/export${queryString}")
    }
}
