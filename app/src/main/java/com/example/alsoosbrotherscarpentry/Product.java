package com.example.alsoosbrotherscarpentry;

public class Product {
    private String name;
    private float oldPrice;
    private float newPrice;
    private int quantity;
    private String description;
    private int imageResId;

    // Constructor
    public Product(String name, float oldPrice, float newPrice, int quantity, String description, int imageResId) {
        this.name = name;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.description = description;
        this.imageResId = imageResId;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public float getNewPrice() {
        return newPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
