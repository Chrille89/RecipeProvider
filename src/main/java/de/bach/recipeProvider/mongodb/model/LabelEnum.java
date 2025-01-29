package de.bach.recipeProvider.mongodb.model;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets label
 */
public enum LabelEnum {
    LOWCARB("lowcarb"),

    LOWFAT("lowfat"),

    VEGETARIAN("vegetarian"),

    PIG("pig"),

    BEEF("beef"),

    FISH("fish");

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
