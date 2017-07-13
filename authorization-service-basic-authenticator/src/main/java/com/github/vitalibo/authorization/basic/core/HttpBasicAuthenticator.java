package com.github.vitalibo.authorization.basic.core;

import com.github.vitalibo.authorization.shared.core.http.BasicScheme;
import com.github.vitalibo.authorization.shared.core.http.Credentials;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@RequiredArgsConstructor
public class HttpBasicAuthenticator {

    private final UserPool userPool;

    public Principal authenticate(String authenticationToken) {
        Credentials credentials = BasicScheme.decode(
            Collections.singletonMap("Authorization", authenticationToken));

        return PrincipalTranslator.from(
            userPool.verify(
                credentials.getUsername(),
                credentials.getPassword()));
    }

}
