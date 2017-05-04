package com.github.vitalibo.auth.infrastructure.aws.gateway.proxy;

import lombok.Data;

import java.util.Map;

@Data
public class ProxyRequest {

    private String resource;
    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;
    private Map<String, String> stageVariables;
    private Map<String, ?> requestContext;
    private String body;
    private Boolean isBase64Encoded;

}
