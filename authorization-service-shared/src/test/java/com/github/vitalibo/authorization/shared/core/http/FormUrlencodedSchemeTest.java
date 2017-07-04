package com.github.vitalibo.authorization.shared.core.http;

import com.amazonaws.util.json.Jackson;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

public class FormUrlencodedSchemeTest {

    @Test
    public void testFullDecode() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("aa=bb&cc=dd");
        request.setHeaders(new HashMap<>(Collections.singletonMap(
            "Content-Type", "application/x-www-form-urlencoded")));

        ProxyRequest actual = FormUrlencodedScheme.decode(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getHeaders().get(
            "Content-Type"), "application/json");
        Assert.assertEquals(actual.getBody(), Jackson.toJsonString(
            map(entry("aa", "bb"), entry("cc", "dd"))));
    }

    @Test
    public void testSkipDecode() {
        ProxyRequest request = new ProxyRequest();
        request.setBody("foo");
        request.setHeaders(new HashMap<>(Collections.singletonMap(
            "Content-Type", "application/text")));

        ProxyRequest actual = FormUrlencodedScheme.decode(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getHeaders().get(
            "Content-Type"), "application/text");
        Assert.assertEquals(actual.getBody(), "foo");
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"aa=bb&cc=dd&ee=ff", map(entry("aa", "bb"), entry("cc", "dd"), entry("ee", "ff"))},
            {"%D0%BF%D1%80%D0%B8%D0%B2%D1%96%D1%82=%D1%81%D0%B2%D1%96%D1%82", map(entry("привіт", "світ"))},
            {"aa=aa%26%26&bb%26%26=bb%2B%2B", map(entry("aa", "aa&&"), entry("bb&&", "bb++"))},
            {"aa=bb&cc=&dd=ee", map(entry("aa", "bb"), entry("cc", ""), entry("dd", "ee"))}
        };
    }

    @Test(dataProvider = "samples")
    public void testDecode(String body, Map<String, String> expected) {
        Map<String, String> actual = FormUrlencodedScheme.decode(body, Collections.singletonMap(
            "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @SafeVarargs
    private static Map<String, String> map(Map.Entry<String, String>... entries) {
        return Arrays.stream(entries)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, String> entry(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

}