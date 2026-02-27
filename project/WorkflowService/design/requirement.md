# Workflow API Requirements

## Overview
- Purpose: a simple workflow API to run templated task/processes.

## Use Cases
- Create a Task
- Create a Workflow base on the task.
- Base on the workflow template, create a workflow instance
- Run the workflow instance

## Terminology
- Workflow: a sequence of tasks and a workspace and context
- Task: a unit of work that can be executed, it's a single node workflow. With a workspace and context to work on.
- Context: a json object that contains the data and state of the workflow.
- Workspace: a directory for a workflow/task. Task can use a subdirectory of the workspace as its workspace.
- Workflow template: a json file and a zip file that defines the workflow. 
  - The json file define the tasks and init context.
  - The zip file will be unzip to the workspace of the workflow, can be any content.


