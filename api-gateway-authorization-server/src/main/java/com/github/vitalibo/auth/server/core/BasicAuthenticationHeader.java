package com.github.vitalibo.auth.server.core;

import com.amazonaws.util.StringUtils;
import lombok.Data;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class BasicAuthenticationHeader {

    private static final Pattern BASIC_AUTHENTICATION_PATTERN = Pattern.compile(
        "Basic (?<encoded>(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$)");
    private static final Pattern DECODED_CLIENT_CREDENTIALS_PATTERN = Pattern.compile(
        "(?<user>[^:]+):(?<password>[^:]+)");

    private final String user;
    private final String password;

    public static BasicAuthenticationHeader decode(String authorization) {
        if (StringUtils.isNullOrEmpty(authorization)) {
            throw new IllegalArgumentException("Authentication Token can't be empty");
        }

        Matcher matcher = BASIC_AUTHENTICATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect Basic Authentication token");
        }

        matcher = DECODED_CLIENT_CREDENTIALS_PATTERN.matcher(
            new String(Base64.getDecoder()
                .decode(matcher.group("encoded"))));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Encrypted user and password has incorrect format");
        }

        return new BasicAuthenticationHeader(
            matcher.group("user"),
            matcher.group("password"));
    }

}
