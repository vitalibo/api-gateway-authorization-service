package com.github.vitalibo.authorization.shared.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

public class ErrorStateTest {

    @Test(expectedExceptions = ErrorState.class)
    public void testThrowException() {
        throw new ErrorState();
    }

    @Test
    public void testAddError() {
        ErrorState errorState = new ErrorState();
        errorState.addError("key1", "foo");
        errorState.addError("key1", "bar");
        errorState.addError("key2", "foo bar");

        Assert.assertEquals(errorState.get("key1"), Arrays.asList("foo", "bar"));
        Assert.assertEquals(errorState.get("key2"), Collections.singletonList("foo bar"));
    }

}