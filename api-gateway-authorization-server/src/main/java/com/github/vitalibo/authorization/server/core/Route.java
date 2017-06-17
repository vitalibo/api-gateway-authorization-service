package com.github.vitalibo.authorization.server.core;

import lombok.Getter;

import java.util.regex.Pattern;

public enum Route {

    NOT_FOUND("", ""),
    OAUTH2_CLIENT_CREDENTIALS("/oauth/token", "POST"),
    CHANGE_PASSWORD("/account", "GET|POST");

    @Getter
    private final Pattern path;
    @Getter
    private final Pattern httpMethod;

    Route(String path, String httpMethod) {
        this.path = Pattern.compile(path);
        this.httpMethod = Pattern.compile(httpMethod);
    }

}
