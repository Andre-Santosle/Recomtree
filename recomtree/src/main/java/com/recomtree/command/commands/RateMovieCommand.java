package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to rate a movie - only accessible to users
public class RateMovieCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        // Only users can rate movies
        if (!"USER".equals(role)) {
            return "ERROR: Access Denied. Only users can rate movies.";
        }

        // Check if we have enough arguments
        if (args.length < 3) {
            return "USAGE: RATE_MOVIE <Movie_Title> <Rating>\n" +
                   "Examples:\n" +
                   "  RATE_MOVIE Matrix 8.5\n" +
                   "  RATE_MOVIE The_Shawshank_Redemption 9.3\n" +
                   "Note: Use underscores for spaces in movie title\n" +
                   "      Rating must be between 0.0 and 10.0";
        }

        try {
            // Parse the rating
            double rating = Double.parseDouble(args[2]);

            // Validate rating range
            if (rating < 0.0 || rating > 10.0) {
                return "ERROR: Rating must be between 0.0 and 10.0";
            }

            // Replace underscores with spaces in title
            String title = args[1].replace("_", " ");

            // Call service to rate movie
            return service.rateMovie(title, rating);
        } catch (NumberFormatException e) {
            return "ERROR: Invalid rating format. Please use a number (e.g., 8.5)";
        }
    }
}

