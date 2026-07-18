package com.jklib

class ICEModels implements Serializable {

    static class CreateAuditEntryInput {
        String resource_id
        String resource_type
        String action
        String actor
        String change_source
        Map details
        List compliance_tags
        String change_request_id

        static CreateAuditEntryInput fromMap(Map m) {
            def input = new CreateAuditEntryInput()
            input.resource_id = m.resource_id as String
            input.resource_type = m.resource_type as String
            input.action = m.action as String
            input.actor = m.actor as String
            input.change_source = m.change_source as String
            input.details = m.details as Map
            input.compliance_tags = m.compliance_tags as List
            input.change_request_id = m.change_request_id as String
            return input
        }
    }

    static class CreateAuditEntryOutput {
        String id
        String resource_id
        String action
        String actor
        String timestamp
        boolean immutable

        static CreateAuditEntryOutput fromApiResult(Map result) {
            def output = new CreateAuditEntryOutput()
            output.id = result.id as String
            output.resource_id = result.resource_id as String
            output.action = result.action as String
            output.actor = result.actor as String
            output.timestamp = result.timestamp as String
            output.immutable = result.immutable as boolean
            return output
        }

        Map toMap() {
            return [id: id, resource_id: resource_id, action: action, actor: actor, timestamp: timestamp, immutable: immutable]
        }
    }

    static class GetAuditRecordInput {
        String audit_id

        static GetAuditRecordInput fromMap(Map m) {
            def input = new GetAuditRecordInput()
            input.audit_id = m.audit_id as String
            return input
        }
    }

    static class GetAuditRecordOutput {
        Map audit_record

        static GetAuditRecordOutput fromApiResult(Map result) {
            def output = new GetAuditRecordOutput()
            output.audit_record = result
            return output
        }

        Map toMap() {
            return [audit_record: audit_record]
        }
    }

    static class ListAuditRecordsInput {
        String resource_id
        String resource_type
        String actor
        String action
        String change_source
        String from
        String to
        String compliance_tag
        Integer limit
        String cursor

        static ListAuditRecordsInput fromMap(Map m) {
            def input = new ListAuditRecordsInput()
            input.resource_id = m.resource_id as String
            input.resource_type = m.resource_type as String
            input.actor = m.actor as String
            input.action = m.action as String
            input.change_source = m.change_source as String
            input.from = m.from as String
            input.to = m.to as String
            input.compliance_tag = m.compliance_tag as String
            input.limit = m.limit as Integer
            input.cursor = m.cursor as String
            return input
        }
    }

    static class ListAuditRecordsOutput {
        Integer total
        List records
        String next_cursor

        static ListAuditRecordsOutput fromApiResult(Map result) {
            def output = new ListAuditRecordsOutput()
            output.total = result.total as Integer
            output.records = result.records as List
            output.next_cursor = result.next_cursor as String
            return output
        }

        Map toMap() {
            return [total: total, records: records, next_cursor: next_cursor]
        }
    }

    static class GetAuditSummaryInput {
        String from
        String to
        String group_by
        String compliance_tag

        static GetAuditSummaryInput fromMap(Map m) {
            def input = new GetAuditSummaryInput()
            input.from = m.from as String
            input.to = m.to as String
            input.group_by = m.group_by as String
            input.compliance_tag = m.compliance_tag as String
            return input
        }
    }

    static class GetAuditSummaryOutput {
        Map period
        Integer total_records
        List groups

        static GetAuditSummaryOutput fromApiResult(Map result) {
            def output = new GetAuditSummaryOutput()
            output.period = result.period as Map
            output.total_records = result.total_records as Integer
            output.groups = result.groups as List
            return output
        }

        Map toMap() {
            return [period: period, total_records: total_records, groups: groups]
        }
    }

    static class ExportAuditLogInput {
        String from
        String to
        String format
        String resource_type
        String compliance_tag
        Boolean async

        static ExportAuditLogInput fromMap(Map m) {
            def input = new ExportAuditLogInput()
            input.from = m.from as String
            input.to = m.to as String
            input.format = m.format as String
            input.resource_type = m.resource_type as String
            input.compliance_tag = m.compliance_tag as String
            input.async = m.async as Boolean
            return input
        }
    }

    static class ExportAuditLogOutput {
        String format
        Integer record_count
        String data
        String export_id
        String status
        String download_url

        static ExportAuditLogOutput fromApiResult(Map result) {
            def output = new ExportAuditLogOutput()
            output.format = result.format as String
            output.record_count = result.record_count as Integer
            output.data = result.data as String
            output.export_id = result.export_id as String
            output.status = result.status as String
            output.download_url = result.download_url as String
            return output
        }

        Map toMap() {
            def map = [:]
            if (format) map.format = format
            if (record_count != null) map.record_count = record_count
            if (data) map.data = data
            if (export_id) map.export_id = export_id
            if (status) map.status = status
            if (download_url) map.download_url = download_url
            return map
        }
    }
}
