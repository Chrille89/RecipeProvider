package de.bach.recipeProvider.mongodb.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UnitEnum {
    G("g"),

    KG("kg"),

    ST("St"),

    ML("ml"),

    L("l"),

    ESSL_FEL("Esslöfel"),

    TEEL_FEL("Teelöfel"),

    KCAL("kcal");

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