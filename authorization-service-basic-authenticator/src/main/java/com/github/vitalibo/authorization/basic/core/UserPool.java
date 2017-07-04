package com.github.vitalibo.authorization.basic.core;

public interface UserPool {

    String verify(String userId, String password);

}
