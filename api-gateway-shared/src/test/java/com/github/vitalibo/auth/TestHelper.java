package com.github.vitalibo.auth;

import com.amazonaws.util.json.Jackson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TestHelper {

    private TestHelper() {
    }

    public static String resourceAsJsonString(String resource) {
        return Jackson.toJsonString(
            Jackson.fromJsonString(
                resourceAsString(resource), Object.class));
    }

    public static String resourceAsString(String resource) {
        return new BufferedReader(new InputStreamReader(TestHelper.class.getResourceAsStream(resource)))
            .lines().collect(Collectors.joining("\n"));
    }

}
