package com.github.vitalibo.authorization.server.core;

public interface UserPool {

    String authenticate(String username, String password) throws UserPoolException;

    void changePassword(String username, String previousPassword, String proposedPassword) throws UserPoolException;

}
