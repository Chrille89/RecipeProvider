package de.bach.recipeProvider.controller;

import de.bach.recipeProvider.mongodb.model.Amount;
import de.bach.recipeProvider.mongodb.model.LabelEnum;
import de.bach.recipeProvider.mongodb.model.Recipe;
import de.bach.recipeProvider.mongodb.RecipesRepository;
import de.bach.recipeProvider.mongodb.model.UnitEnum;
import de.bach.recipeProvider.services.OpenAiRecipeGeneratorService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.openapitools.api.RecipesApi;
import org.openapitools.model.AmountDto;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RecipeProviderController implements RecipesApi {

    private static long time;

    private OpenAiRecipeGeneratorService openAiRecipeGeneratorService;
    private RecipesRepository recipesRepository;

    private static RecipeReadDto firstRecipe;

    private static RecipeReadDto secondRecipe;

    public RecipeProviderController(
            OpenAiRecipeGeneratorService openAiRecipeGeneratorService,
            RecipesRepository recipesRepository
    ) {
        this.openAiRecipeGeneratorService = openAiRecipeGeneratorService;
        this.recipesRepository = recipesRepository;
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
        List<RecipeReadDto> recipeReadDtos = recipes.stream().map(recipe ->
                        new RecipeReadDto()
                                .id(recipe.id)
                                .title(recipe.title)
                                .subtitle(recipe.subtitle)
                                .labels(recipe.getLabels()
                                        .stream()
                                        .map(labelEnum -> RecipeReadDto.LabelsEnum.fromValue(labelEnum.getValue())).collect(Collectors.toList()))
                                .duration(recipe.duration)
                                .image(recipe.image)
                                .nutrients(recipe
                                        .getNutrients()
                                        .stream()
                                        .map(amount -> new AmountDto()
                                                .name(amount.getName())
                                                .amount(amount.getAmount())
                                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                                        .collect(Collectors.toList()))
                                .preparation(recipe.getPreparation())
                                .ingredients(recipe
                                        .getIngredients()
                                        .stream()
                                        .map(amount -> new AmountDto()
                                                .name(amount.getName())
                                                .amount(amount.getAmount())
                                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeReadDtos);
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> createRecipes(List<@Valid RecipeWriteDto> recipeWriteDtos) {
        return ResponseEntity.ok(recipeWriteDtos.stream().map(this::createRecipe).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<RecipeReadDto> deleteRecipeById(String id) {
        Recipe recipe = this.recipesRepository.findById(new ObjectId(id)).get();
        this.recipesRepository.deleteById(new ObjectId(id));
        RecipeReadDto recipeReadDto = new RecipeReadDto()
                .id(recipe.id)
                .title(recipe.title)
                .subtitle(recipe.subtitle)
                .labels(recipe.getLabels()
                        .stream()
                        .map(labelEnum -> RecipeReadDto.LabelsEnum.fromValue(labelEnum.getValue())).collect(Collectors.toList()))
                .duration(recipe.duration)
                .image(recipe.image)
                .nutrients(recipe
                        .getNutrients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                        .collect(Collectors.toList()))
                .preparation(recipe.getPreparation())
                .ingredients(recipe
                        .getIngredients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name()))).collect(Collectors.toList()));
        return ResponseEntity.ok(recipeReadDto);
    }

    @Override
    public ResponseEntity<RecipeReadDto> getRecipeById(String id) {
        Recipe recipe = this.recipesRepository.findById(new ObjectId(id)).get();
        RecipeReadDto recipeReadDto = new RecipeReadDto()
                .id(recipe.id)
                .title(recipe.title)
                .subtitle(recipe.subtitle)
                .labels(recipe.getLabels()
                        .stream()
                        .map(labelEnum -> RecipeReadDto.LabelsEnum.fromValue(labelEnum.getValue())).collect(Collectors.toList()))
                .duration(recipe.duration)
                .image(recipe.image)
                .nutrients(recipe
                        .getNutrients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                        .collect(Collectors.toList()))
                .preparation(recipe.getPreparation())
                .ingredients(recipe
                        .getIngredients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name()))).collect(Collectors.toList()));
        return ResponseEntity.ok(recipeReadDto);
    }

    @Override
    public ResponseEntity<List<RecipeReadDto>> getActualMenu(Boolean random) {
        if (RecipeProviderController.firstRecipe == null && RecipeProviderController.secondRecipe == null || random) {

            try {
                RecipeProviderController.firstRecipe = this.openAiRecipeGeneratorService.generateRecipe();
                RecipeProviderController.secondRecipe = this.openAiRecipeGeneratorService.generateRecipe();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
           /*
            List<Recipe> recipes = recipesRepository.findAll();
            Random r = new Random();
            int randomIndex = r.nextInt(recipes.size());
            final Recipe firstRecipe = recipes.get(randomIndex);

            recipes = recipes.stream().filter(recipe -> !recipe.id.equals(firstRecipe.id)).collect(Collectors.toList());
            randomIndex = r.nextInt(recipes.size());
            Recipe secondRecipe = recipes.get(randomIndex);

            RecipeProviderController.firstRecipe = createReadDto(firstRecipe);
            RecipeProviderController.secondRecipe = createReadDto(secondRecipe);
            */

            return ResponseEntity.ok(List.of(RecipeProviderController.firstRecipe, RecipeProviderController.secondRecipe));
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

    private RecipeReadDto createRecipe(RecipeWriteDto recipeWriteDto) {
        Recipe recipe = new Recipe(
                recipeWriteDto.getTitle(),
                recipeWriteDto.getSubtitle(),
                recipeWriteDto.getLabels()
                        .stream()
                        .map(labelsEnum -> LabelEnum.valueOf(labelsEnum.name()))
                        .collect(Collectors.toList()),
                recipeWriteDto.getDuration(),
                recipeWriteDto.getImage(),
                recipeWriteDto
                        .getIngredients()
                        .stream()
                        .map(amountDto -> new Amount(amountDto.getName(), amountDto.getAmount(), UnitEnum.valueOf(amountDto.getUnit().name())))
                        .collect(Collectors.toList()),
                recipeWriteDto
                        .getNutrients()
                        .stream()
                        .map(amountDto -> new Amount(amountDto.getName(), amountDto.getAmount(), UnitEnum.valueOf(amountDto.getUnit().name())))
                        .collect(Collectors.toList()),
                recipeWriteDto.getPreparation());
        recipe = recipesRepository.save(recipe);

        RecipeReadDto recipeReadDto = new RecipeReadDto()
                .id(recipe.id)
                .title(recipe.title)
                .subtitle(recipe.subtitle)
                .labels(recipe.getLabels()
                        .stream()
                        .map(labelEnum -> RecipeReadDto.LabelsEnum.fromValue(labelEnum.getValue())).collect(Collectors.toList()))
                .duration(recipe.duration)
                .image(recipe.image)
                .nutrients(recipe
                        .getNutrients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                        .collect(Collectors.toList()))
                .preparation(recipe.getPreparation())
                .ingredients(recipe
                        .getIngredients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name()))).collect(Collectors.toList()));
        return recipeReadDto;
    }

    private RecipeReadDto createReadDto(Recipe recipe) {
        RecipeReadDto recipeReadDto = new RecipeReadDto();

        recipeReadDto.id(recipe.id);
        recipeReadDto.title(recipe.title);
        recipeReadDto.subtitle(recipe.subtitle);
        recipeReadDto.labels(recipe.getLabels()
                .stream()
                .map(labelEnum -> RecipeReadDto.LabelsEnum.fromValue(labelEnum.getValue())).collect(Collectors.toList()));
        recipeReadDto.duration(recipe.duration);
        recipeReadDto.image(recipe.image);
        recipeReadDto
                .ingredients(recipe
                        .getIngredients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                        .collect(Collectors.toList()));
        recipeReadDto.preparation(recipe.getPreparation());
        recipeReadDto
                .nutrients(recipe
                        .getNutrients()
                        .stream()
                        .map(amount -> new AmountDto()
                                .name(amount.getName())
                                .amount(amount.getAmount())
                                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name())))
                        .collect(Collectors.toList()));
        recipeReadDto.preparation(recipe.getPreparation());
        return recipeReadDto;

    }
}
