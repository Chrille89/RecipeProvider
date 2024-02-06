package de.bach.recipeProvider.mongodb.model;

import org.openapitools.model.RecipeWriteDtoIngredientsInnerPersonsValue;

import java.util.HashMap;
import java.util.Map;

public class RecipeIngredient {

    private String name;
    private Map<String, RecipeIngredientPersons> persons = new HashMap<>();

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, Map<String, RecipeIngredientPersons> persons) {
        this.name = name;
        this.persons = persons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, RecipeIngredientPersons> getPersons() {
        return persons;
    }
}
