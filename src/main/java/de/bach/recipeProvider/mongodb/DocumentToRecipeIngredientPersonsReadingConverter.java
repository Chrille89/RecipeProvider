package de.bach.recipeProvider.mongodb;

import de.bach.recipeProvider.mongodb.model.RecipeIngredientPersons;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class DocumentToRecipeIngredientPersonsReadingConverter implements Converter<Document, RecipeIngredientPersons> {

    @Override
    public RecipeIngredientPersons convert(Document source) {
        return null;
    }

}
