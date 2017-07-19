# AWS Api Gateway Authorization services

[![Build Status](https://travis-ci.org/vitalibo/api-gateway-authorization-service.svg?branch=master)](https://travis-ci.org/vitalibo/api-gateway-authorization-service)

### Workflow

API Gateway Authorization Services implemented **Client Credentials** authorization flow  (see also [RFC6749: "The OAuth 2.0 Authorization Framework" section 4.4](https://tools.ietf.org/html/rfc6749#section-4.4)).
Sequence UML diagram show this workflow with main points, please see below:

![Sequence Diagram](http://g.gravizo.com/source/sequence_diagram?https://raw.githubusercontent.com/vitalibo/api-gateway-authorization-service/master/README.md)

<details> 

<summary>UML code</summary>

```
sequence_diagram
@startuml
"Client" -> "Authorization Server": Authentication request
activate "Authorization Server"
note left
+ Request (application/json)
  {
    "grant_type": "client_credentials",
    "client_id": "<username>",
    "client_secret": "<password>"
  }
end note
"Authorization Server" -> "Authorization Server": Validate client credential\nand signed JWT
"Client" <-- "Authorization Server": Authentication response
deactivate "Authorization Server"
note right
+ Response 200 (application/json)
  {
    "access_token": "<jwt>",
    "expires_in": <timestamp>,
    "token_type": "Bearer"
  }
end note
"Client" -> "API Gateway": Resource request
activate "API Gateway"
note left
+ Headers
  Authorization: Bearer <jwt>
end note
"API Gateway" -> "JWT Authorizer": Authorizer request
activate "JWT Authorizer"
note left
Context + Token
end note
"JWT Authorizer" -> "JWT Authorizer": Verify JWT\nand make policy
"API Gateway" <-- "JWT Authorizer": Authorizer response
deactivate "JWT Authorizer"
note left
Principal + Scope + Policy
end note
activate "API Gateway"
"API Gateway" <-- "API Gateway": if not authorized\nor don't have permission\non resource
"Client" <-- "API Gateway": Unauthorized or Forbidden response
destroy "API Gateway"
note right
+ Response 401 or 403 (application/json)
end note
"API Gateway" -> "Resource": Resource request
"Resource" -> "Resource": Process\nrequest
"Client" <-- "Resource": Resource response
deactivate "API Gateway"
note left
+ Response 200 (<Media Type>)
end note
@endum
sequence_diagram
```

</details>

### Links

- [OAuth 2. Client Credentials authorization flow](http://oauthbible.com/#oauth-2-two-legged)
