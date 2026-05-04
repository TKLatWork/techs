# User API Use Cases

## UC-001: User Login
- **Actor**: User
- **Description**: User can call login with username and password, will return a JWT on success
- **Success Response**: 200 OK with JWT token
- **Error Response**: Appropriate HTTP status codes (4xx/5xx) with error details

## UC-002: Get User Information
- **Actor**: User
- **Description**: User provides a JWT, on success will return a UserInfo object
- **Success Response**: 200 OK with UserInfo object
- **Error Response**: Appropriate HTTP status codes (4xx/5xx) with error details

## UserInfo Model
- **userId**: Unique identifier for the user
- **username**: User's chosen username
- **role**: User's role within the system