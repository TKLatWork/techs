package com.jklib.g3

import com.jklib.ModelUtil

class G3Model implements Serializable {

    static class CreateReleasePackageInput {
        String name
        String version
        String description
        List artifacts
        String target_env
        Map config_overrides

        static final Map FIELD_SPEC = [
            name: [type: String], version: [type: String], description: [type: String],
            artifacts: [type: List, default: []], target_env: [type: String],
            config_overrides: [type: Map, default: [:]],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static CreateReleasePackageInput fromMap(Map m) {
            return ModelUtil.populate(new CreateReleasePackageInput(), m, FIELD_SPEC) as CreateReleasePackageInput
        }
        Map toBody() { return ModelUtil.toBody(this, FIELDS) }
    }

    static class CreateReleasePackageOutput {
        String id
        String name
        String version
        String status
        String created_at

        static final Map FIELD_SPEC = [
            id: [type: String], name: [type: String], version: [type: String],
            status: [type: String], created_at: [type: String],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static CreateReleasePackageOutput fromApiResult(Map result) {
            return ModelUtil.populate(new CreateReleasePackageOutput(), result, FIELD_SPEC) as CreateReleasePackageOutput
        }
        Map toMap() { return ModelUtil.toMap(this, FIELDS) }
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

        static final Map FIELD_SPEC = [
            name: [type: String], version: [type: String], status: [type: String],
            target_env: [type: String], limit: [type: Integer], offset: [type: Integer],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static ListReleasePackagesInput fromMap(Map m) {
            return ModelUtil.populate(new ListReleasePackagesInput(), m, FIELD_SPEC) as ListReleasePackagesInput
        }
        Map toFilters() { return ModelUtil.toFilters(this, FIELDS) }
    }

    static class ListReleasePackagesOutput {
        Integer total
        Integer limit
        Integer offset
        List packages

        static final Map FIELD_SPEC = [
            total: [type: Integer], limit: [type: Integer],
            offset: [type: Integer], packages: [type: List],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static ListReleasePackagesOutput fromApiResult(Map result) {
            return ModelUtil.populate(new ListReleasePackagesOutput(), result, FIELD_SPEC) as ListReleasePackagesOutput
        }
        Map toMap() { return ModelUtil.toMap(this, FIELDS) }
    }

    static class ExecuteReleaseInput {
        String package_id
        Boolean dry_run
        Boolean rollback_on_failure
        List notify

        static final Map FIELD_SPEC = [
            package_id: [type: String],
            dry_run: [type: Boolean, default: false],
            rollback_on_failure: [type: Boolean, default: true],
            notify: [type: List, default: []],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()
        static final List BODY_FIELDS = ['dry_run', 'rollback_on_failure', 'notify']

        static ExecuteReleaseInput fromMap(Map m) {
            return ModelUtil.populate(new ExecuteReleaseInput(), m, FIELD_SPEC) as ExecuteReleaseInput
        }
        Map toBody() { return ModelUtil.toBody(this, BODY_FIELDS) }
    }

    static class ExecuteReleaseOutput {
        String execution_id
        String package_id
        String status
        String started_at

        static final Map FIELD_SPEC = [
            execution_id: [type: String], package_id: [type: String],
            status: [type: String], started_at: [type: String],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static ExecuteReleaseOutput fromApiResult(Map result) {
            return ModelUtil.populate(new ExecuteReleaseOutput(), result, FIELD_SPEC) as ExecuteReleaseOutput
        }
        Map toMap() { return ModelUtil.toMap(this, FIELDS) }
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

        static final Map FIELD_SPEC = [
            execution_id: [type: String], reason: [type: String],
            force: [type: Boolean, default: false],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()
        static final List BODY_FIELDS = ['reason', 'force']

        static RollbackReleaseInput fromMap(Map m) {
            return ModelUtil.populate(new RollbackReleaseInput(), m, FIELD_SPEC) as RollbackReleaseInput
        }
        Map toBody() { return ModelUtil.toBody(this, BODY_FIELDS) }
    }

    static class RollbackReleaseOutput {
        String rollback_id
        String execution_id
        String status
        String started_at

        static final Map FIELD_SPEC = [
            rollback_id: [type: String], execution_id: [type: String],
            status: [type: String], started_at: [type: String],
        ]
        static final List FIELDS = FIELD_SPEC.keySet().toList()

        static RollbackReleaseOutput fromApiResult(Map result) {
            return ModelUtil.populate(new RollbackReleaseOutput(), result, FIELD_SPEC) as RollbackReleaseOutput
        }
        Map toMap() { return ModelUtil.toMap(this, FIELDS) }
    }
}
