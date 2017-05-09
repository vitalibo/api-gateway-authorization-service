package com.github.vitalibo.auth.server.infrastructure.aws;

import com.github.vitalibo.auth.server.core.facade.OAuth2ClientCredentialsFacade;
import lombok.Getter;

public class Factory {

    @Getter(lazy = true)
    private static final Factory factory = new Factory();

    public OAuth2ClientCredentialsFacade createOAuth2ClientCredentialsFacade() {
        return null;
    }

}
