package de.bach.recipeProvider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecipeProviderController {

	@GetMapping(path = "/test", produces = "application/json")
	public String ping() {
		return "Hello World";
	}
}
