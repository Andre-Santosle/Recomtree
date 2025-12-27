package com.recomtree.composite;

import java.util.*;

// Class representing a genre that can contain movies and sub-genres
public class Genre extends CatalogComponent {
    private List<CatalogComponent> children; // List to store movies and sub-genres

    // Constructor with name
    public Genre(String name) {
        super(name);
        children = new ArrayList<>();
    }

    // Add a movie or sub-genre to this genre
    public void add(CatalogComponent component) {
        children.add(component);
    }

    // Get all children (movies and sub-genres)
    public List<CatalogComponent> getChildren() {
        return children;
    }

    // Setter for children (needed for loading data)
    public void setChildren(List<CatalogComponent> children) {
        this.children = children;
    }

    // Find a genre by name (recursive search)
    public Genre findGenre(String genreName) {
        // Check if this is the genre we're looking for
        if (this.name != null && this.name.equalsIgnoreCase(genreName)) {
            return this;
        }

        // Search in children
        for (int i = 0; i < children.size(); i++) {
            CatalogComponent child = children.get(i);

            // Only search in sub-genres
            if (child instanceof Genre) {
                Genre found = ((Genre) child).findGenre(genreName);

                if (found != null) {
                    return found;
                }
            }
        }

        // Not found
        return null;
    }

    // Display the genre tree
    @Override
    public void display(StringBuilder sb, int depth) {
        // Skip root genre display
        if (depth > 0) {
            // Add indentation
            for (int i = 0; i < depth; i++) {
                sb.append("   ");
            }

            // Add "- " prefix for sub-genres
            sb.append("- ");
            sb.append(name);
            sb.append("\n");
        }

        // Display all children
        for (int i = 0; i < children.size(); i++) {
            children.get(i).display(sb, depth + 1);
        }
    }

    // Genres don't have ratings
    @Override
    public double getRating() {
        return 0.0;
    }
}