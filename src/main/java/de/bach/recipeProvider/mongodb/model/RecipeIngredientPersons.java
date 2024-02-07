package de.bach.recipeProvider.mongodb.model;

public class RecipeIngredientPersons {

    private String amount;

    public RecipeIngredientPersons() {
    }
    public RecipeIngredientPersons(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}
