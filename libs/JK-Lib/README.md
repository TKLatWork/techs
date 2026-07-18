# JK-Lib — Jenkins Shared Library

## Structure

| Folder | Purpose |
|---|---|
| `vars/` | Global pipeline DSL steps (each `.groovy` = callable step) |
| `src/` | Groovy classes (`com.jklib.*`) |
| `resources/` | Static files loaded via `libraryResource()` |

## Usage

```groovy
@Library('JK-Lib') _

hello('Jenkins')
def slug = com.jklib.Utils.slugify("My Project")
```

## writeReturnJson

Shared step for job-as-function pattern. Writes and archives a `Return.json` file.

```groovy
@Library('JK-Lib') _

writeReturnJson(200, 'success', [id: 'abc123', status: 'created'])
writeReturnJson(500, 'error message')
```

| Param | Type | Description |
|---|---|---|
| `code` | int | HTTP-style status code |
| `message` | String | Status message |
| `data` | Map | Output data (optional, default `[:]`) |
| `files` | Map | File references (optional, default `[:]`) |

## Model Classes

Input/output model classes for each API, used by job-as-function jobs.

| Class | API | Inner Classes |
|---|---|---|
| `com.jklib.SnowModels` | SNOW | CreateChangeRequestInput/Output, GetChangeRequestInput/Output, UpdateChangeRequestInput/Output, ListChangeRequestsInput/Output, ApproveChangeRequestInput/Output, CloseChangeRequestInput/Output |
| `com.jklib.ICEModels` | ICE | CreateAuditEntryInput/Output, GetAuditRecordInput/Output, ListAuditRecordsInput/Output, GetAuditSummaryInput/Output, ExportAuditLogInput/Output |
| `com.jklib.G3Models` | G3 | CreateReleasePackageInput/Output, GetReleasePackageInput/Output, ListReleasePackagesInput/Output, ExecuteReleaseInput/Output, GetExecutionStatusInput/Output, RollbackReleaseInput/Output |

Each Input class has `static fromMap(Map)` to parse INPUT_JSON. Each Output class has `static fromApiResult(Map)` to wrap API response and `Map toMap()` for Return.json data.

## G3Client

Wraps the G3 Release Package API (`https://g3-api.<env>.internal`). Requires the HTTP Request plugin and a Jenkins credential `g3-api-key`.

```groovy
@Library('JK-Lib') _

def g3 = new com.jklib.G3Client(this, 'dev', 'g3-api-key')
def pkg = g3.createReleasePackage('my-app', '1.0.0', 'Initial release', [], 'dev', [:])
echo "Package ID: ${pkg.id}"

def exec = g3.executeRelease(pkg.id)
echo "Execution: ${exec.execution_id}"

def status = g3.getExecutionStatus(exec.execution_id)
echo "Status: ${status.status}"
```

| Method | Description |
|---|---|
| `createReleasePackage(name, version, description?, artifacts?, targetEnv?, configOverrides?)` | Create a release package |
| `getReleasePackage(packageId)` | Get package details |
| `listReleasePackages(filters?)` | List/filter packages |
| `executeRelease(packageId, dryRun?, rollbackOnFailure?, notify?)` | Trigger package execution |
| `getExecutionStatus(executionId)` | Get execution status |
| `rollbackRelease(executionId, reason, force?)` | Rollback an execution |

## ICEClient

Wraps the ICE Audit API (`https://ice-audit.<env>.internal`). Requires the HTTP Request plugin and a Jenkins credential `ice-api-key`.

```groovy
@Library('JK-Lib') _

def ice = new com.jklib.ICEClient(this, 'dev', 'ice-api-key')
def audit = ice.createAuditEntry(
    'server-prod-01',
    'server',
    'modified',
    'admin@example.com',
    'Jenkins',
    [old_version: '1.0', new_version: '1.1'],
    ['SOX', 'PCI'],
    'CHG123456'
)
echo "Audit ID: ${audit.id}"
```

| Method | Description |
|---|---|
| `createAuditEntry(resourceId, resourceType, action, actor, changeSource, details?, complianceTags?, changeRequestId?)` | Create an immutable audit entry |
| `getAuditRecord(auditId)` | Get a specific audit record |
| `listAuditRecords(filters?)` | List/filter audit records |
| `getAuditSummary(from, to, groupBy?, complianceTag?)` | Get aggregated audit summary |
| `exportAuditLog(from, to, format?, resourceType?, complianceTag?, async?)` | Export audit log data |

## SnowClient

Wraps the ServiceNow API (`https://<env>-instance.service-now.com`). Requires the HTTP Request plugin and a Jenkins `usernamePassword` credential `snow-api-key`.

```groovy
@Library('JK-Lib') _

def snow = new com.jklib.SnowClient(this, 'dev', 'snow-api-key')
def cr = snow.createChangeRequest(
    'Deploy app v1.2',
    'Production deployment of application version 1.2',
    'Standard',
    'Low',
    '2026-07-20 08:00:00',
    '2026-07-20 10:00:00',
    'Deployment Team'
)
echo "CR Number: ${cr.number}"
echo "sys_id: ${cr.sys_id}"
echo "State: ${cr.state}"
```

| Method | Description |
|---|---|
| `createChangeRequest(shortDescription, description?, category?, riskLevel?, plannedStart?, plannedEnd?, assignmentGroup?)` | Create a ServiceNow change request |
| `getChangeRequest(sysId)` | Get a change request by ID |
| `updateChangeRequest(sysId, shortDescription?, description?, riskLevel?, plannedStart?, plannedEnd?)` | Update a change request |
| `listChangeRequests(filters?)` | List/filter change requests |
| `approveChangeRequest(sysId, approvalComments?)` | Approve a change request |
| `closeChangeRequest(sysId, closeCode, closeNotes)` | Close a change request |
