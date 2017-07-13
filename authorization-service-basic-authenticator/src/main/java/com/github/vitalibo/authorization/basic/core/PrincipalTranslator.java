package com.github.vitalibo.authorization.basic.core;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.text.ParseException;

final class PrincipalTranslator {

    private PrincipalTranslator() {
    }

    @SneakyThrows
    static Principal from(String token) {
        Principal principal = new Principal();
        JWTClaimsSet claims = parseJWT(token);
        principal.setId(claims.getSubject());
        principal.setUsername
            (claims.getStringClaim("cognito:username"));
        principal.setScope(
            claims.getStringListClaim("cognito:groups"));
        principal.setExpirationTime(
            claims.getExpirationTime().getTime());
        return principal;
    }

    private static JWTClaimsSet parseJWT(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (ParseException e) {
            return new JWTClaimsSet.Builder().build();
        }
    }

}
