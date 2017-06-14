package com.github.vitalibo.auth.jwt.core;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Jwt {

    private static final Pattern AUTHORIZATION_HEADER_PATTERN =
        Pattern.compile("Bearer (?<jwt>[0-9A-Za-z.\\-_]*)");

    private final JWKSource<? extends SecurityContext> jwkSource;

    public Claims verify(String header) {
        Matcher matcher = AUTHORIZATION_HEADER_PATTERN.matcher(header);
        if (!matcher.matches()) {
            throw new AuthorizationException("Incorrect header format");
        }

        try {
            return checkJWT(matcher.group("jwt"));
        } catch (ParseException | KeySourceException e) {
            throw new AuthorizationException("JWT couldn't be parsed");
        }
    }

    private Claims checkJWT(String authorization) throws ParseException, KeySourceException {
        SignedJWT jwt = SignedJWT.parse(authorization);
        if (!checkSignature(jwt)) {
            throw new AuthorizationException("JWS object didn't pass the verification");
        }

        JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
        checkExpirationTime(claimsSet);

        return ClaimsTranslator.from(claimsSet);
    }

    private boolean checkSignature(SignedJWT jwt) throws KeySourceException {
        JWKSelector selector = new JWKSelector(
            new JWKMatcher.Builder()
                .keyID(jwt.getHeader().getKeyID())
                .publicOnly(true)
                .build());

        List<JWK> keys = jwkSource.get(selector, null);
        try {
            return !keys.isEmpty()
                && jwt.verify(new RSASSAVerifier((RSAKey) keys.get(0)));
        } catch (JOSEException e) {
            throw new AuthorizationException("JWS object couldn't be verified");
        }
    }

    private void checkExpirationTime(JWTClaimsSet claimsSet) throws ParseException {
        final Date expirationTime = claimsSet.getExpirationTime();
        if (expirationTime == null || new Date().after(expirationTime)) {
            throw new AuthorizationException("JWT has expired");
        }
    }

}