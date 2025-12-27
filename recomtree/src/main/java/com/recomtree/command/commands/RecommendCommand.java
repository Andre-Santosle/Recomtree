package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to get movie recommendations
public class RecommendCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        // Check if strategy is provided
        if (args.length < 2) {
            return "USAGE: RECOMMEND <TOP_RATED|GENRE_SIMILAR> [GenreName]";
        }

        // Get the strategy type
        String strategy = args[1].toUpperCase();

        // Get optional parameter (genre name)
        String param = "";
        if (args.length > 2) {
            param = args[2];
        }

        // Call service to get recommendations
        return service.recommend(strategy, param);
    }
}