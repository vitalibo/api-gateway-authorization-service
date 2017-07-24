package com.github.vitalibo.authorization.jwt.core;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class Claims {

    private String username;
    private List<String> roles;
    private ZonedDateTime expiredAt;

}