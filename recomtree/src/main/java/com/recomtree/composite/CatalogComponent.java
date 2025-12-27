package com.recomtree.composite;

// Abstract base class for catalog items (movies and genres)
public abstract class CatalogComponent {
    protected String name;

    // Default constructor
    public CatalogComponent() {}

    // Constructor with name
    public CatalogComponent(String name) {
        this.name = name;
    }

    // Get the name
    public String getName() {
        return name;
    }

    // Abstract method to display the component
    public abstract void display(StringBuilder sb, int depth);

    // Abstract method to get rating
    public abstract double getRating();
}