package com.recomtree.command;

import com.recomtree.command.commands.*;
import com.recomtree.service.CatalogService;
import java.util.HashMap;
import java.util.Map;

// Class to manage and execute commands
public class CommandInvoker {
    private Map<String, Command> registry; // Map to store all available commands
    private CatalogService service;

    // Constructor to initialize the command registry
    public CommandInvoker(CatalogService service) {
        this.service = service;
        this.registry = new HashMap<>();

        // Register all available commands
        registry.put("ADD_MOVIE", new AddMovieCommand());
        registry.put("LIST_SUBTREE", new ListSubtreeCommand());
        registry.put("LIST_ALL", new ListAllCommand());
        registry.put("RECOMMEND", new RecommendCommand());
        registry.put("RATE_MOVIE", new RateMovieCommand());
        registry.put("HELP", new HelpCommand());
    }

    // Execute a command
    public String invoke(String input, String role) {
        // Check if input is empty
        if (input == null || input.trim().isEmpty()){
            return "ERROR: Empty command";
        }

        // Split input into parts
        String[] parts = input.trim().split("\\s+"); // Split by whitespace
        String key = parts[0].toUpperCase();

        // Find the command
        Command cmd = registry.get(key);
        if (cmd == null){
            return "ERROR: Unknown Command";
        }

        // Check if user is logged in
        if ("GUEST".equals(role)) {
            return "ERROR: Please LOGIN first.";
        }

        // Execute the command
        try {
            return cmd.execute(service, parts, role);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}