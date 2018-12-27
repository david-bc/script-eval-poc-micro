package com.bettercloud.poc.scripts.config;

import com.bettercloud.poc.scripts.handler.RpcHandler;
import com.bettercloud.poc.scripts.model.JsonRpcRequest;
import com.bettercloud.poc.scripts.model.JsonRpcResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class RoutingConfig {

    @Bean
    public RouterFunction<ServerResponse> rpcResponseRouterFunction(List<RpcHandler> handlers) {
        Map<String, RpcHandler> handlersMap = handlers.stream().collect(Collectors.toMap(
                h -> h.getMethod(),
                h -> h
        ));
        return RouterFunctions
                .route(
                        RequestPredicates.POST("/rpc/v1/sync"),
                        request -> request.bodyToMono(JsonRpcRequest.class)
                                .flatMap(req -> Optional.ofNullable(handlersMap.get(req.getMethod()))
                                        .map(h -> h.handle(req))
                                        .orElseGet(() -> Mono.just(JsonRpcResponse.builder()
                                                .jsonrpc("2.0")
                                                .id(req.getId())
                                                .error(JsonRpcResponse.JsonRpcError.builder()
                                                        .code(JsonRpcResponse.JsonRpcError.ERROR_METHOD_NOT_FOUND_CODE)
                                                        .message(JsonRpcResponse.JsonRpcError.ERROR_METHOD_NOT_FOUND_TEXT)
                                                        .build())
                                                .build())))
                                .flatMap(body -> ServerResponse.ok()
                                        .body(BodyInserters.fromObject(body))
                                )
                );
    }
}
