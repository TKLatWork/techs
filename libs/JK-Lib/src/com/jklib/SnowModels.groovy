package com.jklib

class SnowModels implements Serializable {

    static class CreateChangeRequestInput {
        String short_description
        String description
        String category
        String risk_level
        String planned_start
        String planned_end
        String assignment_group

        static CreateChangeRequestInput fromMap(Map m) {
            def input = new CreateChangeRequestInput()
            input.short_description = m.short_description as String
            input.description = m.description as String
            input.category = m.category as String
            input.risk_level = m.risk_level as String
            input.planned_start = m.planned_start as String
            input.planned_end = m.planned_end as String
            input.assignment_group = m.assignment_group as String
            return input
        }
    }

    static class CreateChangeRequestOutput {
        String sys_id
        String number
        String state

        static CreateChangeRequestOutput fromApiResult(Map result) {
            def output = new CreateChangeRequestOutput()
            output.sys_id = result.sys_id as String
            output.number = result.number as String
            output.state = result.state as String
            return output
        }

        Map toMap() {
            return [sys_id: sys_id, number: number, state: state]
        }
    }

    static class GetChangeRequestInput {
        String sys_id

        static GetChangeRequestInput fromMap(Map m) {
            def input = new GetChangeRequestInput()
            input.sys_id = m.sys_id as String
            return input
        }
    }

    static class GetChangeRequestOutput {
        Map change_request

        static GetChangeRequestOutput fromApiResult(Map result) {
            def output = new GetChangeRequestOutput()
            output.change_request = result
            return output
        }

        Map toMap() {
            return [change_request: change_request]
        }
    }

    static class UpdateChangeRequestInput {
        String sys_id
        String short_description
        String description
        String risk_level
        String planned_start
        String planned_end

        static UpdateChangeRequestInput fromMap(Map m) {
            def input = new UpdateChangeRequestInput()
            input.sys_id = m.sys_id as String
            input.short_description = m.short_description as String
            input.description = m.description as String
            input.risk_level = m.risk_level as String
            input.planned_start = m.planned_start as String
            input.planned_end = m.planned_end as String
            return input
        }
    }

    static class UpdateChangeRequestOutput {
        Map change_request

        static UpdateChangeRequestOutput fromApiResult(Map result) {
            def output = new UpdateChangeRequestOutput()
            output.change_request = result
            return output
        }

        Map toMap() {
            return [change_request: change_request]
        }
    }

    static class ListChangeRequestsInput {
        String state
        String category
        String assignment_group
        Integer sysparm_limit
        Integer sysparm_offset

        static ListChangeRequestsInput fromMap(Map m) {
            def input = new ListChangeRequestsInput()
            input.state = m.state as String
            input.category = m.category as String
            input.assignment_group = m.assignment_group as String
            input.sysparm_limit = m.sysparm_limit as Integer
            input.sysparm_offset = m.sysparm_offset as Integer
            return input
        }
    }

    static class ListChangeRequestsOutput {
        List change_requests

        static ListChangeRequestsOutput fromApiResult(Map result) {
            def output = new ListChangeRequestsOutput()
            output.change_requests = result.result as List
            return output
        }

        Map toMap() {
            return [change_requests: change_requests]
        }
    }

    static class ApproveChangeRequestInput {
        String sys_id
        String approval_comments

        static ApproveChangeRequestInput fromMap(Map m) {
            def input = new ApproveChangeRequestInput()
            input.sys_id = m.sys_id as String
            input.approval_comments = m.approval_comments as String
            return input
        }
    }

    static class ApproveChangeRequestOutput {
        Map change_request

        static ApproveChangeRequestOutput fromApiResult(Map result) {
            def output = new ApproveChangeRequestOutput()
            output.change_request = result
            return output
        }

        Map toMap() {
            return [change_request: change_request]
        }
    }

    static class CloseChangeRequestInput {
        String sys_id
        String close_code
        String close_notes

        static CloseChangeRequestInput fromMap(Map m) {
            def input = new CloseChangeRequestInput()
            input.sys_id = m.sys_id as String
            input.close_code = m.close_code as String
            input.close_notes = m.close_notes as String
            return input
        }
    }

    static class CloseChangeRequestOutput {
        Map change_request

        static CloseChangeRequestOutput fromApiResult(Map result) {
            def output = new CloseChangeRequestOutput()
            output.change_request = result
            return output
        }

        Map toMap() {
            return [change_request: change_request]
        }
    }
}
