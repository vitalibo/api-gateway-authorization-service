package com.github.vitalibo.authorization.server.core;

import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.authorization.shared.infrastructure.aws.gateway.proxy.ProxyResponse;

public interface Facade {

    ProxyResponse process(ProxyRequest request) throws Exception;

}
