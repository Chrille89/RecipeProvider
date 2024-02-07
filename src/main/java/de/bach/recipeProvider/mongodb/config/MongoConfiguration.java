package de.bach.recipeProvider.mongodb.config;

import de.bach.recipeProvider.mongodb.DocumentToRecipeIngredientPersonsReadingConverter;
import de.bach.recipeProvider.mongodb.DocumentToRecipeIngredientReadingConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        new DocumentToRecipeIngredientReadingConverter(),
                        new DocumentToRecipeIngredientPersonsReadingConverter()));
    }
}
