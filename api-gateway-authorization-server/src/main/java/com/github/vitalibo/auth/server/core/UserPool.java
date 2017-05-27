package com.github.vitalibo.auth.server.core;

import com.github.vitalibo.auth.core.Principal;

public interface UserPool {

    Principal authenticate(String username, String password) throws UserPoolException;

    boolean changePassword(Principal principal, String newPassword) throws UserPoolException;

}
