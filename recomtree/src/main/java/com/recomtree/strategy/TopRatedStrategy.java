package com.recomtree.strategy;

import com.recomtree.composite.*;
import java.util.*;

// Strategy to recommend top rated movies
public class TopRatedStrategy implements RecommendationStrategy {

    @Override
    public List<Movie> recommend(CatalogComponent root, String parameter) {
        List<Movie> allMovies = new ArrayList<>();
        collectMovies(root, allMovies);

        // Sort movies by rating (highest first) using bubble sort
        for (int i = 0; i < allMovies.size() - 1; i++) {
            for (int j = 0; j < allMovies.size() - i - 1; j++) {
                if (allMovies.get(j).getRating() < allMovies.get(j + 1).getRating()) {
                    // Swap
                    Movie temp = allMovies.get(j);
                    allMovies.set(j, allMovies.get(j + 1));
                    allMovies.set(j + 1, temp);
                }
            }
        }

        // Return top 5 movies
        List<Movie> topMovies = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < allMovies.size() && count < 5; i++) {
            topMovies.add(allMovies.get(i));
            count++;
        }

        return topMovies;
    }

    // Helper method to collect all movies
    private void collectMovies(CatalogComponent node, List<Movie> collector) {
        if (node instanceof Movie) {
            collector.add((Movie) node);
        } else if (node instanceof Genre) {
            List<CatalogComponent> children = ((Genre) node).getChildren();
            for (int i = 0; i < children.size(); i++) {
                collectMovies(children.get(i), collector);
            }
        }
    }
}