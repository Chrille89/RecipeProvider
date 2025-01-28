package de.bach.recipeProvider.mongodb.model;

public class Amount {

    private String name;

    private Integer amount;

    private UnitEnum unitEnum;

    public Amount() {}

    public Amount(String name, Integer amount, UnitEnum unitEnum) {
        this.name = name;
        this.amount = amount;
        this.unitEnum = unitEnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public UnitEnum getUnitEnum() {
        return unitEnum;
    }

    public void setUnitEnum(UnitEnum unitEnum) {
        this.unitEnum = unitEnum;
    }
}
