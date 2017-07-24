package com.github.vitalibo.authorization.jwt.core;

import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.time.ZoneId;

class ClaimsTranslator {

    private ClaimsTranslator() {
    }

    static Claims from(JWTClaimsSet claimsSet) throws ParseException {
        Claims claims = new Claims();
        claims.setUsername(claimsSet.getStringClaim("cognito:username"));
        claims.setRoles(claimsSet.getStringListClaim("cognito:roles"));
        claims.setExpiredAt(claimsSet.getExpirationTime()
            .toInstant().atZone(ZoneId.of("UTC")));
        return claims;
    }

}