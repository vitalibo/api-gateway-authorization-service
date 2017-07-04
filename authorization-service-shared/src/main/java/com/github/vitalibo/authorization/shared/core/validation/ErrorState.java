package com.github.vitalibo.authorization.shared.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ErrorState extends HashMap<String, Collection<String>> {

    public boolean hasErrors() {
        return !this.isEmpty();
    }

    public void addError(String field, String message) {
        Collection<String> collection = this.computeIfAbsent(field, o -> new ArrayList<>());
        collection.add(message);
    }

}
