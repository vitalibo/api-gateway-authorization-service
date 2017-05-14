package com.github.vitalibo.auth.server.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.vitalibo.auth.core.ErrorState;
import com.github.vitalibo.auth.core.HttpError;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequestTranslator;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;
import com.github.vitalibo.auth.server.core.Facade;
import com.github.vitalibo.auth.server.core.Route;
import com.github.vitalibo.auth.server.core.Router;
import com.github.vitalibo.auth.server.infrastructure.aws.Factory;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ProxyRequestHandler implements RequestHandler<ProxyRequest, ProxyResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ProxyRequestHandler.class);

    private final Factory factory;

    public ProxyRequestHandler() {
        this(Factory.getInstance());
    }

    @Override
    public ProxyResponse handleRequest(ProxyRequest o, Context context) {
        final ProxyRequest request = ProxyRequestTranslator.ofNullable(o);
        logger.debug("invoke lambda with {}", request);

        final Route route = Router.match(request);
        final Facade facade;
        switch (route) {
            case CHANGE_PASSWORD:
                facade = factory.createChangePasswordFacade();
                break;

            case OAUTH2_CLIENT_CREDENTIALS:
                facade = factory.createOAuth2ClientCredentialsFacade();
                break;

            case NOT_FOUND:
                return new HttpError.Builder()
                    .withStatusCode(HttpStatus.SC_NOT_FOUND)
                    .withRequestId(context.getAwsRequestId())
                    .build()
                    .asProxyResponse();

            default:
                throw new IllegalStateException();
        }

        try {
            return facade.process(request);
        } catch (ErrorState errorState) {
            return new HttpError.Builder()
                .withStatusCode(HttpStatus.SC_BAD_REQUEST)
                .withErrorState(errorState)
                .withRequestId(context.getAwsRequestId())
                .build()
                .asProxyResponse();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw e;
        }
    }

}
