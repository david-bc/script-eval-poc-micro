package com.bettercloud.poc.scripts;

import com.google.common.collect.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class ScriptEvalPocMicroApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptEvalPocMicroApplication.class, args);
	}

	@Bean
	public CommandLineRunner groovyTest(GroovyScriptEvaluator scriptEvaluator) {
		return args -> {
			StaticScriptSource src = new StaticScriptSource("def add(a, b) { return calculator.add(a, b) }\n\nadd(x, y + 3)", UUID.randomUUID().toString());
			Map<String, Object> params = Maps.newHashMap();
			params.put("x", 11);
			params.put("y", 31);
			params.put("calculator", new Calcuator());
			Object output = scriptEvaluator.evaluate(src, params);

			System.out.println("output = " + output);
		};
	}

	public static class Calcuator {

		public static int ADD(int x, int y) {
			return x + y;
		}

		public int add(int x, int y) {
			return Calcuator.ADD(x, y);
		}
	}
}

