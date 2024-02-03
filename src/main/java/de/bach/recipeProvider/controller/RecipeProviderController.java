package de.bach.recipeProvider.controller;

import org.openapitools.api.RecipesApi;
import org.openapitools.model.RecipeDtoInner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class RecipeProviderController implements RecipesApi {

	@GetMapping(path = "/test", produces = "application/json")
	public String ping() {
		return "Hello World";
	}

	@Override
	public ResponseEntity<List<RecipeDtoInner>> getRecipes() {
		List<RecipeDtoInner> recipes = new ArrayList<>();

		RecipeDtoInner recipesDtoInner = new RecipeDtoInner();
		recipesDtoInner.id(UUID.randomUUID());
		recipesDtoInner.title("Bratkartoffeln");
		recipesDtoInner.addIngredientsItem("500g Kartoffeln").addIngredientsItem("100g Zwiebeln");
		recipesDtoInner.addPreparationItem("Kartoffeln schälen").addPreparationItem("Zwiebeln abziehen").addPreparationItem("Kartoffeln und Zwiebeln braten");
		recipes.add(recipesDtoInner);
		return ResponseEntity.ok(recipes);
	}

}
