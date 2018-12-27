package com.bettercloud.poc.scripts.handler;

import com.bettercloud.poc.scripts.config.MappingConfig;
import com.bettercloud.poc.scripts.config.ScriptingConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GroovyScriptRpcHandlerTest {

    public GroovyScriptRpcHandler handler = new GroovyScriptRpcHandler(ScriptingConfig.GROOVY_SCRIPT_EVALUATOR,
            MappingConfig.JSON_OBJECT_MAPPER);

    private Map<String, Object> m(Pair<String, Object>...pairs) {
        return Lists.newArrayList(pairs).stream()
                .collect(Collectors.toMap(
                        p -> p.key,
                        p -> p.value
                ));
    }

    private GroovyScriptRpcHandler.GroovyScript getGroovyScript(String filename, Map<String, Object> params) throws IOException {
        return GroovyScriptRpcHandler.GroovyScript.builder()
                .name(filename)
                .src(Resources.toString(Resources.getResource(filename), Charset.defaultCharset()))
                .params(params)
                .build();
    }

    @Test
    public void getMethod() {
        assertEquals("scripting.groovy.eval", handler.getMethod());
    }

    @Test
    public void handle() {
        // #YOLO
    }

    @Test
    public void eval_constSadPath() {
        GroovyScriptRpcHandler.GroovyScript input = GroovyScriptRpcHandler.GroovyScript.builder()
                .name("TestingScript123")
                .src("19+1")
                .params(Collections.emptyMap())
                .build();
        JsonNode expected = IntNode.valueOf(42);

        JsonNode actual = handler.eval(input);

        assertNotEquals(expected, actual);
    }

    @Test
    public void eval_constNumbers() {
        GroovyScriptRpcHandler.GroovyScript input = GroovyScriptRpcHandler.GroovyScript.builder()
                .name("TestingScript123")
                .src("19+1")
                .params(Collections.emptyMap())
                .build();
        JsonNode expected = IntNode.valueOf(20);

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_constString() {
        GroovyScriptRpcHandler.GroovyScript input = GroovyScriptRpcHandler.GroovyScript.builder()
                .name("TestingScript123")
                .src("'Hello, World!'")
                .params(Collections.emptyMap())
                .build();
        JsonNode expected = TextNode.valueOf("Hello, World!");

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_paramsNumbers() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("x", 121);
        params.put("y", 242);
        GroovyScriptRpcHandler.GroovyScript input = GroovyScriptRpcHandler.GroovyScript.builder()
                .name("TestingScript123")
                .src("x + y")
                .params(params)
                .build();
        JsonNode expected = IntNode.valueOf(363);

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_paramsString() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("x", "Hello");
        params.put("y", "World");
        GroovyScriptRpcHandler.GroovyScript input = GroovyScriptRpcHandler.GroovyScript.builder()
                .name("TestingScript123")
                .src("String.format('%s, %s!', x, y)")
                .params(params)
                .build();
        JsonNode expected = TextNode.valueOf("Hello, World!");

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_addFunc() throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("x", 38);
        params.put("y", 4);
        GroovyScriptRpcHandler.GroovyScript input = getGroovyScript("scripts/addFunction.groovy", params);

        JsonNode expected = IntNode.valueOf(42);

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_addFuncString() throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("x", "This is ");
        params.put("y", "only a test!");
        GroovyScriptRpcHandler.GroovyScript input = getGroovyScript("scripts/addFunction.groovy", params);

        JsonNode expected = TextNode.valueOf("This is only a test!");

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_trxUser() throws Exception {
        Map<String, Object> params = m(
                Pair.of("entity", m(
                        Pair.of("kind", "USER"),
                        Pair.of("raw", m(
                                Pair.of("id", "asdf123"),
                                Pair.of("name", "David Hardwick"),
                                Pair.of("profile", m(
                                        Pair.of("login", "davidH123"),
                                        Pair.of("emails", Lists.newArrayList("dh@bc.com", "davidH@better.cloud"))
                                ))
                        ))
                ))
        );
        GroovyScriptRpcHandler.GroovyScript input = getGroovyScript("scripts/transformer.groovy", params);

        ObjectNode expected = MappingConfig.JSON_OBJECT_MAPPER.createObjectNode()
                .put("externalId", "asdf123")
                .put("username", "davidH123")
                .put("email", "dh@bc.com")
                .put("display", "David Hardwick")
                .put("name", "David Hardwick");

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_trxGroup() throws Exception {
        Map<String, Object> params = m(
                Pair.of("entity", m(
                        Pair.of("kind", "GROUP"),
                        Pair.of("raw", m(
                                Pair.of("id", "asdf123"),
                                Pair.of("name", "Test Group #16233"),
                                Pair.of("email", "group16233@test.com")
                        ))
                ))
        );
        GroovyScriptRpcHandler.GroovyScript input = getGroovyScript("scripts/transformer.groovy", params);

        ObjectNode expected = MappingConfig.JSON_OBJECT_MAPPER.createObjectNode()
                .put("externalId", "asdf123")
                .put("email", "group16233@test.com")
                .put("display", "Test Group #16233");

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    @Test
    public void eval_trxUnknown() throws Exception {
        Map<String, Object> params = m(
                Pair.of("entity", m(
                        Pair.of("kind", "BIGFOOT"),
                        Pair.of("raw", m(
                                Pair.of("id", "asdf123"),
                                Pair.of("name", "Test Group #16233"),
                                Pair.of("email", "group16233@test.com")
                        ))
                ))
        );
        GroovyScriptRpcHandler.GroovyScript input = getGroovyScript("scripts/transformer.groovy", params);

        ObjectNode expected = MappingConfig.JSON_OBJECT_MAPPER.createObjectNode();

        JsonNode actual = handler.eval(input);

        assertEquals(expected, actual);
    }

    private static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public static <A, B> Pair<A, B> of(A key, B value) {
            return new Pair<>(key, value);
        }
    }
}