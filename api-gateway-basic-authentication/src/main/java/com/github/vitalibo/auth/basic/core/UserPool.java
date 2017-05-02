package com.github.vitalibo.auth.basic.core;

public interface UserPool {

    String verify(String userId, String password);

}
