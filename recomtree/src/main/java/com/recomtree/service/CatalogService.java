package com.recomtree.service;

import com.recomtree.composite.CatalogComponent;
import com.recomtree.composite.Genre;
import com.recomtree.composite.Movie;
import com.recomtree.strategy.GenreSimilarStrategy;
import com.recomtree.strategy.RecommendationStrategy;
import com.recomtree.strategy.TopRatedStrategy;

import java.util.ArrayList;
import java.util.List;

// Service class to manage the catalog operations
public class CatalogService {
    private Genre root;

    public CatalogService(Genre root) {
        this.root = root;
    }

    // Add a movie to a genre without initial rating (supports hierarchical paths like "action/superhero")
    public String addMovie(String genrePath, String title) {
        // Handle hierarchical genre paths (e.g., "action/superhero")
        String[] pathParts = genrePath.split("/");
        Genre targetGenre = root;

        // Navigate or create the genre hierarchy
        for (int i = 0; i < pathParts.length; i++) {
            String genreName = pathParts[i].trim();

            if (genreName.isEmpty()) {
                continue;
            }

            Genre nextGenre = targetGenre.findGenre(genreName);

            // If the genre doesn't exist at this level, create it
            if (nextGenre == null) {
                nextGenre = new Genre(genreName);
                targetGenre.add(nextGenre);
            }

            targetGenre = nextGenre;
        }

        // Add movie to the final target genre without rating
        Movie newMovie = new Movie(title);
        targetGenre.add(newMovie);

        String fullPath = genrePath.replace("/", " > ");
        return "SUCCESS: Added movie '" + title + "' to " + fullPath + " (not rated yet)";
    }

    // Old method kept for backward compatibility
    public String addMovie(String genrePath, String title, double rating) {
        // Handle hierarchical genre paths (e.g., "action/superhero")
        String[] pathParts = genrePath.split("/");
        Genre targetGenre = root;

        // Navigate or create the genre hierarchy
        for (int i = 0; i < pathParts.length; i++) {
            String genreName = pathParts[i].trim();

            if (genreName.isEmpty()) {
                continue;
            }

            Genre nextGenre = targetGenre.findGenre(genreName);

            // If the genre doesn't exist at this level, create it
            if (nextGenre == null) {
                nextGenre = new Genre(genreName);
                targetGenre.add(nextGenre);
            }

            targetGenre = nextGenre;
        }

        // Add movie to the final target genre
        Movie newMovie = new Movie(title, rating);
        targetGenre.add(newMovie);

        String fullPath = genrePath.replace("/", " > ");
        return "SUCCESS: Added movie '" + title + "' to " + fullPath + " (rating: " + rating + ")";
    }

    // Rate a movie (user action)
    public String rateMovie(String title, double rating) {
        // Find the movie in the catalog
        List<Movie> allMovies = new ArrayList<>();
        collectMoviesRecursively(root, allMovies);

        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (movie.getName().equalsIgnoreCase(title)) {
                movie.addRating(rating);
                int count = movie.getRatingCount();
                double avgRating = movie.getRating();
                return "SUCCESS: Your rating of " + rating + " has been recorded for '" + title + "'.\n" +
                       "New average: " + String.format("%.1f", avgRating) + " (" + count + " rating" +
                       (count > 1 ? "s" : "") + ")";
            }
        }

        return "ERROR: Movie '" + title + "' not found in catalog.";
    }

    // List all movies and genres in a specific genre
    public String listSubtree(String genreName) {
        Genre genre = root.findGenre(genreName);

        if (genre == null) {
            return "ERROR: Genre '" + genreName + "' not found.";
        }

        StringBuilder sb = new StringBuilder();
        genre.display(sb, 0);
        return sb.toString();
    }

    // List all movies in the catalog
    public String listAll() {
        StringBuilder sb = new StringBuilder();
        root.display(sb, 0);

        if (sb.length() == 0) {
            return "Catalog is empty.";
        }

        return sb.toString();
    }

    // Get recommendations based on strategy
    public String recommend(String strategyType, String param) {
        RecommendationStrategy strategy;

        // Choose the right strategy
        if ("TOP_RATED".equals(strategyType)) {
            strategy = new TopRatedStrategy();
        } else if ("GENRE_SIMILAR".equals(strategyType)) {
            strategy = new GenreSimilarStrategy();
        } else {
            return "ERROR: Unknown strategy";
        }

        // Get recommendations
        List<Movie> results = strategy.recommend(root, param);

        if (results.isEmpty()){
            return "No recommendations found.";
        }

        StringBuilder sb = new StringBuilder("RECOMMENDATIONS:\n");
        for (int i = 0; i < results.size(); i++) {
            Movie m = results.get(i);
            sb.append("- ");
            sb.append(m.getName());
            if (m.getRatingCount() > 0) {
                sb.append(" (");
                sb.append(String.format("%.1f", m.getRating()));
                sb.append(" - ");
                sb.append(m.getRatingCount());
                sb.append(" rating");
                if (m.getRatingCount() > 1) {
                    sb.append("s");
                }
                sb.append(")");
            } else {
                sb.append(" (Not rated yet)");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Helper method to collect all movies recursively
    private void collectMoviesRecursively(CatalogComponent node, List<Movie> accumulator) {
        if (node instanceof Movie){
            accumulator.add((Movie) node);
        }
        else if (node instanceof Genre) {
            List<CatalogComponent> children = ((Genre) node).getChildren();

            for (int i = 0; i < children.size(); i++) {
                collectMoviesRecursively(children.get(i), accumulator);
            }
        }
    }

    // Helper class to store a movie with its category path
    private static class MovieWithPath {
        Movie movie;
        List<String> path;

        MovieWithPath(Movie movie, List<String> path) {
            this.movie = movie;
            this.path = new ArrayList<>(path);
        }
    }

    // Helper method to collect movies with their category path
    private void collectMoviesWithPath(CatalogComponent node, List<String> currentPath, List<MovieWithPath> accumulator) {
        if (node instanceof Movie) {
            accumulator.add(new MovieWithPath((Movie) node, currentPath));
        } else if (node instanceof Genre) {
            Genre genre = (Genre) node;
            List<CatalogComponent> children = genre.getChildren();

            // Add current genre to path (skip root which has null or empty name)
            List<String> newPath = new ArrayList<>(currentPath);
            if (genre.getName() != null && !genre.getName().isEmpty() && !genre.getName().equals("Root")) {
                newPath.add(genre.getName());
            }

            for (int i = 0; i < children.size(); i++) {
                collectMoviesWithPath(children.get(i), newPath, accumulator);
            }
        }
    }
}