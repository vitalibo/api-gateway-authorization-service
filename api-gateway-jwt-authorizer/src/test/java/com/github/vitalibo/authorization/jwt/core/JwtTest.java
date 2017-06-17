package com.github.vitalibo.authorization.jwt.core;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;

public class JwtTest {

    private KeyPair keysHolder;
    private RSAKey publicKey;
    private JWSSigner jwsSigner;

    private Jwt jwt;
    private String accessToken;

    @BeforeClass
    public void init() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        keysHolder = generator.generateKeyPair();
        jwsSigner = new RSASSASigner(keysHolder.getPrivate());
        publicKey = RSAKey(keysHolder.getPublic(), "keyId");
    }

    @BeforeMethod
    public void setUp() throws JOSEException {
        jwt = new Jwt(new ImmutableJWKSet<>(new JWKSet(publicKey)));
        JWTClaimsSet claims = claims(expirationTime(60), "role1", "role2");
        accessToken = jws(claims, publicKey).serialize();
    }

    @DataProvider
    public Object[][] samplesIncorrectHeaderFormat() throws JOSEException {
        String accessToken = jws(claims(expirationTime(60)), publicKey).serialize();
        return new Object[][]{
            {""}, {accessToken}, {"Basic " + accessToken}, {"Bearer" + accessToken}, {"bearer " + accessToken}
        };
    }

    @Test(dataProvider = "samplesIncorrectHeaderFormat",
        expectedExceptions = AuthorizationException.class,
        expectedExceptionsMessageRegExp = "Incorrect header format")
    public void testIncorrectHeaderFormat(String header) {
        jwt.verify(header);
    }

    @DataProvider
    public Object[][] samplesIncorrectJWTToken() throws JOSEException {
        String accessToken = jws(claims(expirationTime(60)), publicKey).serialize();
        return new Object[][]{
            {"foo" + accessToken}, {"aa.bb.cc"}
        };
    }

    @Test(dataProvider = "samplesIncorrectJWTToken",
        expectedExceptions = AuthorizationException.class,
        expectedExceptionsMessageRegExp = "JWT couldn't be parsed")
    public void testIncorrectJWTToken(String accessToken) {
        jwt.verify("Bearer " + accessToken);
    }

    @Test
    public void testVerifyValidJWT() {
        Claims claims = jwt.verify("Bearer " + accessToken);

        Assert.assertNotNull(claims);
        Assert.assertEquals(claims.getUsername(), "foo");
        Assert.assertEquals(claims.getRoles(), Arrays.asList("role1", "role2"));
    }

    @Test(expectedExceptions = AuthorizationException.class,
        expectedExceptionsMessageRegExp = "JWS object didn't pass the verification")
    public void testUnknownJWT() throws JOSEException {
        JWTClaimsSet claims = claims(expirationTime(60), "role1", "role2");
        String header = "Bearer " + jws(claims, RSAKey(keysHolder.getPublic(), "foo")).serialize();

        jwt.verify(header);
    }

    @Test(expectedExceptions = AuthorizationException.class,
        expectedExceptionsMessageRegExp = "JWS object didn't pass the verification")
    public void testBadSignature() {
        jwt.verify("Bearer " + accessToken + "a");
    }

    @Test(expectedExceptions = AuthorizationException.class,
        expectedExceptionsMessageRegExp = "JWT has expired")
    public void testExpiredTime() throws JOSEException {
        String header = "Bearer " + jws(
            claims(expirationTime(-60), "role1", "role2"),
            publicKey).serialize();

        jwt.verify(header);
    }

    private JWSObject jws(JWTClaimsSet claims, RSAKey publicKey)
        throws JOSEException {
        JWSObject jws = new JWSObject(
            new JWSHeader(JWSAlgorithm.RS512, null, null, null, null, null, null, null, null, null, publicKey.getKeyID(), null, null),
            new Payload(claims.toJSONObject()));
        jws.sign(jwsSigner);
        return jws;
    }

    private static RSAKey RSAKey(PublicKey publicKey, String kid) {
        return new RSAKey((RSAPublicKey) publicKey, KeyUse.SIGNATURE, null, null, kid, null, null, null);
    }

    private static JWTClaimsSet claims(Date expirationTime, String... roles) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.claim("cognito:username", "foo");

        if (roles.length != 0) {
            builder.claim("cognito:roles", Arrays.asList(roles));
        }

        if (expirationTime != null) {
            builder.expirationTime(expirationTime);
        }

        return builder.build();
    }

    private static Date expirationTime(int offset) {
        return new Date(new Date().getTime() + offset * 1000);
    }

}