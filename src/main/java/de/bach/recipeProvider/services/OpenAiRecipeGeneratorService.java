package de.bach.recipeProvider.services;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;
import org.springframework.stereotype.Component;

@Component
public class OpenAiRecipeGeneratorService {

    private final OpenAIClient client;
    private final JsonSchemaService schemaService;
    private final ObjectMapper mapper;

    public OpenAiRecipeGeneratorService(
            JsonSchemaService schemaService,
            ObjectMapper mapper
    ) {
        this.schemaService = schemaService;
        this.mapper = mapper;
        // Initialize OpenAI client
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
    }

    public RecipeWriteDto generateRecipe(String recipePrompt) throws Exception {
        String schema = schemaService.generateSchema();

        // Prompt explicitly enforces allowed units
        String prompt = recipePrompt +
                """
            
                Requirements:
                - return ONLY JSON
                - follow exactly this schema
                - use integer amounts
                - include a working food image URL
                - all labels in lowercase!
                - please recipe in german language!
                
                Schema:
                """ + schema;

        // Send prompt to OpenAI
        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model("gpt-4.1")
                        .addUserMessage(prompt)
                        .build();

        ChatCompletion completion =
                client.chat()
                        .completions()
                        .create(params);

        // Extract JSON string from AI response
        String json =
                completion.choices()
                        .get(0)
                        .message()
                        .content()
                        .get();

        String normalizedJson = json.toLowerCase()
                .replace("\"st\"", "\"St\"")
                .replace("\"essl_fel\"", "\"Esslöfel\"")
                .replace("\"teel_fel\"", "\"Teelöfel\"")
                .replace("\"gefl_gel\"", "\"Geflügel\"")
                .replace("\"kalorienarm\"", "\"Kalorienarm\"")
                .replace("\"fettarm\"", "\"Fettarm\"")
                .replace("\"eiwei_\"", "\"Eiweiß\"")
                .replace("\"fettarm\"", "\"Fettarm\"")
                .replace("\"vegetarisch\"", "\"Vegetarisch\"")
                .replace("\"vegan\"", "\"Vegan\"")
                .replace("\"schwein\"", "\"Schwein\"")
                .replace("\"rind\"", "\"Rind\"")
                .replace("\"geflügel\"", "\"Geflügel\"")
                .replace("\"fisch\"", "\"Fisch\"")
                .replace("\"thermomix\"", "\"Thermomix\"")
                .replace("\"airfryer\"", "\"Airfryer\"")
                .replace("\"express\"", "\"Express\"");

        // Deserialize into RecipeReadDto (uses case-insensitive enums)
        RecipeWriteDto recipe = mapper.readValue(normalizedJson, RecipeWriteDto.class);

        // Optional: validate that each ingredient uses allowed units
        recipe.getIngredients().forEach(a -> {
            if (a.getUnit() == null || !isAllowedUnit(a.getUnit().getValue())) {
                throw new IllegalArgumentException("Invalid unit from AI: " + a.getUnit());
            }
        });
        return recipe;
    }

    private boolean isAllowedUnit(String unit) {
        if (unit == null) return false;
        return switch (unit) {
            case "g", "kg", "St", "ml", "l", "Esslöfel", "Teelöfel", "kcal" -> true;
            default -> false;
        };
    }
}