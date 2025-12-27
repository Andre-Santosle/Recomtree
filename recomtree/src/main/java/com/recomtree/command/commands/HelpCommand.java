package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to display help information
public class HelpCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        StringBuilder help = new StringBuilder();

        help.append("\n====================================================================\n");
        help.append("                     AVAILABLE COMMANDS                             \n");
        help.append("====================================================================\n\n");

        // General commands
        help.append("GENERAL COMMANDS:\n");
        help.append("  HELP                            - Display this help\n");
        help.append("  LIST_ALL                        - List all movies in the catalog\n");
        help.append("  LIST_SUBTREE <Genre/SubGenre>   - List movies from a genre or sub-genre\n");
        help.append("                                    Examples: LIST_SUBTREE action\n");
        help.append("                                              LIST_SUBTREE superhero\n");
        help.append("  RECOMMEND TOP_RATED             - Recommend top rated movies\n");
        help.append("  RECOMMEND GENRE_SIMILAR <Genre> - Recommend movies from same genre\n");
        help.append("                                    (includes all sub-genres)\n\n");

        // Admin only commands
        if ("ADMIN".equals(role)) {
            help.append("ADMIN COMMANDS:\n");
            help.append("  ADD_MOVIE <Genre/Path> <Title>\n");
            help.append("                                  - Add a movie to the catalog\n");
            help.append("                                    Use underscores for spaces in title\n");
            help.append("                                    Use slash for sub-genre paths\n");
            help.append("                                    Movies are added without rating\n");
            help.append("                                    Examples:\n");
            help.append("                                      ADD_MOVIE action The_Raid\n");
            help.append("                                      ADD_MOVIE action/superhero Deadpool\n");
            help.append("                                      ADD_MOVIE sci-fi/space Apollo_13\n\n");
        }

        // User only commands
        if ("USER".equals(role)) {
            help.append("USER COMMANDS:\n");
            help.append("  RATE_MOVIE <Movie_Title> <Rating>\n");
            help.append("                                  - Rate a movie (0.0 to 10.0)\n");
            help.append("                                    Use underscores for spaces in title\n");
            help.append("                                    Examples:\n");
            help.append("                                      RATE_MOVIE Matrix 8.5\n");
            help.append("                                      RATE_MOVIE The_Dark_Knight 9.0\n\n");
        }

        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            help.append("Log in as ADMIN or USER to see more commands!\n\n");
        }
        help.append("====================================================================\n");

        return help.toString();
    }
}

