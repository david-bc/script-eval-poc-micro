package com.bettercloud.poc.scripts.handler;

import com.bettercloud.poc.scripts.model.JsonRpcRequest;
import com.bettercloud.poc.scripts.model.JsonRpcResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GroovyScriptRpcHandler implements RpcHandler {

    public static final String METHOD = "scripting.groovy.eval";

    private final ScriptEvaluator scriptEvaluator;
    private final ObjectMapper jsonObjectMapper;

    public GroovyScriptRpcHandler(ScriptEvaluator scriptEvaluator, ObjectMapper jsonObjectMapper) {
        this.scriptEvaluator = scriptEvaluator;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public Mono<JsonRpcResponse> handle(JsonRpcRequest req) {
        JsonRpcResponse.JsonRpcError error;
        try {
            GroovyScript groovyScript = jsonObjectMapper.convertValue(req.getParams(), GroovyScript.class);
            return Mono.just(
                    JsonRpcResponse.builder()
                        .jsonrpc("2.0")
                        .result(eval(groovyScript))
                        .id(req.getId())
                        .build()
            );
        } catch (Exception e) {
            JsonNode stacktrace = jsonObjectMapper.valueToTree(e.getStackTrace());
            ObjectNode data = jsonObjectMapper.createObjectNode()
                    .put("message", e.getMessage());
            data.set("stacktrace", stacktrace);
            error = JsonRpcResponse.JsonRpcError.builder()
                    .code(JsonRpcResponse.JsonRpcError.ERROR_INTERNAL_EXCEPTION_CODE)
                    .message(JsonRpcResponse.JsonRpcError.ERROR_INTERNAL_EXCEPTION_TEXT)
                    .data(data)
                    .build();
        }
        return Mono.just(
                JsonRpcResponse.builder()
                    .jsonrpc("2.0")
                    .error(error)
                    .id(req.getId())
                    .build()
        );
    }

    public JsonNode eval(GroovyScript groovyScript) {
        StaticScriptSource src = new StaticScriptSource(groovyScript.getSrc(), groovyScript.getName());
        Map<String, Object> input = Maps.newHashMap();
        input.put("input", groovyScript.getParams());
        // TODO: more stuff
        return jsonObjectMapper.valueToTree(scriptEvaluator.evaluate(src, input));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class GroovyScript {

        private String name;
        private String src;
        private Map<String, Object> params;
    }
}
