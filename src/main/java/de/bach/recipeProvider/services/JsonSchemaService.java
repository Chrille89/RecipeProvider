package de.bach.recipeProvider.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;
import org.springframework.stereotype.Service;

@Service
public class JsonSchemaService {

    public String generateSchema() {

        SchemaGeneratorConfigBuilder configBuilder =
                new SchemaGeneratorConfigBuilder(
                        SchemaVersion.DRAFT_2020_12,
                        OptionPreset.PLAIN_JSON
                );

        SchemaGenerator generator =
                new SchemaGenerator(configBuilder.build());

        JsonNode jsonSchema =
                generator.generateSchema(RecipeWriteDto.class);

        return jsonSchema.toPrettyString();
    }
}