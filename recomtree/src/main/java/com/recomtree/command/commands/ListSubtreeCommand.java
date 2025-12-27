package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to list movies in a specific genre or sub-genre
public class ListSubtreeCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        // Check if genre name is provided
        if (args.length < 2) {
            return "USAGE: LIST_SUBTREE <Genre_or_SubGenre>\n" +
                   "Examples:\n" +
                   "  LIST_SUBTREE action\n" +
                   "  LIST_SUBTREE superhero\n" +
                   "  LIST_SUBTREE pixar";
        }

        // Call service to list the subtree
        return service.listSubtree(args[1]);
    }
}