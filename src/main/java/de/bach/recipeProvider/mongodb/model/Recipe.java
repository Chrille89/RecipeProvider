package de.bach.recipeProvider.mongodb.model;

import jakarta.validation.Valid;
import org.openapitools.model.RecipeWriteDtoIngredientsInner;
import org.springframework.data.annotation.Id;

import java.util.List;


public class Recipe {

    @Id
    public String id;

    public String title;

    private List<RecipeIngredient> ingredients;

    private List<String> preparation;

    public Recipe() {}

    public Recipe(String title, List<RecipeIngredient> ingredients, List<String> preparation) {
        this.title = title;
        this.ingredients = ingredients;
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
