package de.bre.mits.mitsapp.ingredientsList;

public class CardModel {
    private String id;
    private String title;
    private String critAmount;
    private String ingAmount;
    private String quantityUnit;


    public CardModel(String id, String title, String critAmount, String ingAmount, String quantityUnit) {
        this.id = id;
        this.title = title;
        this.critAmount = critAmount;
        this.ingAmount = ingAmount;
        this.quantityUnit = quantityUnit;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCritAmount() {
        return critAmount;
    }

    public String getIngAmount() {
        return ingAmount;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }
}

