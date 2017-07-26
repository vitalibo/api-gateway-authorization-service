# AWS Api Gateway Authorization service

[![Build Status](https://travis-ci.org/vitalibo/api-gateway-authorization-service.svg?branch=master)](https://travis-ci.org/vitalibo/api-gateway-authorization-service)

The AWS API Gateway Authorization Service project provide simple OAuth 2.0 solution for AWS infrastructure.
It allow you don't care about manage and validate access to own resources, but enable focus on business values of resources.
You actually only need describe IAM Policy for your resources and configure API Gateway custom authorizer, Amazon Cognito user pool care about maintain a user and group directory. 

### Workflow

The Authorization Service implemented **Client Credentials** authorization flow (see also [RFC6749: "The OAuth 2.0 Authorization Framework" section 4.4](https://tools.ietf.org/html/rfc6749#section-4.4)).
Sequence UML diagram show this workflow with main points, please see below:

![Sequence Diagram](http://g.gravizo.com/source/svg/sequence_diagram.puml?https://raw.githubusercontent.com/vitalibo/api-gateway-authorization-service/master/README.md)

<details> 

<summary>UML code</summary>

```
sequence_diagram.puml
@startuml
skinparam monochrome false

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

...

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
note left
Principal + Scope + Policy
end note
deactivate "JWT Authorizer"

alt failed request
"API Gateway" <-- "API Gateway": if not authorized\nor don't have permission\non resource
"Client" <-- "API Gateway": Unauthorized or Forbidden response
note right
+ Response 401 or 403 (application/json)
end note

else successful request
"API Gateway" -> "Resource": Resource request
"Resource" -> "Resource": Process\nrequest

"Client" <-- "Resource": Resource response
note left
+ Response 200 (<Media Type>)
end note
deactivate "API Gateway"
end

@endum
sequence_diagram.puml
```

</details>

### Build

Ensure that you have installed Git, Java 8, Maven, AWS Cli and Docker.  
Please clone this repository in first time and build the source codes use following command : 

```
mvn clean verify
```

in scope verify phase will be run unit tests and package jar files.

### Deploy

The deployment of Authorization Services is fully based on AWS infrastructure and CloudFormation templates.
In folder `infrastructure` contains three stack templates:

- `policy.json` - This template Create/Update AWS AIM subject. Namely deployment User, lambda execution Roles, authorizer Role and execute API Gateway Policy for default Cognito Groups.
- `stack.json` - This template Create/Update infrastructure of Authorization Service. That is Cognito User Pool, AWS Lambdas (Server, JWT Authorizer and Basic Authenticator) and API Gateway rest API for Authorization Server.
- `sample.json` - This template Create/Update mock API Gateway for integration testing and as sample of using custom authorizers.

In order to deploy you must already have deployment S3 bucket and choose user name/prefix of all resources. This IAM User will be have permission restriction on resources with this prefix. I recommended to use user name `authorization-service` or `authorization-service-dev` for develop stage.

- You must get started with running bash script to create/update policy stack, command see below. All changes set must be reviewed and approved by super admin in [AWS CloudFormation Console](https://console.aws.amazon.com/cloudformation).

```
./update-policy.sh <user-name> <deployment-bucket>
```

After successfully create stack in `Outputs` section you will see `AccessKey` and `SecretKey`. Please use this credentials for the following deployment steps.

- Then, need create/update infrastructure running next script:

```
./update-stack.sh <user-name> <deployment-bucket>
```

This script copy compiled lambdas in deployment S3 bucket and create/update necessary resources. 
These two steps are enough for normal work Authorization Service in production environment, but if you use develop environment for integration tests or you want see sample of using custom authorizers, please do third step.

- This step create Mock API Gateway and does not have business value. This stack is for integration tests or as demonstration sample, how to use Authorization Service.

```
./update-sample.sh <user-name>
```

- To run integration test, please move to folder `integration`, where you need run follow script:

```
./tests.sh <user-name>
```

Inside this script up Docker container with already installed Newman, that run and test a Postman Collections directly. That you can easily integrate it into your continuous integration servers and build systems.

### Links

- [OAuth 2. Client Credentials authorization flow](http://oauthbible.com/#oauth-2-two-legged)
- [AWS Management Console](https://console.aws.amazon.com/)
