package com.github.vitalibo.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TestHelper {

    private TestHelper() {
    }

    public static String resourceAsJsonString(String resource) {
        return resourceAsString(resource)
            .replaceAll("[ \t\n\r]+", "");
    }

    public static String resourceAsString(String resource) {
        return new BufferedReader(new InputStreamReader(TestHelper.class.getResourceAsStream(resource)))
            .lines().collect(Collectors.joining("\n"));
    }

}
