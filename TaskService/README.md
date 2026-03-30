# A Task API

    [Overall view](./TaskServiceIdea.drawio)

# Modules

    - task-domain module to host domain entities and logic
    - task-api module for API definition
    - task-application module to host api definitions

## Logs/Why

   - DDD to isolate domain code
   - API module to isolate API/DTO/OpenAPI
   - 
    

## Dependency

    [Domain] > [API] > [Application]