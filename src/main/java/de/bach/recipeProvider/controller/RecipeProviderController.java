package de.bach.recipeProvider.controller;

import de.bach.recipeProvider.mongodb.model.Amount;
import de.bach.recipeProvider.mongodb.model.Recipe;
import de.bach.recipeProvider.mongodb.RecipesRepository;
import de.bach.recipeProvider.mongodb.model.UnitEnum;
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

    @Autowired
    RecipesRepository recipesRepository;

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
                                .image(recipe.uri)
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
    public ResponseEntity<RecipeReadDto> getRandomRecipe() {
        List<Recipe> recipes = recipesRepository.findAll();
        RecipeReadDto recipeReadDto = new RecipeReadDto();
        Random r = new Random();
        int randomIndex = r.nextInt(recipes.size());
        Recipe recipe = recipes.get(randomIndex);

        recipeReadDto.id(recipe.id);
        recipeReadDto.title(recipe.title);
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
                                .amount(amount.getAmount()))
                        .collect(Collectors.toList()));
        recipeReadDto.preparation(recipe.getPreparation());
        return ResponseEntity.ok(recipeReadDto);
    }

    @Override
    public ResponseEntity<Void> createRecipe(RecipeWriteDto recipeWriteDto) {
        Recipe recipe = new Recipe(
                recipeWriteDto.getTitle(),
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
        recipesRepository.save(recipe);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAllRecipes() {
        recipesRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
