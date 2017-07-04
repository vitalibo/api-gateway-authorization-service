package com.github.vitalibo.authorization.shared.infrastructure.aws.gateway;

import lombok.Data;

@Data
public class AuthorizerRequest {

    private String type;
    private String authorizationToken;
    private String methodArn;

}
