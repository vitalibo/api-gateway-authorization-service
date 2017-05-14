package com.github.vitalibo.auth.server.infrastructure.aws;

import com.github.vitalibo.auth.server.core.facade.ChangePasswordFacade;
import com.github.vitalibo.auth.server.core.facade.OAuth2ClientCredentialsFacade;
import com.github.vitalibo.auth.server.infrastructure.aws.cognito.CognitoUserPool;
import lombok.Getter;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class Factory {

    @Getter(lazy = true)
    private static final Factory instance = new Factory();
    @Getter(lazy = true)
    private static final VelocityEngine velocityEngine = makeVelocityEngine();

    public OAuth2ClientCredentialsFacade createOAuth2ClientCredentialsFacade() {
        return null;
    }

    public ChangePasswordFacade createChangePasswordFacade() {
        return new ChangePasswordFacade(new CognitoUserPool(), Factory.getVelocityEngine());
    }

    private static VelocityEngine makeVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.setProperty("resource.loader", "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        return ve;
    }

}
