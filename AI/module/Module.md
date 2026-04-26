# A Client connection management API

## Models
### Connect
- targetEnv: string
- expiryMins: number
- owner: string

### Request
- token: string
- dsl: string

### Disconnect
- token: string

## Use cases
### Create a new connection
1. User creates a new connection with a target environment/expiry/owner. On sucess, returns a token.
2. User uses the token to submit a request to the connection.
3. User use API to disconnect the connection.

### Notes
- When targetEnv is booked, Connect request returns error.