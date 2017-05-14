package com.github.vitalibo.auth.server.core;

import com.github.vitalibo.auth.core.Principal;

public interface UserPool {

    Principal auth(String username, String password);

    boolean changePassword(String username, String password, String newPassword);

}
