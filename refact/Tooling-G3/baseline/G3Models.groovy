package com.jklib

class G3Models implements Serializable {

    static class CreateReleasePackageInput {
        String name
        String version
        String description
        List artifacts
        String target_env
        Map config_overrides

        static CreateReleasePackageInput fromMap(Map m) {
            def input = new CreateReleasePackageInput()
            input.name = m.name as String
            input.version = m.version as String
            input.description = m.description as String
            input.artifacts = m.artifacts as List
            input.target_env = m.target_env as String
            input.config_overrides = m.config_overrides as Map
            return input
        }
    }

    static class CreateReleasePackageOutput {
        String id
        String name
        String version
        String status
        String created_at

        static CreateReleasePackageOutput fromApiResult(Map result) {
            def output = new CreateReleasePackageOutput()
            output.id = result.id as String
            output.name = result.name as String
            output.version = result.version as String
            output.status = result.status as String
            output.created_at = result.created_at as String
            return output
        }

        Map toMap() {
            return [id: id, name: name, version: version, status: status, created_at: created_at]
        }
    }

    static class GetReleasePackageInput {
        String package_id

        static GetReleasePackageInput fromMap(Map m) {
            def input = new GetReleasePackageInput()
            input.package_id = m.package_id as String
            return input
        }
    }

    static class GetReleasePackageOutput {
        Map release_package

        static GetReleasePackageOutput fromApiResult(Map result) {
            def output = new GetReleasePackageOutput()
            output.release_package = result
            return output
        }

        Map toMap() {
            return [release_package: release_package]
        }
    }

    static class ListReleasePackagesInput {
        String name
        String version
        String status
        String target_env
        Integer limit
        Integer offset

        static ListReleasePackagesInput fromMap(Map m) {
            def input = new ListReleasePackagesInput()
            input.name = m.name as String
            input.version = m.version as String
            input.status = m.status as String
            input.target_env = m.target_env as String
            input.limit = m.limit as Integer
            input.offset = m.offset as Integer
            return input
        }
    }

    static class ListReleasePackagesOutput {
        Integer total
        Integer limit
        Integer offset
        List packages

        static ListReleasePackagesOutput fromApiResult(Map result) {
            def output = new ListReleasePackagesOutput()
            output.total = result.total as Integer
            output.limit = result.limit as Integer
            output.offset = result.offset as Integer
            output.packages = result.packages as List
            return output
        }

        Map toMap() {
            return [total: total, limit: limit, offset: offset, packages: packages]
        }
    }

    static class ExecuteReleaseInput {
        String package_id
        Boolean dry_run
        Boolean rollback_on_failure
        List notify

        static ExecuteReleaseInput fromMap(Map m) {
            def input = new ExecuteReleaseInput()
            input.package_id = m.package_id as String
            input.dry_run = m.dry_run as Boolean
            input.rollback_on_failure = m.rollback_on_failure as Boolean
            input.notify = m.notify as List
            return input
        }
    }

    static class ExecuteReleaseOutput {
        String execution_id
        String package_id
        String status
        String started_at

        static ExecuteReleaseOutput fromApiResult(Map result) {
            def output = new ExecuteReleaseOutput()
            output.execution_id = result.execution_id as String
            output.package_id = result.package_id as String
            output.status = result.status as String
            output.started_at = result.started_at as String
            return output
        }

        Map toMap() {
            return [execution_id: execution_id, package_id: package_id, status: status, started_at: started_at]
        }
    }

    static class GetExecutionStatusInput {
        String execution_id

        static GetExecutionStatusInput fromMap(Map m) {
            def input = new GetExecutionStatusInput()
            input.execution_id = m.execution_id as String
            return input
        }
    }

    static class GetExecutionStatusOutput {
        Map execution_status

        static GetExecutionStatusOutput fromApiResult(Map result) {
            def output = new GetExecutionStatusOutput()
            output.execution_status = result
            return output
        }

        Map toMap() {
            return [execution_status: execution_status]
        }
    }

    static class RollbackReleaseInput {
        String execution_id
        String reason
        Boolean force

        static RollbackReleaseInput fromMap(Map m) {
            def input = new RollbackReleaseInput()
            input.execution_id = m.execution_id as String
            input.reason = m.reason as String
            input.force = m.force as Boolean
            return input
        }
    }

    static class RollbackReleaseOutput {
        String rollback_id
        String execution_id
        String status
        String started_at

        static RollbackReleaseOutput fromApiResult(Map result) {
            def output = new RollbackReleaseOutput()
            output.rollback_id = result.rollback_id as String
            output.execution_id = result.execution_id as String
            output.status = result.status as String
            output.started_at = result.started_at as String
            return output
        }

        Map toMap() {
            return [rollback_id: rollback_id, execution_id: execution_id, status: status, started_at: started_at]
        }
    }
}
