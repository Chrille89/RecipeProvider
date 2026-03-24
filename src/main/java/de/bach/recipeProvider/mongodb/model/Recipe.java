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

    public String subtitle;

    public List<LabelEnum> labels;

    public Integer duration;

    public String image;

    public String imageBase64;

    private List<Amount> ingredients;

    private List<Amount> nutrients;

    private List<String> preparation;

    public Recipe() {}

    public Recipe(String title, String subtitle, List<LabelEnum> labels, Integer duration, String image, String imageBase64,List<Amount> ingredients, List<Amount> nutrients, List<String> preparation) {
        this.title = title;
        this.subtitle = subtitle;
        this.labels = labels;
        this.duration = duration;
        this.image = image;
        this.imageBase64 = imageBase64;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageBase64() {
        return imageBase64;
    }
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
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
