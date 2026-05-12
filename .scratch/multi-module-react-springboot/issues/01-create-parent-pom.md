# Issue: Create Parent POM and Project Structure

**Status:** ready-for-agent

## Description

Create the root `pom.xml` for the multi-module Maven project `myapp`.

## Acceptance Criteria

- Root `pom.xml` with packaging type `pom`
- Defines modules: `backend`, `frontend`
- Inherits from `spring-boot-starter-parent` version 3.2.x
- Configures Java 21 toolchain
- Sets project encoding to UTF-8

## Technical Notes

- Group ID: `com.myapp`
- Artifact ID: `myapp`
- Version: `0.0.1-SNAPSHOT`
