package de.bach.recipeProvider.mapper;

import de.bach.recipeProvider.mongodb.model.Amount;
import de.bach.recipeProvider.mongodb.model.LabelEnum;
import de.bach.recipeProvider.mongodb.model.Recipe;
import de.bach.recipeProvider.mongodb.model.UnitEnum;
import org.openapitools.model.AmountDto;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeMapper {

    public static RecipeReadDto toRecipeReadDto(Recipe recipe) {
        return new RecipeReadDto()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .subtitle(recipe.getSubtitle())
                .labels(recipe.getLabels().stream()
                        .map(label -> RecipeReadDto.LabelsEnum.fromValue(label.getValue()))
                        .collect(Collectors.toList()))
                .duration(recipe.getDuration())
                .image(recipe.getImage())
                .imageBase64(recipe.getImageBase64())
                .nutrients(recipe.getNutrients().stream()
                        .map(RecipeMapper::toAmountDto)
                        .collect(Collectors.toList()))
                .preparation(recipe.getPreparation())
                .ingredients(recipe.getIngredients().stream()
                        .map(RecipeMapper::toAmountDto)
                        .collect(Collectors.toList()));
    }

    public static AmountDto toAmountDto(Amount amount) {
        return new AmountDto()
                .name(amount.getName())
                .amount(amount.getAmount())
                .unit(AmountDto.UnitEnum.valueOf(amount.getUnitEnum().name()));
    }

    public static Recipe toRecipe(RecipeWriteDto dto) {
        return new Recipe(
                dto.getTitle(),
                dto.getSubtitle(),
                dto.getLabels().stream()
                        .map(label -> LabelEnum.valueOf(label.name()))
                        .collect(Collectors.toList()),
                dto.getDuration(),
                dto.getImage(),
                dto.getImageBase64(),
                dto.getIngredients().stream()
                        .map(RecipeMapper::toAmount)
                        .collect(Collectors.toList()),
                dto.getNutrients().stream()
                        .map(RecipeMapper::toAmount)
                        .collect(Collectors.toList()),
                dto.getPreparation()
        );
    }

    public static Amount toAmount(AmountDto dto) {
        return new Amount(
                dto.getName(),
                dto.getAmount(),
                UnitEnum.valueOf(dto.getUnit().name())
        );
    }
}
