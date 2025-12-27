package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

// Command to list all movies in the catalog
public class ListAllCommand implements Command {

    @Override
    public String execute(CatalogService service, String[] args, String role) {
        // Just call the service method to list all movies
        return service.listAll();
    }
}