package com.bettercloud.poc.scripts.handler;

import com.bettercloud.poc.scripts.model.JsonRpcRequest;
import com.bettercloud.poc.scripts.model.JsonRpcResponse;
import reactor.core.publisher.Mono;

public interface RpcHandler {

    String getMethod();

    Mono<JsonRpcResponse> handle(JsonRpcRequest req);
}
