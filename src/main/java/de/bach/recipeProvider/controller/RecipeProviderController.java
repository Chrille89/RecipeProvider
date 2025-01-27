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
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RecipeProviderController implements RecipesApi {

	private static long time;
	private static RecipeReadDto recipeReadDtoCache;

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
		Integer finalPersons = 2;
		List<Recipe> recipes = recipesRepository.findAll();
		List<RecipeReadDto> recipeReadDtos = recipes.stream().map(recipe ->
				new RecipeReadDto()
						.id(recipe.id)
						.title(recipe.title)
						.image(recipe.uri)
						.nutrients(recipe.getNutrients())
						.preparation(recipe.getPreparation())
						.ingredients(recipe.getIngredients()
								.stream()
								.map(ingredient -> {
									Map<String, RecipeIngredientPersons> map = ingredient.getPersons();
									RecipeIngredientPersons recipeIngredientPersons =map.get(finalPersons.toString());
									return recipeIngredientPersons.getAmount()+" "+ingredient.getName();
								}).collect(Collectors.toList()))).collect(Collectors.toList());

		return ResponseEntity.ok(recipeReadDtos);

	}

	@Override
	public ResponseEntity<RecipeReadDto> getRandomRecipe(Integer persons) {
		System.out.println("Get data from database ...");
		time = System.currentTimeMillis();
		if(persons == null) persons = 2;
		if(persons < 2) persons = 2;
		if(persons > 4) persons = 4;
		List<Recipe> recipes = recipesRepository.findAll();
		RecipeReadDto recipeReadDto = new RecipeReadDto();
		Random r = new Random();
		int randomIndex = r.nextInt(recipes.size());
		Recipe recipe = recipes.get(randomIndex);
		List<String> ingredients = new ArrayList<>();
		Integer finalPersons = persons;
		recipe.getIngredients().forEach(recipeIngredient -> {
					Map<String, RecipeIngredientPersons> map = recipeIngredient.getPersons();
					RecipeIngredientPersons recipeIngredientPersons =map.get(finalPersons.toString());
					ingredients.add(recipeIngredientPersons.getAmount()+" "+recipeIngredient.getName());
				}
		);
		recipeReadDto.id(recipe.id);
		recipeReadDto.title(recipe.title);
		recipeReadDto.ingredients(ingredients);
		recipeReadDto.preparation(recipe.getPreparation());
		recipeReadDtoCache = recipeReadDto;
		return ResponseEntity.ok(recipeReadDto);
	}

	@Override
	public ResponseEntity<Void> createRecipe(RecipeWriteDto recipeWriteDto) {
		List<RecipeIngredient> recipeIngredients = new ArrayList<>();
		recipeWriteDto.getIngredients().forEach(ingredient -> {
			Map<String, RecipeWriteDtoIngredientsInnerPersonsValue> map = ingredient.getPersons();
			Map<String, RecipeIngredientPersons> mapMongo = new HashMap<>();
			map.keySet().forEach(key -> {
				RecipeWriteDtoIngredientsInnerPersonsValue recipeWriteDtoIngredientsInnerPersonsValue = map.get(key);
				RecipeIngredientPersons recipeIngredientsPersons = new RecipeIngredientPersons(recipeWriteDtoIngredientsInnerPersonsValue.getAmount());
				mapMongo.put(key,recipeIngredientsPersons);
			});
			recipeIngredients.add(new RecipeIngredient(ingredient.getName(),mapMongo));
		});

		Recipe recipe = new Recipe(
				recipeWriteDto.getTitle(),
				recipeWriteDto.getImage(),
				recipeIngredients,
				recipeWriteDto.getNutrients(),
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
