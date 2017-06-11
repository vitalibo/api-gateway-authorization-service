package com.github.vitalibo.auth.core;

import com.amazonaws.util.json.Jackson;
import org.apache.http.HttpHeaders;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FormURLEncodedTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"aa=bb&cc=dd&ee=ff", expected(param("aa", "bb"), param("cc", "dd"), param("ee", "ff"))},
            {"%D0%BF%D1%80%D0%B8%D0%B2%D1%96%D1%82=%D1%81%D0%B2%D1%96%D1%82", expected(param("привіт", "світ"))},
            {"aa=aa%26%26&bb%26%26=bb%2B%2B", expected(param("aa", "aa&&"), param("bb&&", "bb++"))}
        };
    }

    @Test(dataProvider = "samples")
    public void testDecode(String urlencoded, String expected) {
        String actual = FormURLEncoded.decode(urlencoded, Collections.singletonMap(
            HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @SafeVarargs
    private static String expected(Map.Entry<String, String>... entries) {
        return Jackson.toJsonString(Arrays.stream(entries)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private static Map.Entry<String, String> param(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

}