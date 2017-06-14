package com.github.vitalibo.auth.jwt.core;

import com.amazonaws.auth.policy.Policy;

public interface PolicyRepository {

    Policy getPolicy(Claims claims);

}