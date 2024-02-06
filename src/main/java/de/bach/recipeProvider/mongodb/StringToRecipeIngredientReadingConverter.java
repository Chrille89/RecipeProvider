package de.bach.recipeProvider.mongodb;

import de.bach.recipeProvider.mongodb.model.RecipeIngredient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToRecipeIngredientReadingConverter implements Converter<String, RecipeIngredient> {


    @Override
    public RecipeIngredient convert(String source) {
        return null;
    }
}
