package com.github.vitalibo.authorization.shared.core.http;

import com.amazonaws.util.StringUtils;

import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BasicScheme {

    private static final Pattern BASIC_AUTHENTICATION_PATTERN = Pattern.compile(
        "Basic (?<encoded>(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$)");
    private static final Pattern DECODED_CLIENT_CREDENTIAL_PATTERN = Pattern.compile(
        "(?<username>[^:]+):(?<password>[^:]+)");

    private BasicScheme() {
        super();
    }

    public static Credentials decode(Map<String, String> headers) {
        String authorization = headers.get("Authorization");
        if (StringUtils.isNullOrEmpty(authorization)) {
            throw new BasicAuthenticationException(
                "The authorization header can't be empty.");
        }

        Matcher matcher = BASIC_AUTHENTICATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new BasicAuthenticationException(
                "Incorrect value of Basic Authentication header.");
        }

        matcher = DECODED_CLIENT_CREDENTIAL_PATTERN.matcher(
            new String(Base64.getDecoder()
                .decode(matcher.group("encoded"))));
        if (!matcher.matches()) {
            throw new BasicAuthenticationException(
                "Encrypted username and password has incorrect scheme.");
        }

        return new Credentials(
            matcher.group("username"),
            matcher.group("password"));
    }

}
