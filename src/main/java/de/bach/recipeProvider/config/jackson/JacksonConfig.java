package de.bach.recipeProvider.config.jackson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Case-insensitive Enums
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

        // Optional: JsonNullable Unterstützung von OpenAPI Generator
        mapper.registerModule(new JsonNullableModule());

        return mapper;
    }
}