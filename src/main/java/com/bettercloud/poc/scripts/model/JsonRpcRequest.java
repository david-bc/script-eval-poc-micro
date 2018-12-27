package com.bettercloud.poc.scripts.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * According to https://www.jsonrpc.org/specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonRpcRequest {

    private String jsonrpc = "2.0";
    private String method;
    private JsonNode params;
    private JsonNode id;
}
