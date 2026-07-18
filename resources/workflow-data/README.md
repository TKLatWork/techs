# Workflow Data Common Resource

## Summary
- Description: A Model contract for workflow. It's not a job, a data contract can be use in a job.

## Terminology
- Workflow-Runner: A job to run a workflow.
- Workflow-Config: The config to drive this workflow to execute.
- Workflow-Build: A workflow execution, base on a workflow config.
- Workflow-Stage-data: standard data contract for workflow stage.
- Workflow-Context: A json data for shared data between workflow stages.
- Build-ID: A unique ID for a workflow-build.
- Workflow-Stage: A workflow stage/step, unit of a workflow.

## Resource
- Build-ID: A unique ID for a workflow-build. Use to track a workflow-build.
- A Data API will be use to persist workflow-build data. It support free json format.

### Stage job output
- prefer a json output from 

### Model

Workflow-Request request:
```json
{
    "id": "workflow-build-id",
    "common": { 
        "owner": "user-id",
        "ownerEmail": "user-email",
        "lastUpate": "2020-01-01T00:00:00Z"
     },
    "config": { ... },//workflow config
    "data": { ... }//Workflow-data
}
```

Workflow-Config:
```json
{
    "batches": [
        "batch-id": { ... }//Workflow-Stage
    ]
}
```

Workflow-Stage:
```json
{
    "id": "stage-id",
    "jobName": "job-name",
    "config": { ... }//any data use to help workflow runner to run the stage
}
```

Workflow-data:
```json
{
    "stageDatas": {
        "stage-id": { ... }//Workflow-Stage-data
    },
    "context": { ... }//any data, set by workflow runner
}
```

Workflow-Stage-data:
```json
{
    "jobName": "job-name",
    "status": "status",//enum: PENDING, RUNNING, SUCCESS, FAILED, CANCELED
    "startTime": "2020-01-01T00:00:00Z",
    "endTime": "2020-01-01T00:00:00Z",
    "inuput": { ... },//input/parameters
    "output": { ... },//output, free json or output model from job-as-function
    "log": [

    ]
    
}
```

## Dependencies

[job-as-function](../job-as-function/README.md)

# Notes

# Change log
