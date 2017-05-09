package com.github.vitalibo.auth.server.core;

import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyRequest;
import com.github.vitalibo.auth.infrastructure.aws.gateway.proxy.ProxyResponse;

public interface Facade {

    ProxyResponse process(ProxyRequest request);

}
