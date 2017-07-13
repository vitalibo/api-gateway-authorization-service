package com.github.vitalibo.authorization.basic.core;

import lombok.Data;

import java.util.List;

@Data
public class Principal {

    private String id;
    private String username;
    private List<String> scope;
    private Long expirationTime;

}
