package com.github.vitalibo.auth.server.infrastructure.aws.cognito;

import com.github.vitalibo.auth.core.Principal;
import com.github.vitalibo.auth.server.core.UserPool;

public class CognitoUserPool implements UserPool {

    @Override
    public Principal auth(String username, String password) {
        return null;
    }

}
