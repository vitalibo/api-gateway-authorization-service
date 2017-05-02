package com.github.vitalibo.auth.basic.core;

import com.amazonaws.util.StringUtils;
import com.github.vitalibo.auth.core.Principal;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpBasicAuthenticator {

    private static final Pattern BASIC_AUTHENTICATION_PATTERN = Pattern.compile(
        "Basic (?<credentials>(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$)");
    private static final Pattern CREDENTIALS_PATTERN = Pattern.compile("(?<user>[^:]+):(?<password>[^:]+)");

    private final Base64.Decoder base64Decoder;
    private final UserPool userPool;

    public HttpBasicAuthenticator(UserPool userPool) {
        this.base64Decoder = Base64.getDecoder();
        this.userPool = userPool;
    }

    public Principal auth(String authenticationToken) {
        if (StringUtils.isNullOrEmpty(authenticationToken)) {
            throw new IllegalArgumentException("Authentication Token can't be empty");
        }

        Matcher matcher = BASIC_AUTHENTICATION_PATTERN.matcher(authenticationToken);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect Basic Authentication token");
        }

        matcher = CREDENTIALS_PATTERN.matcher(
            new String(base64Decoder.decode(
                matcher.group("credentials"))));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Encrypted user and password has incorrect format");
        }

        return PrincipalTranslator.from(
            userPool.verify(
                matcher.group("user"),
                matcher.group("password")));
    }

}
