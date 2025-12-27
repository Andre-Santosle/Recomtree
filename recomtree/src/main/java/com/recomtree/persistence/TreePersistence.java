package com.recomtree.persistence;

import com.recomtree.composite.Genre;
import com.recomtree.composite.Movie;
import com.recomtree.composite.CatalogComponent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Class to save and load catalog data using JSON
public class TreePersistence {
    private static final String FILE_PATH = "catalog_data.json";

    //==== SAVE DATA TO JSON FILE ====
    // Save catalog to JSON file
    public static void save(Genre root) {
        try {
            JSONObject json = genreToJson(root);

            // Write JSON to file with pretty print
            FileWriter writer = new FileWriter(FILE_PATH);
            writer.write(json.toString(2)); // 2 spaces indentation
            writer.close();

            System.out.println("System state saved to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Failed to save state: " + e.getMessage());
        }
    }

    // Convert Genre to JSON object
    private static JSONObject genreToJson(Genre genre) {
        JSONObject json = new JSONObject();
        json.put("type", "genre");
        json.put("name", genre.getName());

        JSONArray childrenArray = new JSONArray();
        List<CatalogComponent> children = genre.getChildren();

        // Recursively convert children
        for (int i = 0; i < children.size(); i++) {
            CatalogComponent child = children.get(i);

            if (child instanceof Genre) {
                childrenArray.put(genreToJson((Genre) child));
            } else if (child instanceof Movie) {
                childrenArray.put(movieToJson((Movie) child));
            }
        }

        json.put("children", childrenArray);
        return json;
    }

    // Convert Movie to JSON object
    private static JSONObject movieToJson(Movie movie) {
        JSONObject json = new JSONObject();
        json.put("type", "movie");
        json.put("name", movie.getName());
        json.put("rating", movie.getRating());
        json.put("ratingCount", movie.getRatingCount());
        json.put("totalRatingSum", movie.getTotalRatingSum());
        return json;
    }

    //==== LOAD DATA FROM JSON FILE ====
    // Load catalog from JSON file
    public static Genre load() {
        File file = new File(FILE_PATH);

        // If file doesn't exist, return new root
        if (!file.exists()) {
            return new Genre("Movies Catalog");
        }

        try {
            // Read file content
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            JSONObject json = new JSONObject(content);

            // Convert JSON back to Genre tree
            return jsonToGenre(json);

        } catch (Exception e) {
            System.err.println("Failed to load state, starting fresh: " + e.getMessage());
            return new Genre("Movies Catalog");
        }
    }

    // Convert JSON object to Genre
    private static Genre jsonToGenre(JSONObject json) {
        String name = json.getString("name");
        Genre genre = new Genre(name);

        if (json.has("children")) {
            JSONArray childrenArray = json.getJSONArray("children");

            for (int i = 0; i < childrenArray.length(); i++) {
                JSONObject childJson = childrenArray.getJSONObject(i);
                String type = childJson.getString("type");

                if ("genre".equals(type)) {
                    genre.add(jsonToGenre(childJson));
                } else if ("movie".equals(type)) {
                    genre.add(jsonToMovie(childJson));
                }
            }
        }

        return genre;
    }

    // Convert JSON object to Movie
    private static Movie jsonToMovie(JSONObject json) {
        String name = json.getString("name");
        double rating = json.getDouble("rating");

        Movie movie = new Movie(name, rating);

        // Load ratingCount and totalRatingSum if present (for backward compatibility)
        if (json.has("ratingCount")) {
            movie.setRatingCount(json.getInt("ratingCount"));
        }
        if (json.has("totalRatingSum")) {
            movie.setTotalRatingSum(json.getDouble("totalRatingSum"));
        }

        return movie;
    }
}

