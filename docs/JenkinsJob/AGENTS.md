# How to write a Jenkins Job    

## Folders

all jobs are in the folder `jobs`, job in development are in the folder `jobs/dev`, jobs in production are in the folder `jobs/master`

## Job naming convention
- Prefix: `CI-`: CI/CD job
- Prefix: `Tooling-`: Tooling job
- Prefix: `Release-`: Release job

## Job Spec
Read [Spec AGENTS.md](./spec/AGENTS.md) and [Jenkins-Job Spec](./../spec/Jenkins-Job.md)

## Job Code

### Structure

Following ordered from top to bottom

1. Imports
2. Config+Var: Parameters, Functional Config, non-functional config, Shared Runtime var root.
3. Pipeline code
4. Functional functions
6. Util functions: Utility functions

### Code style
- Only do validation for user input or at lib's public method entrance, don't do validation for internal data/api return.
- Do not do defensive coding.
- Top level code/function should use to show the highest level of abstraction, low level code should hold the implementation detail.
- Priortity: 1. DRY rule, 2. Readability, 3. Reduce line count
- Model code/class should also holds the data update/convert logic.

### Code ownership

##### Jenkinsfile
- Pipeline code should be simple and only show the main flow.
- If a stage only 1 line of script, make the whole stage a single line
- Stage condition line should be in separate line
- Keep the stage boilerplate, the heading/tailing in one line when possible, leave space for main code
- Acceptalbe stage should less or around 15 lines
- Prefer validation in Jenkins parameter definition.

#### Shared-Libs
- Config/Model/Const and functions that belongs to the shared libs should be hold in it.
- Should provide model class for input and output unless the fields are less than 4
