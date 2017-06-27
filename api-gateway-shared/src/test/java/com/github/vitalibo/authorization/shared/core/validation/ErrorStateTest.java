package com.github.vitalibo.authorization.shared.core.validation;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

public class ErrorStateTest {

    private ErrorState errorState;

    @BeforeMethod
    public void setUp() {
        errorState = new ErrorState();
    }

    @Test
    public void testAddError() {
        errorState.addError("key1", "foo");
        errorState.addError("key1", "bar");
        errorState.addError("key2", "foo bar");

        Assert.assertEquals(errorState.get("key1"), Arrays.asList("foo", "bar"));
        Assert.assertEquals(errorState.get("key2"), Collections.singletonList("foo bar"));
    }

    @Test
    public void testDoNotHasErrors() {
        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testHasErrors() {
        errorState.addError("foo", "bar");

        Assert.assertTrue(errorState.hasErrors());
    }

}