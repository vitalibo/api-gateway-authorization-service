package com.github.vitalibo.authorization.shared.core;

import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ErrorState extends RuntimeException implements Map<String, Collection<String>> {

    @Delegate
    private final Map<String, Collection<String>> errors = new HashMap<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addError(String field, String message) {
        Collection<String> collection = errors.computeIfAbsent(field, o -> new ArrayList<>());
        collection.add(message);
    }

}
