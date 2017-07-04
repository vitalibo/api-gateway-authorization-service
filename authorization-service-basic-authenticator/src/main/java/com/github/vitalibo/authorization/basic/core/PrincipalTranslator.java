package com.github.vitalibo.authorization.basic.core;

import com.github.vitalibo.authorization.shared.core.Principal;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Optional;

final class PrincipalTranslator {

    private PrincipalTranslator() {
    }

    static Principal from(String token) {
        Principal principal = new Principal();
        JWTClaimsSet claims = parseJWT(token);
        principal.setId(claims.getSubject());
        principal.setUsername(
            Optional.ofNullable(claims.getClaim("username"))
                .map(String::valueOf).orElse(null));
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
