package de.bach.recipeProvider.services;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import org.openapitools.model.RecipeReadDto;
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

    public RecipeReadDto generateRecipe() throws Exception {
        String schema = schemaService.generateSchema();

        // Prompt explicitly enforces allowed units
        String prompt = """
                Generate a healthy and delicious recipe.
                
                Requirements:
                - return ONLY JSON
                - follow exactly this schema
                - use integer amounts
                - include a working food image URL
                - all labels in lowercase!
                - please recipe in german language!
                
                Extract the ingredients as a JSON array of objects with the following schema:
                
                {
                   "name": "<ingredient name>",
                   "amount": <number>,
                   "unit": "<unit>"
                }
                
                **Important:** The "unit" field must be one of the following:
                g, kg, St, ml, l, Esslöfel, Teelöfel, kcal
                No other units are allowed.
                
                Example:
                {
                   "name": "Flour",
                   "amount": 200,
                   "unit": "g"
                }
                
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
                .replace("\"esslöfel\"", "\"Esslöfel\"")
                .replace("\"teelöfel\"", "\"Teelöfel\"")
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
        RecipeReadDto recipe = mapper.readValue(normalizedJson, RecipeReadDto.class);

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