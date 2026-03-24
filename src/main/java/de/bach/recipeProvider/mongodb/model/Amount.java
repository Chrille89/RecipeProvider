package de.bach.recipeProvider.mongodb.model;

import java.math.BigDecimal;

public class Amount {

    private String name;

    private BigDecimal amount;

    private UnitEnum unitEnum;

    public Amount() {}

    public Amount(String name, BigDecimal amount, UnitEnum unitEnum) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UnitEnum getUnitEnum() {
        return unitEnum;
    }

    public void setUnitEnum(UnitEnum unitEnum) {
        this.unitEnum = unitEnum;
    }
}
