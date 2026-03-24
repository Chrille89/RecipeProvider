package de.bach.recipeProvider.services;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ImageGenerateParams;
import com.openai.models.ImagesResponse;
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
            
                Anforderungen:
                - return NUR JSON
                - folge exakt dem Schema
                - verwende Integer Mengen-Angaben
                - alle labels bitte kleingeschrieben!
                - Rezepte bitte in deutscher Sprache
                
                Das Image muss eine real existierende URL sein.
                Mögliche domains:
                - hellofresh.com
                - chefkoch.de
                - kochbar.de
                - eat.de
                - tmecosys.com
                
                Bitte erzeuge eine direkte image URL, die den Status-Code 200 zurückgibt. 
                Bitte erfinde keine eigene URL.

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
                .replace("\"el\"", "\"EL\"")
                .replace("\"tl\"", "\"TL\"")
                .replace("\"prise\"", "\"Prise\"")
                .replace("\"gefl_gel\"", "\"Geflügel\"")
                .replace("\"kalorienarm\"", "\"Kalorienarm\"")
                .replace("\"fettarm\"", "\"Fettarm\"")
                .replace("\"eiwei_\"", "\"Eiweiß\"")
                .replace("\"fettarm\"", "\"Fettarm\"")
                .replace("\"vegetarisch\"", "\"Vegetarisch\"")
                .replace("\"vegan\"", "\"Vegan\"")
                .replace("\"schwein\"", "\"Schwein\"")
                .replace("\"rind\"", "\"Rind\"")
                .replace("\"fisch\"", "\"Fisch\"")
                .replace("\"thermomix\"", "\"Thermomix\"")
                .replace("\"airfryer\"", "\"Airfryer\"")
                .replace("\"ofen\"", "\"Ofen\"")
                .replace("\"r_mertopf\"", "\"Römertopf\"")
                .replace("\"express\"", "\"Express\"");

        // Deserialize into RecipeReadDto (uses case-insensitive enums)
        RecipeWriteDto recipe = mapper.readValue(normalizedJson, RecipeWriteDto.class);

        recipe.setImageBase64(this.generateImage(recipe.getTitle()));

        // Optional: validate that each ingredient uses allowed units
        recipe.getIngredients().forEach(a -> {
            if (a.getUnit() == null || !isAllowedUnit(a.getUnit().getValue())) {
                throw new IllegalArgumentException("Invalid unit from AI: " + a.getUnit());
            }
        });
        return recipe;
    }

    private String generateImage(String recipeTitle) {
        ImagesResponse image = client.images().generate(
                ImageGenerateParams.builder()
                        .model("gpt-image-1")
                        .prompt(recipeTitle+", low quality 512x512 food image")
                        .build());
        String base64 = image.data().get(0).b64Json().get();
        return base64;
    }

    private boolean isAllowedUnit(String unit) {
        if (unit == null) return false;
        return switch (unit) {
            case "g", "kg", "St", "ml", "l", "EL", "TL", "Prise", "kcal" -> true;
            default -> false;
        };
    }
}