package de.bach.recipeProvider.controller;

import de.bach.recipeProvider.mapper.RecipeMapper;
import de.bach.recipeProvider.mongodb.model.Recipe;
import de.bach.recipeProvider.mongodb.RecipesRepository;
import de.bach.recipeProvider.services.OpenAiRecipeGeneratorService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.openapitools.api.RecipesApi;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RecipeProviderController implements RecipesApi {

    private static final Logger log = LoggerFactory.getLogger(RecipeProviderController.class);

    private final OpenAiRecipeGeneratorService openAiRecipeGeneratorService;

    private final RecipesRepository recipesRepository;

    private static RecipeReadDto firstRecipe;
    private static RecipeReadDto secondRecipe;

    public RecipeProviderController(
            OpenAiRecipeGeneratorService openAiRecipeGeneratorService,
            RecipesRepository recipesRepository
    ) {
        this.openAiRecipeGeneratorService = openAiRecipeGeneratorService;
        this.recipesRepository = recipesRepository;
        List<Recipe> recipes = this.recipesRepository.findAll();
        firstRecipe = RecipeMapper.toRecipeReadDto(recipes.get(0));
        secondRecipe = RecipeMapper.toRecipeReadDto(recipes.get(1));
    }

    @GetMapping(path = "/test", produces = "application/json")
    public String ping() {
        return "Hello World";
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return RecipesApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> getAllRecipes() {
        List<Recipe> recipes = recipesRepository.findAll();
        List<RecipeReadDto> recipeReadDtos = recipes.stream()
                .map(RecipeMapper::toRecipeReadDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeReadDtos);
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> createRecipes(List<@Valid RecipeWriteDto> recipeWriteDtos) {
        List<Recipe> recipes = recipesRepository.saveAll(recipeWriteDtos.stream().map(RecipeMapper::toRecipe).toList());
        return ResponseEntity.ok(recipes.stream().map(RecipeMapper::toRecipeReadDto).toList());
    }

    @Override
    public ResponseEntity<RecipeReadDto> deleteRecipeById(String id) {
        Recipe recipe = this.recipesRepository.findById(new ObjectId(id)).get();
        this.recipesRepository.deleteById(new ObjectId(id));
        return ResponseEntity.ok( RecipeMapper.toRecipeReadDto(recipe));
    }

    @Override
    public ResponseEntity<Void> deleteAllRecipes() {
        this.recipesRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<RecipeReadDto> getRecipeById(String id) {
        Recipe recipe = this.recipesRepository.findById(new ObjectId(id)).get();
        return ResponseEntity.ok(RecipeMapper.toRecipeReadDto(recipe));
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> getActualMenu(Boolean random) {
        if (random) {
            System.out.println("Generating new recipes...");
            try {
                List<String> recipesTitlesInDatabase = recipesRepository.findAll().stream().map(Recipe::getTitle).toList();
                List<String> ingredient = List.of("Schwein","Frikadellen","Rind","Geflügel","Fisch","Vegetarisch", "Nudeln", "Reis", "Auflauf","Eintopf");
                String randomIngredient = ingredient.get(new Random().nextInt(ingredient.size()));
                String actualRecipes = String.join(", ", recipesTitlesInDatabase);
                String recipePrompt = """
                Generiere bitte ein leckeres Rezept für 3 Personen.
                Wir essen gern %s.
                Bitte die folgenden Gerichte nicht wiederholen: %s
                """.formatted(randomIngredient,actualRecipes);

                String childrenRecipePrompt = """
                Generiere bitte ein leckeres Rezept für 2 Kinder.
                Wir essen gern %s.
                Bitte die folgenden Gerichte nicht wiederholen: %s
                """.formatted(randomIngredient,actualRecipes);
                RecipeWriteDto firstWriteDto = this.openAiRecipeGeneratorService.generateRecipe(recipePrompt);
                RecipeWriteDto secondWriteDto  = this.openAiRecipeGeneratorService.generateRecipe(childrenRecipePrompt);
                List<Recipe> recipes = recipesRepository.saveAll(List.of(RecipeMapper.toRecipe(firstWriteDto) ,RecipeMapper.toRecipe(secondWriteDto)));
                RecipeProviderController.firstRecipe = RecipeMapper.toRecipeReadDto(recipes.get(0));
                RecipeProviderController.secondRecipe = RecipeMapper.toRecipeReadDto(recipes.get(1));
                return ResponseEntity.ok(List.of(RecipeProviderController.firstRecipe, RecipeProviderController.secondRecipe));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(List.of(RecipeProviderController.firstRecipe, RecipeProviderController.secondRecipe));
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> updateMenu(List<RecipeReadDto> recipeReadDto) {
        if (recipeReadDto.size() != 2) {
            return ResponseEntity.badRequest().build();
        }
        firstRecipe = recipeReadDto.get(0);
        secondRecipe = recipeReadDto.get(1);
        return ResponseEntity.ok(List.of(firstRecipe, secondRecipe));
    }
}
