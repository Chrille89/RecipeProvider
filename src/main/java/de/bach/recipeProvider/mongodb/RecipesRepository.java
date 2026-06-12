package de.bach.recipeProvider.mongodb;

import de.bach.recipeProvider.mongodb.model.Recipe;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipesRepository extends MongoRepository<Recipe, ObjectId> {

    // liefert alle Rezepte ohne imageBase64-Feld
    @Query(value = "{}", fields = "{ 'imageBase64' : 0 }")
    List<Recipe> findAllWithoutImageBase64();

    // liefert ein Rezept ohne imageBase64 (Suche nach _id)
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'imageBase64' : 0 }")
    Optional<Recipe> findByIdWithoutImageBase64(ObjectId id);
}
