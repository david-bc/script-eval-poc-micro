package com.bettercloud.poc.scripts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;

@Configuration
public class ScriptingConfig {

    public static final GroovyScriptEvaluator GROOVY_SCRIPT_EVALUATOR = new GroovyScriptEvaluator();

    @Bean
    public GroovyScriptEvaluator groovyScriptEvaluator() {
        return GROOVY_SCRIPT_EVALUATOR;
    }
}
