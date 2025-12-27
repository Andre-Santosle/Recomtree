package com.recomtree.strategy;

import com.recomtree.composite.CatalogComponent;
import com.recomtree.composite.Genre;
import com.recomtree.composite.Movie;
import java.util.ArrayList;
import java.util.List;

// Strategy to recommend movies from the same genre
public class GenreSimilarStrategy implements RecommendationStrategy {

    @Override
    public List<Movie> recommend(CatalogComponent root, String genreName) {
        List<Movie> results = new ArrayList<>();

        // Check if root is a genre
        if (!(root instanceof Genre)) {
            return results;
        }

        // Find the target genre
        Genre targetGenre = ((Genre) root).findGenre(genreName);

        if (targetGenre == null) {
            return results;
        }

        // Collect all movies from this genre
        collectMovies(targetGenre, results);
        return results;
    }

    // Helper method to collect movies
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