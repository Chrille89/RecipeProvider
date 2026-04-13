package de.bach.recipeProvider.mongodb.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.model.RecipeReadDto;
import org.openapitools.model.RecipeWriteDto;

public enum LabelEnum {
    KALORIENARM("Kalorienarm"),

    FETTARM("Fettarm"),

    EIWEI_("Eiweiß"),

    VEGETARISCH("Vegetarisch"),

    VEGAN("Vegan"),

    SCHWEIN("Schwein"),

    RIND("Rind"),

    GEFL_GEL("Geflügel"),

    FISCH("Fisch"),

    THERMOMIX("Thermomix"),

    AIRFRYER("Airfryer"),

    OFEN("Ofen"),

    R_MERTOPF("Römertopf"),

    EXPRESS("Express"),

    KIDS("Kids");

    private String value;

    LabelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}