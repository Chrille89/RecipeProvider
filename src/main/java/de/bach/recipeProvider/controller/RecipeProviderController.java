package de.bach.recipeProvider.controller;

import de.bach.recipeProvider.mongodb.model.Recipe;
import de.bach.recipeProvider.mongodb.RecipesRepository;
import de.bach.recipeProvider.mongodb.model.RecipeIngredient;
import de.bach.recipeProvider.mongodb.model.RecipeIngredientPersons;
import org.openapitools.api.RecipesApi;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;
import org.openapitools.model.RecipeWriteDtoIngredientsInnerPersonsValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RecipeProviderController implements RecipesApi {

	@Autowired
	RecipesRepository recipesRepository;

	@GetMapping(path = "/test", produces = "application/json")
	public String ping() {
		return "Hello World";
	}

	@Override
	public ResponseEntity<RecipeReadDto> getRandomRecipe() {
		List<Recipe> recipes = recipesRepository.findAll();
		RecipeReadDto recipeReadDto = new RecipeReadDto();
		Recipe recipe = recipes.get(recipes.size() -1);
		List<String> ingredients = new ArrayList<>();

		recipe.getIngredients().stream().forEach(recipeIngredient -> {
			Map<String, RecipeIngredientPersons> map = recipeIngredient.getPersons();
			RecipeIngredientPersons recipeIngredientPersons =map.get(map.keySet().stream().iterator().next());
					ingredients.add(recipeIngredientPersons.getAmount()+" "+recipeIngredient.getName());
		}
		);
		recipeReadDto.id(UUID.randomUUID());
		recipeReadDto.title(recipe.title);
		recipeReadDto.ingredients(ingredients);
		recipeReadDto.preparation(recipe.getPreparation());
		return ResponseEntity.ok(recipeReadDto);
	}

	@Override
	public ResponseEntity<Void> createRecipe(RecipeWriteDto recipeWriteDto) {
		List<RecipeIngredient> recipeIngredients = new ArrayList<>();
		recipeWriteDto.getIngredients().stream().forEach(ingredient -> {
			Map<String, RecipeWriteDtoIngredientsInnerPersonsValue> map = ingredient.getPersons();
			Map<String, RecipeIngredientPersons> mapMongo = new HashMap<>();
			map.keySet().stream().forEach(key -> {
				RecipeWriteDtoIngredientsInnerPersonsValue recipeWriteDtoIngredientsInnerPersonsValue = map.get(key);
				RecipeIngredientPersons recipeIngredientsPersons = new RecipeIngredientPersons(recipeWriteDtoIngredientsInnerPersonsValue.getAmount());
				mapMongo.put(key,recipeIngredientsPersons);
			});
			recipeIngredients.add(new RecipeIngredient(ingredient.getName(),mapMongo));
		});
		Recipe recipe = new Recipe(recipeWriteDto.getTitle(),recipeIngredients,recipeWriteDto.getPreparation());
		recipesRepository.save(recipe);
		return RecipesApi.super.createRecipe(recipeWriteDto);
	}
}
