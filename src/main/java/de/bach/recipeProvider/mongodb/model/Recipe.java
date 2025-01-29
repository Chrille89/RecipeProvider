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

    public List<LabelEnum> labels;

    public Integer duration;

    public URI uri;

    private List<Amount> ingredients;

    private List<Amount> nutrients;

    private List<String> preparation;

    public Recipe() {}

    public Recipe(String title, List<LabelEnum> labels, Integer duration, URI uri, List<Amount> ingredients, List<Amount> nutrients, List<String> preparation) {
        this.title = title;
        this.labels = labels;
        this.duration = duration;
        this.uri = uri;
        this.ingredients = ingredients;
        this.nutrients = nutrients;
        this.preparation = preparation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<LabelEnum> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelEnum> labels) {
        this.labels = labels;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public List<Amount> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Amount> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Amount> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Amount> nutrients) {
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
