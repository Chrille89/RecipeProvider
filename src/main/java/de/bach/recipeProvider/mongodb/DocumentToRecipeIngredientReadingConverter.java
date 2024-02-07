package de.bach.recipeProvider.mongodb;

import de.bach.recipeProvider.mongodb.model.RecipeIngredient;
import de.bach.recipeProvider.mongodb.model.RecipeIngredientPersons;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;
import java.util.Map;

@ReadingConverter
public class DocumentToRecipeIngredientReadingConverter implements Converter<Document, RecipeIngredient> {

    @Override
    public RecipeIngredient convert(Document source) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setName(source.getString("name"));
        Map<String, Document> documentMap = source.get("persons",Map.class);

        Map<String, RecipeIngredientPersons> personsMap = new HashMap<>();

        documentMap.keySet().stream().forEach(personCount -> {
            Document document = documentMap.get(personCount);
            RecipeIngredientPersons recipeIngredientPersons = new RecipeIngredientPersons(document.getString("amount"));
            personsMap.put(personCount,recipeIngredientPersons);

        });
        recipeIngredient.setPersons(personsMap);
        return recipeIngredient;
    }
}
