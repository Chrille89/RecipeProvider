package de.bach.recipeProvider.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.util.List;

@Document(collection = "Recipes")
public class Recipe {

    @Id
    public String id;

    public String title;

    public URI uri;

    private List<RecipeIngredient> ingredients;

    private List<String> nutrients;

    private List<String> preparation;

    public Recipe() {}

    public Recipe(String title, URI uri, List<RecipeIngredient> ingredients, List<String> nutrients, List<String> preparation) {
        this.title = title;
        this.uri = uri;
        this.ingredients = ingredients;
        this.nutrients = nutrients;
        this.preparation = preparation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<String> nutrients) {
        this.nutrients = nutrients;
    }

    public List<String> getPreparation() {
        return preparation;
    }

    public void setPreparation(List<String> preparation) {
        this.preparation = preparation;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
