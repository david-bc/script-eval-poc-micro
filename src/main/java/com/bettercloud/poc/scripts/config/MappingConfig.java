package com.bettercloud.poc.scripts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig {

    public static ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public ObjectMapper jsonObjectMapper() {
        return JSON_OBJECT_MAPPER;
    }

}
