package de.bach.recipeProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
@RestController
public class RecipeProviderApplication  {


	public static void main(String[] args) {
		SpringApplication.run(RecipeProviderApplication.class, args);
	}





}
