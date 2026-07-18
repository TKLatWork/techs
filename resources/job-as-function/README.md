# Job as function Common Resource

# Summary
- Type: Common
- Description: A Jenkins job pattern that make a job be call as a function

# Terminology
- JOB_FUNCTION: param use to tell the function to call

# Resource

## Model

- Required Model class for input and output, for each function. Prefer to extend resue those from libs.

## Input

- Optional INPUT_JSON parameter, a json string that contains all the input parameters
- Optional JOB_FUNCTION parameter, tell the job which function to call

## Output

Sample output json:
```
{
    "code": 200,//int code follow the html convention
    "message": "success",
    "data": {//the output data}，
    "files": { "file1": "file1.txt", "file2": "file2.txt" }
}
```

- Return.json archive file: the json output
- If output has files, they should be in archive and pointed from the Return.json


# References

# Notes

# Change log