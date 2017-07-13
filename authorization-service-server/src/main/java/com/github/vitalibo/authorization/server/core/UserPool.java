package com.github.vitalibo.authorization.server.core;

public interface UserPool {

    UserIdentity authenticate(String username, String password) throws UserPoolException;

    boolean changePassword(UserIdentity identity, String newPassword) throws UserPoolException;

}
