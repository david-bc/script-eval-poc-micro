package com.bettercloud.poc.scripts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
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
public class JsonRpcResponse {

    private String jsonrpc = "2.0";
    private JsonNode result;
    private JsonRpcError error;
    private JsonNode id;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonRpcError {

        public static final int ERROR_PARSE_CODE = -32700;
        public static final String ERROR_PARSE_TEXT = "Parse error";
        public static final String ERROR_PARSE_DESCRIPTION = "Invalid JSON was received by the server. An error occurred on the server while parsing the JSON text.";
        public static final int ERROR_INVALID_REQ_CODE = -32600;
        public static final String ERROR_INVALID_REQ_TEXT = "Invalid Request";
        public static final String ERROR_INVALID_REQ_DESCRIPTION = "The JSON sent is not a valid Request object.";
        public static final int ERROR_METHOD_NOT_FOUND_CODE = -32601;
        public static final String ERROR_METHOD_NOT_FOUND_TEXT = "Method not found";
        public static final String ERROR_METHOD_NOT_FOUND_DESCRIPTION = "The method does not exist / is not available.";
        public static final int ERROR_INVALID_PARAMS_CODE = -32602;
        public static final String ERROR_INVALID_PARAMS_TEXT = "Method not found";
        public static final String ERROR_INVALID_PARAMS_DESCRIPTION = "Invalid method parameter(s).";
        public static final int ERROR_INTERNAL_EXCEPTION_CODE = -32603;
        public static final String ERROR_INTERNAL_EXCEPTION_TEXT = "Internal error";
        public static final String ERROR_INTERNAL_EXCEPTION_DESCRIPTION = "Internal JSON-RPC error.";
        public static final int ERROR_SERVER_EXCEPTION_CODE = -32000;
        public static final int ERROR_SERVER_EXCEPTION_CODE_MAX = -32000;
        public static final String ERROR_SERVER_EXCEPTION_TEXT = "Server error";
        public static final String ERROR_SERVER_EXCEPTION_DESCRIPTION = "Reserved for implementation-defined server-errors.";

        private int code;
        private String message;
        private JsonNode data;
    }
}
