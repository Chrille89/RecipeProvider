package de.bach.recipeProvider.mongodb.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.model.AmountDto;

public enum UnitEnum {
    GRAMM("gramm"),

    KILOGRAMM("kilogramm"),

    STUECK("stueck"),

    MILLILITER("milliliter"),

    LITER("liter");

    private String value;

    UnitEnum(String value) {
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

