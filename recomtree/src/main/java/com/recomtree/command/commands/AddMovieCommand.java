package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to add a new movie to the catalog
public class AddMovieCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        // Only admins can add movies
        if (!"ADMIN".equals(role)) {
            return "ERROR: Access Denied. Admins only.";
        }

        // Check if we have enough arguments
        if (args.length < 3) {
            return "USAGE: ADD_MOVIE <Genre_or_Path> <Title_Name>\n" +
                   "Examples:\n" +
                   "  ADD_MOVIE action Matrix_4\n" +
                   "  ADD_MOVIE action/superhero Batman_Returns\n" +
                   "  ADD_MOVIE sci-fi/space Gravity\n" +
                   "Note: Movies are added without rating. Users will rate them.";
        }

        // Replace underscores with spaces in title
        String title = args[2].replace("_", " ");

        // Genre path can contain slashes for sub-genres (e.g., "action/superhero")
        String genrePath = args[1];

        // Call service to add movie (without rating)
        return service.addMovie(genrePath, title);
    }
}