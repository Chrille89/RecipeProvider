package de.bach.recipeProvider.mongodb;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Recipe {

    @Id
    public String id;

    public String title;
    public List<String> ingredients;

    public List<String> preparation;

    public Recipe() {}

    public Recipe(String title, List<String> ingredients, List<String> preparation) {
        this.title = title;
        this.ingredients = ingredients;
        this.preparation = preparation;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
