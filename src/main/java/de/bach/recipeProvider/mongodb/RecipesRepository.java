package de.bach.recipeProvider.mongodb;

import de.bach.recipeProvider.mongodb.model.Recipe;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipesRepository extends MongoRepository<Recipe, ObjectId> {

}
